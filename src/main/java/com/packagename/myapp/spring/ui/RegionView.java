package com.packagename.myapp.spring.ui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("region")
public class RegionView extends VerticalLayout {

    public RegionView() {

        initHeader();

    }

    private void initHeader() {

        Label headLabel = new Label("Filling regional distributors");
        add(headLabel);

    }
}
