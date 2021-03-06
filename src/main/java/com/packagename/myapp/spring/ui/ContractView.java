package com.packagename.myapp.spring.ui;

import com.packagename.myapp.spring.dto.TaskDao;
import com.packagename.myapp.spring.entity.EntityFromTable;
import com.packagename.myapp.spring.service.ExcelParserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
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

@Route("contract")
public class ContractView extends VerticalLayout {

    @Autowired
    private ExcelParserService excelParserService;
    @Autowired
    private TaskDao taskDao;

    private List<EntityFromTable> listOfEntity = new ArrayList<>();
    private RadioButtonGroup<String> group = new RadioButtonGroup<>();
    private Button doIt = new Button("Execute");
    private TextArea area = new TextArea("Generated SQL");
    private Button createSqlFile = new Button("Save sql file");
    private TextArea areaProblem = new TextArea("Problem in data fom Excel");

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
        areaProblem.setSizeFull();
        Anchor download = new Anchor(new StreamResource("filename.sql", () -> createResource()), "");
        download.getElement().setAttribute("download", true);
        download.add(createSqlFile);
        add(area, download,areaProblem);
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
                if (group.getValue() == "Плательщик АУП") {
                    area.setValue(taskDao.getContractFromBDAUP(listOfEntity));
                } else area.setValue(taskDao.getContractFromBDUFPS(listOfEntity));
                createSqlFile.setEnabled(true);
                searchProblemsInDocument();
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

    private void searchProblemsInDocument() {
        final String[] problemText = {""};
        listOfEntity.forEach(entity -> {
            if(entity.getHaveProblem() != null){
                if(entity.getHaveProblem()) {
                    problemText[0] += "Id Контрагента: " + entity.getId() + "\t Номер договора: " + entity.getDocNumber()
                            + "\nSQL для нахождения договора:\nselect * from contract where legal_hid =\'"
                            + entity.getId() + "\' and doc_number = \'" + entity.getDocNumber() + "\';";
                }
            }
        });
        if (!problemText[0].isEmpty()) {
            areaProblem.setVisible(true);
            areaProblem.setValue(problemText[0]);
        }
    }

    private void setElementUnActive() {
        group.clear();
        area.clear();
        group.setEnabled(false);
        doIt.setEnabled(false);
        createSqlFile.setEnabled(false);
        areaProblem.clear();
        areaProblem.setVisible(false);
    }

    private void showOutput(String fileName, Component component, Object output) {

    }

    private Component createComponent(String mimeType, String fileName, InputStream inputStream) {
        try {
            setElementUnActive();
            listOfEntity = excelParserService.readFromExcel(inputStream);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        group.setEnabled(true);
        return null;
    }
}
