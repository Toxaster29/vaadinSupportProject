package com.packagename.myapp.spring.ui.parser;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("parser")
public class FileParseLayout extends VerticalLayout {

    public FileParseLayout() {
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initOtherComponents() {

    }

    private void initFileUpload() {

    }

    private void initHeader() {
        Label headLabel = new Label("Parse old standard files");
        add(headLabel);
    }
}
