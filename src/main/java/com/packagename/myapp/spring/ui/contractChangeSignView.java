package com.packagename.myapp.spring.ui;

import com.packagename.myapp.spring.dto.TaskDao;
import com.packagename.myapp.spring.entity.TableMainData;
import com.packagename.myapp.spring.service.ExcelParserService;
import com.packagename.myapp.spring.service.SQLGeneratorService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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
import java.util.stream.Collectors;

@Route("contractSign")
public class contractChangeSignView extends VerticalLayout {

    @Autowired
    private ExcelParserService excelParserService;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private SQLGeneratorService sqlService;

    private Button doIt = new Button("Execute");
    private TextArea area = new TextArea("Generated SQL");
    private Button createSqlFile = new Button("Save sql file");
    private List<TableMainData> listOfEntity = new ArrayList<>();

    public contractChangeSignView() {
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initOtherComponents() {
        area.setSizeFull();
        Anchor download = new Anchor(new StreamResource("filename.sql", () -> createResource()), "");
        download.getElement().setAttribute("download", true);
        download.add(createSqlFile);
        add(area, download);
    }

    private InputStream createResource() {
        InputStream targetStream = new ByteArrayInputStream(area.getValue().getBytes());
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
        });
        upload.setWidthFull();
        headerLayout.add(upload);
        doIt.addClickListener(d -> {
            if (!listOfEntity.isEmpty()) {
                /*area.setValue(sqlService.generateSqlForContract(listOfEntity.stream()
                        .filter(p -> p.getFirstPeriod().equals("да")).collect(Collectors.toList()), 2019, 2));*/
                area.setValue(sqlService.generateSqlForContract(listOfEntity.stream()
                        .filter(p -> p.getSecondPeriod().equals("да")).collect(Collectors.toList()), 2020, 1));
            }
        });
        doIt.setWidthFull();
        ComboBox<String> comboBox = new ComboBox<String>("Select type");
        comboBox.setEnabled(false);
        VerticalLayout verticalLayout = new VerticalLayout(comboBox ,doIt);
        setElementUnActive();
        verticalLayout.setWidthFull();
        headerLayout.add(verticalLayout);
        add(headerLayout);
    }

    private void showOutput(String fileName, Component component, Object output) {
    }

    private Component createComponent(String mimeType, String fileName, InputStream inputStream) {
        try {
            setElementUnActive();
            listOfEntity = excelParserService.readFromExcelSecond(inputStream);
            doIt.setEnabled(true);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            setElementUnActive();
        }
        return null;
    }

    private void initHeader() {
        setWidth("100%");
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSizeFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        Label nameLabel = new Label("Change contract sign");
        nameLabel.setSizeFull();
        headerLayout.add(nameLabel);
        add(headerLayout);
    }

    private void setElementUnActive() {
        area.clear();
        doIt.setEnabled(false);
        createSqlFile.setEnabled(false);
    }
}
