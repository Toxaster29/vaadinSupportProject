package com.packagename.myapp.spring.ui;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("contractParam")
public class contractChangeParamView extends VerticalLayout {

    public contractChangeParamView() {
        add(new Label("Change Param"));
    }

}
