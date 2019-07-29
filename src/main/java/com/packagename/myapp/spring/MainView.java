package com.packagename.myapp.spring;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

import java.io.IOException;
import java.io.InputStream;

@Route
@PWA(name = "Project Base for Vaadin Flow with Spring", shortName = "Project Base")
public class MainView extends VerticalLayout {

    @Autowired
    private ExcelParserService excelParserService;

    public MainView() {
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initHeader() {
        setWidthFull();
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setSizeFull();
        headerLayout.setAlignItems(Alignment.CENTER);
        headerLayout.add(new Label("Prod Support Test"));
        add(headerLayout);
    }

    private void initOtherComponents() {

    }


    private void initFileUpload() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                    event.getFileName(), buffer.getInputStream());
            Object output = null;
            showOutput(event.getFileName(), component, output);
        });
        add(upload);
    }

    private void showOutput(String fileName, Component component, Object output) {

    }

    private Component createComponent(String mimeType, String fileName, InputStream inputStream) {
        try {
            excelParserService.readFromExcel(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
