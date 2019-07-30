package com.packagename.myapp.spring.ui;

import com.packagename.myapp.spring.dto.TaskDao;
import com.packagename.myapp.spring.entity.EntityFromTable;
import com.packagename.myapp.spring.service.ExcelParserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Route
@PWA(name = "Project for support this thing", shortName = "ProdSupport")
public class MainView extends VerticalLayout {

    @Autowired
    private ExcelParserService excelParserService;
    @Autowired
    private TaskDao taskDao;

    private List<EntityFromTable> listOfEntity = new ArrayList<>();
    private RadioButtonGroup<String> group = new RadioButtonGroup<>();
    private Button doIt = new Button("Выполнить");

    public MainView() {
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initHeader() {
        setWidth("100%");
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSizeFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        Label nameLabel = new Label("Prod Support");
        nameLabel.setSizeFull();
        headerLayout.add(nameLabel);
        add(headerLayout);
    }

    private void initOtherComponents() {

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
                    taskDao.getContractFromBDAUP(listOfEntity);
                } else taskDao.getContractFromBDUFPS(listOfEntity);
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
        group.setEnabled(false);
        doIt.setEnabled(false);
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
