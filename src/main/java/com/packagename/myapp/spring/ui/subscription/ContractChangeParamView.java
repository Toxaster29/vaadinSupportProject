package com.packagename.myapp.spring.ui.subscription;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("contractParam")
public class ContractChangeParamView extends VerticalLayout {

    public ContractChangeParamView() {
        add(new Label("Change Param"));
    }

}
