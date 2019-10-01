package com.packagename.myapp.spring.ui;

import com.packagename.myapp.spring.dto.TaskDao;
import com.packagename.myapp.spring.entity.EntityFromTable;
import com.packagename.myapp.spring.service.ExcelParserService;
import com.packagename.myapp.spring.service.SQLGeneratorService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Route("contract")
public class ContractView extends VerticalLayout {

    @Autowired
    private ExcelParserService excelParserService;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private static SQLGeneratorService sqlGeneratorService = new SQLGeneratorService();

    private List<EntityFromTable> listOfEntity = new ArrayList<>();
    private RadioButtonGroup<String> group = new RadioButtonGroup<>();
    private Button doIt = new Button("Execute");
    private TextArea area = new TextArea("Generated SQL");
    private TextArea selectArea = new TextArea("Select request");
    private Button createSqlFile = new Button("Save sql file");
    private Button createSelectFile = new Button("Save select");
    private TextField hidField = new TextField("hid");
    private TextField docNumberField = new TextField("docNumber");
    private TextField yearField = new TextField("year");
    private TextField halfField = new TextField("half");
    private TextField rowStart = new TextField("rowStart");
    private TextField payerField = new TextField("payer");

    public ContractView() {
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initHeader() {
        setWidth("100%");
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSizeFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        Label nameLabel = new Label("Putting/removal of the sign of payment UVPS");
        nameLabel.setSizeFull();
        headerLayout.add(nameLabel);
        add(headerLayout);
    }

    private void initOtherComponents() {
        area.setSizeFull();
        selectArea.setSizeFull();
        Anchor download = new Anchor(new StreamResource("update.sql", () -> createResource(area.getValue().getBytes())), "");
        Anchor getSelect = new Anchor(new StreamResource("select.sql", () -> createResource(selectArea.getValue().getBytes())), "");
        download.getElement().setAttribute("download", true);
        getSelect.getElement().setAttribute("getSelect", true);
        download.add(createSqlFile);
        getSelect.add(createSelectFile);
        add(selectArea, getSelect, area, download);
    }

    private InputStream createResource(byte[] bytes) {
        InputStream targetStream = new ByteArrayInputStream(bytes);
        return targetStream;
    }

    private void initFileUpload() {
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSizeFull();
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                    event.getFileName(), buffer.getInputStream());
            Object output = null;
            showOutput(event.getFileName(), component, output);
            if (!listOfEntity.isEmpty()) {
                selectArea.setValue(sqlGeneratorService.generateSqlForContractGetQuery(listOfEntity));
            } else  selectArea.clear();
        });
        upload.setWidthFull();
        HorizontalLayout uploadButtonLayout = new HorizontalLayout(hidField, docNumberField, yearField, halfField,
                payerField,rowStart);
        VerticalLayout uploadLayout = new VerticalLayout(upload, uploadButtonLayout);
        uploadLayout.setSizeFull();
        headerLayout.add(uploadLayout);
        doIt.addClickListener(d -> {
            if (!listOfEntity.isEmpty()) {
                if (group.getValue() == "Плательщик АУП") {
                    area.setValue(taskDao.getContractFromBDAUP(listOfEntity));
                } else area.setValue(taskDao.getContractFromBDUFPS(listOfEntity));
                createSqlFile.setEnabled(true);
            }
        });
        doIt.setWidthFull();
        group.setItems("Плательщик АУП", "Плательщик Филиал");
        group.addValueChangeListener(event -> {
            if(event.getValue() != null) doIt.setEnabled(true);
        });
        VerticalLayout verticalLayout = new VerticalLayout(group, doIt);
        setElementUnActive();
        verticalLayout.setWidthFull();
        headerLayout.add(verticalLayout);
        add(headerLayout);
    }

    private void setElementUnActive() {
        group.clear();
        area.clear();
        selectArea.clear();
        group.setEnabled(false);
        doIt.setEnabled(false);
        createSqlFile.setEnabled(false);
    }

    private void showOutput(String fileName, Component component, Object output) {

    }

    private Component createComponent(String mimeType, String fileName, InputStream inputStream) {
        try {
            setElementUnActive();
            listOfEntity = excelParserService.readFromExcel(inputStream, hidField.getValue(), docNumberField.getValue(),
                    yearField.getValue(), halfField.getValue(), payerField.getValue(), rowStart.getValue());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        group.setEnabled(true);
        return null;
    }
}
