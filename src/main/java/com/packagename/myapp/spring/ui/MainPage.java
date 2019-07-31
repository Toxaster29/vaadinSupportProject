package com.packagename.myapp.spring.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;

@Route("")
@PWA(name = "Project for support this thing", shortName = "ProdSupport")
public class MainPage extends VerticalLayout {

    public MainPage() {
        Button buttonContract = new Button("Work with contracts");
        buttonContract.addClickListener(e -> {
            buttonContract.getUI().ifPresent(ui -> ui.navigate("contract"));
        });
        add(buttonContract);
        Button buttonRegional = new Button("Work with regional distributors");
        buttonRegional.addClickListener(e -> {
            buttonRegional.getUI().ifPresent(ui -> ui.navigate("region"));
        });
        add(buttonRegional);
    }
}
