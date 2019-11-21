package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Accept;
import com.packagename.myapp.spring.entity.parser.newFormat.ConnectivityThematicEntity;
import com.packagename.myapp.spring.entity.parser.newFormat.Directory;
import com.packagename.myapp.spring.entity.parser.newFormat.Format;
import com.packagename.myapp.spring.entity.parser.oldFormat.*;
import com.packagename.myapp.spring.service.DocumentParseService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Route("parserView")
public class FileParseLayout extends VerticalLayout {

    private DocumentParseService parseService;

    private ThematicDialog thematicDialog;
    private NewFormatResultDialog newFormatResultDialog;

    private ListBox<String> captionsFromFile = new ListBox<>();
    private ComboBox<Accept> acceptSelect = new ComboBox("Accept");
    private Grid<ConnectivityThematicEntity> thematicEntityGrid = new Grid<>();

    private final String[] sender = {""};
    private List<SPublication> publications = new ArrayList<>();
    private List<SIndex> indexList = new ArrayList<>();
    private List<SPrice> priceList = new ArrayList<>();
    private List<SAgency> agencies = new ArrayList<>();
    private List<STopic> topicList = new ArrayList<>();
    private List<STopicIn> topicInList = new ArrayList<>();
    private List<SArea> areaList = new ArrayList<>();
    private List<ConnectivityThematicEntity> connectivityThematicEntities = new ArrayList<>();
    private String selectedCaption = "";
    private Format endJson = new Format();

