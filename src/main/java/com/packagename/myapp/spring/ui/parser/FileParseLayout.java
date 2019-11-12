package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Format;
import com.packagename.myapp.spring.entity.parser.oldFormat.*;
import com.packagename.myapp.spring.service.DocumentParseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Route("parser")
public class FileParseLayout extends VerticalLayout {

    private DocumentParseService parseService;

    private ListBox<String> captionsFromFile = new ListBox<>();
    private String selectedCaption = "";

    public FileParseLayout(@Autowired DocumentParseService parseService) {
        this.parseService = parseService;
        setSizeFull();
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initOtherComponents() {

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
        ComboBox acceptSelect = new ComboBox("Accept");
        yearField.setWidthFull();
        halfYearField.setWidthFull();
        acceptSelect.setWidthFull();
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
        VerticalLayout uploadLayout = new VerticalLayout(upload, readData);
        uploadLayout.setSizeFull();
        VerticalLayout captionLayout = new VerticalLayout(captionsFromFile, getDateButton);
        captionLayout.setSizeFull();
        VerticalLayout campaignLayout = new VerticalLayout(yearField, halfYearField, acceptSelect);
        campaignLayout.setSizeFull();
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

    private void parseFileData(List<String> lineList) {
        final String[] sender = {""};
        List<SPublication> publications = new ArrayList<>();
        List<SIndex> indexList = new ArrayList<>();
        List<SPrice> priceList = new ArrayList<>();
        List<SCatalog> catalogList = new ArrayList<>();
        List<SCountIn> countList = new ArrayList<>();
        List<SArea> areaList = new ArrayList<>();
        List<SAgency> agencies = new ArrayList<>();
        List<SDispatch> dispatchList = new ArrayList<>();
        List<STopic> topicList = new ArrayList<>();
        List<STopicIn> topicInList = new ArrayList<>();
        List<String> captionList = new ArrayList<>();
        lineList.stream().forEach(line -> {
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
        Format endJson = new Format();
        endJson.setSender(parseService.parseLine(sender[0]).get(0));
        endJson.setDate(LocalDate.now());
        endJson.setVersion((byte) 1);
        parseService.fillDictionaryData(endJson);
        parseService.fillAgencyParams(agencies, endJson);
        parseService.fillCampaignParams(publications, endJson, topicList, topicInList, indexList, priceList);
        System.out.println("Complete");
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