    public FileParseLayout(@Autowired DocumentParseService parseService, @Autowired ThematicDialog thematicDialog,
                           @Autowired NewFormatResultDialog newFormatResultDialog) {
        this.parseService = parseService;
        this.thematicDialog = thematicDialog;
        this.newFormatResultDialog = newFormatResultDialog;
        setSizeFull();
        setSpacing(false);
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initOtherComponents() {
        thematicEntityGrid.setSizeFull();
        thematicEntityGrid.setSelectionMode(Grid.SelectionMode.NONE);
        thematicEntityGrid.addColumn(ConnectivityThematicEntity::getOldId).setHeader("Id").setWidth("80px").setFlexGrow(0);
        thematicEntityGrid.addColumn(ConnectivityThematicEntity::getOldName).setHeader("Name").setFlexGrow(1);
        thematicEntityGrid.addColumn(ConnectivityThematicEntity::getDirectoryName).setHeader("New format thematic name").setFlexGrow(1);
        thematicEntityGrid.addComponentColumn(item -> createEditButton(item)).setHeader("Edit").setWidth("120px").setFlexGrow(0);
        add(thematicEntityGrid);
    }

    private Component createEditButton(ConnectivityThematicEntity item) {
        Button button = new Button("Edit", clickEvent -> {
            thematicDialog.open();
            thematicDialog.buildDialog(item.getOldName() ,endJson.getThematic(), item.getDirectory(), this::refreshGrid);
        });
        return button;
    }

    private void refreshGrid() {
        thematicEntityGrid.getDataProvider().refreshAll();
    }

    private void initFileUpload() {
        Upload upload = new Upload((MultiFileReceiver) (filename, mimeType) -> {
            File file = new File(new File("src\\main\\resources\\uploaded-files"), filename);
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return null;
            }
        });
        upload.setSizeFull();
        Button readData = new Button("Read files data");
        readData.addClickListener(click -> {
            try {
                readAllArchives();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        captionsFromFile.setSizeFull();
        captionsFromFile.addValueChangeListener(change -> {
           selectedCaption = change.getValue();
        });
        TextField yearField = new TextField("Year");
        TextField halfYearField = new TextField("Half");
        yearField.setWidthFull();
        halfYearField.setWidthFull();
        acceptSelect.setWidthFull();
        acceptSelect.setItemLabelGenerator(Accept::getName);
        Button getDateButton = new Button("Set date from caption");
        getDateButton.addClickListener(click -> {
            if (!selectedCaption.isEmpty()) {
                Pattern patternHalf = Pattern.compile("[0-9]{1}");
                Matcher matcherHalf = patternHalf.matcher(selectedCaption);
                if (matcherHalf.find()) {
                    halfYearField.setValue(selectedCaption.substring(matcherHalf.start(), matcherHalf.start() + 1));
                }
                Pattern patternYear = Pattern.compile("[0-9]{4}");
                Matcher matcherYear = patternYear.matcher(selectedCaption);
                if (matcherYear.find()) {
                    yearField.setValue(selectedCaption.substring(matcherYear.start(), matcherYear.end()));
                }
            }
        });
        Button parseDataButton = new Button("Start parse data");
        parseDataButton.addClickListener(click -> {
            generateDataNewFormat(halfYearField.getValue(), yearField.getValue(), acceptSelect.getValue());
        });
        VerticalLayout uploadLayout = new VerticalLayout(upload, readData);
        uploadLayout.setSizeFull();
        VerticalLayout captionLayout = new VerticalLayout(captionsFromFile, getDateButton);
        captionLayout.setSizeFull();
        VerticalLayout campaignLayout = new VerticalLayout(yearField, halfYearField, acceptSelect, parseDataButton);
        campaignLayout.setSizeFull();
        campaignLayout.setSpacing(false);
        HorizontalLayout topLayout = new HorizontalLayout(uploadLayout, captionLayout, campaignLayout);
        topLayout.setWidthFull();
        add(topLayout);
    }

    private void readAllArchives() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get("src\\main\\resources\\uploaded-files"))) {
            paths.filter(Files::isRegularFile).forEach(e -> {
                ZipFile zipFile = null;
                List<String> lineList = new ArrayList<>();
                try {
                    zipFile = new ZipFile(e.toFile());
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while(entries.hasMoreElements()){
                        ZipEntry entry = entries.nextElement();
                        InputStream stream = zipFile.getInputStream(entry);
                        readOldFormatData(stream, lineList);
                    }
                    zipFile.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (!lineList.isEmpty()) {
                    parseFileData(lineList);
                }
                e.toFile().delete();
            });
        }
    }

    private void generateDataNewFormat(String halfYear, String yearFieldValue, Accept acceptSelectValue) {
        endJson.setSender(parseService.parseLine(sender[0]).get(0));
        endJson.setDate(LocalDate.now());
        endJson.setVersion((byte) 1);
        parseService.fillTerrainParams(areaList, endJson);
        //parseService.uploadConnectionThematicToDB(connectivityThematicEntities); //Используется для заполнения связи тематик в бд (весь список) при изменении справочников
        parseService.fillCampaignParams(publications, endJson, connectivityThematicEntities, topicInList, indexList,
                priceList, halfYear, yearFieldValue, acceptSelectValue);
        newFormatResultDialog.open();
        newFormatResultDialog.buildDialog(endJson);
    }

    private void loadDirectoryModeration(List<ConnectivityThematicEntity> connectivityThematicEntities) {
        thematicEntityGrid.setItems(connectivityThematicEntities);
    }

    private List<ConnectivityThematicEntity> prepareDirectoryForTopics(List<STopic> topicList, List<Directory> thematicList) {
        List<ConnectivityThematicEntity> thematicEntityList = new ArrayList<>();
       for (STopic topic : topicList) {
           List<Directory> directories = new ArrayList<>();
           for (Directory theme : thematicList) {
               if (topic.getRubricName().contains(".")) {
                   String[] words = topic.getRubricName().split("\\.");
                   String[] wordsTh = theme.getName().split("\\.");
                   for (String word : words) {
                       for (String wordTh : wordsTh) {
                           if (word.trim().toUpperCase().equals(wordTh.trim().toUpperCase())) {
                               directories.add(theme);
                           }
                       }
                   }
                   directories = new ArrayList<>(new LinkedHashSet<>(directories));
               } else {
                   if (theme.getName().toUpperCase().equals(topic.getRubricName().toUpperCase())) directories.add(theme);
               }
           }
           thematicEntityList.add(new ConnectivityThematicEntity(topic.getRubricId(), topic.getRubricName(), directories));
       }
       return thematicEntityList;
    }

    private void parseFileData(List<String> lineList) {
        List<SCatalog> catalogList = new ArrayList<>();
        List<SCountIn> countList = new ArrayList<>();
        List<SDispatch> dispatchList = new ArrayList<>();
        List<String> captionList = new ArrayList<>();
        lineList.forEach(line -> {
            if (!line.isEmpty()) {
                int point = line.indexOf('(');
                String tagName = line.substring(0, point);
                switch (tagName) {
                    case "S_PUBL":
                        publications.add(new SPublication(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_INDEX":
                        indexList.add(new SIndex(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_PRICE":
                        priceList.add(new SPrice(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_CATALOG":
                        catalogList.add(new SCatalog(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_CONT_IN":
                        countList.add(new SCountIn(parseService.parseLine(line.substring(point))));
                        break;
                    case "C_FROM":
                        sender[0] = line.substring(point);
                        break;
                    case "S_AREA":
                        areaList.add(new SArea(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_AGENCY":
                        agencies.add(new SAgency(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_DISPATCH":
                        dispatchList.add(new SDispatch(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_TOPIC":
                        topicList.add(new STopic(parseService.parseLine(line.substring(point))));
                        break;
                    case "S_TOPC_IN":
                        topicInList.add(new STopicIn(parseService.parseLine(line.substring(point))));
                        break;
                    case "I_FCAPTION":
                        captionList.add(line.substring(point));
                        break;
                }
            }
        });
        setCaptionsFromFileOnLayout(captionList);
        acceptSelect.setItems(parseService.getAcceptList());
        parseService.fillDictionaryData(endJson);
        parseService.fillAgencyParams(agencies, endJson);
        List<ConnectivityThematicEntity> connectivityThematicFromDB = parseService
                .getConnectivityThematicFromDataBase(endJson.getThematic());
        if (connectivityThematicFromDB.isEmpty()) {
            connectivityThematicEntities = prepareDirectoryForTopics(topicList, endJson.getThematic());
        } else {
            connectivityThematicEntities = connectivityThematicFromDB;
        }
        loadDirectoryModeration(connectivityThematicEntities);
    }

    private void setCaptionsFromFileOnLayout(List<String> captionList) {
        captionsFromFile.setItems(captionList.stream().map(caption -> caption
                .substring(caption.indexOf(")") + 1)).collect(Collectors.toList()));
    }

    private void readOldFormatData(InputStream stream, List<String> lineList) {
        InputStreamReader isr = new InputStreamReader(stream, Charset.forName("cp1251"));
        BufferedReader br = new BufferedReader(isr);
        br.lines().forEach(line -> {
            lineList.add(line);
        });
    }

    private void initHeader() {
        Label headLabel = new Label("Parse old standard files");
        add(headLabel);
    }
}
