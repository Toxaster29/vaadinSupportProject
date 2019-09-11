package com.packagename.myapp.spring.ui;

import com.packagename.myapp.spring.dto.TaskDao;
import com.packagename.myapp.spring.entity.DataBaseProperties;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@PWA(name = "Project for support this thing", shortName = "ProdSupport")
public class MainPage extends VerticalLayout {

    @Autowired
    private TaskDao taskDao;

    public MainPage() {
        initSubscriptionPanel();
        initSeuvPanel();
        initDataBaseOption();
    }

    private void initSeuvPanel() {
        Button shpiHistoryButton = new Button("Shpi history");
        shpiHistoryButton.addClickListener(e -> {
            shpiHistoryButton.getUI().ifPresent(ui -> ui.navigate("shpi"));
        });
        Button shpiStatisticButton = new Button("Shpi statistic");
        shpiStatisticButton.addClickListener(click -> {
            shpiStatisticButton.getUI().ifPresent(ui -> ui.navigate("shpiStat"));
        });
        Button shpiSearchButton = new Button("Search shpi");
        shpiSearchButton.addClickListener(cklick -> {
           shpiSearchButton.getUI().ifPresent(ui -> ui.navigate("shpiSearch"));
        });
        VerticalLayout seuvLayout = new VerticalLayout(new Label("Seuv"));
        HorizontalLayout buttonLayout = new HorizontalLayout(shpiHistoryButton, shpiStatisticButton, shpiSearchButton);
        seuvLayout.add(buttonLayout);
        add(seuvLayout);
    }

    private void initSubscriptionPanel() {
        Button buttonContract = new Button("Work with contracts");
        buttonContract.addClickListener(e -> {
            buttonContract.getUI().ifPresent(ui -> ui.navigate("contract"));
        });
        Button contractChangeParameter = new Button("Editing contract data");
        contractChangeParameter.addClickListener(e -> {
            buttonContract.getUI().ifPresent(ui -> ui.navigate("contractParam"));
        });
        Button contractChangeSign = new Button("Change contract sign");
        contractChangeSign.addClickListener(e -> {
            buttonContract.getUI().ifPresent(ui -> ui.navigate("contractSign"));
        });
        Button buttonRegional = new Button("Work with regional distributors");
        buttonRegional.addClickListener(e -> {
            buttonRegional.getUI().ifPresent(ui -> ui.navigate("region"));
        });
        VerticalLayout subscriptionLayout = new VerticalLayout(new Label("Subscription"));
        HorizontalLayout inlineButtons = new HorizontalLayout(buttonContract, contractChangeParameter, contractChangeSign, buttonRegional);
        subscriptionLayout.add(inlineButtons);
        add(subscriptionLayout);
    }

    private void initDataBaseOption() {
        TextField urlText = new TextField();
        urlText.setWidth("350px");
        TextField userText = new TextField();
        TextField passwdText = new TextField();
        HorizontalLayout urlLayout = new HorizontalLayout(new Label("DataBase Url"), urlText);
        HorizontalLayout userLayout = new HorizontalLayout(new Label("DataBase User"), userText);
        HorizontalLayout passwdLayout = new HorizontalLayout(new Label("DataBase Password"), passwdText);
        Button confirmButton = new Button("Change db options");
        confirmButton.addClickListener(click -> {
            if (!urlText.getValue().isEmpty() && !userText.getValue().isEmpty() && !passwdText.getValue().isEmpty()) {
                taskDao.setNewProperties(urlText.getValue(), userText.getValue(), passwdText.getValue());
            }
        });
        Button getCurrentOptions = new Button("Get current db options");
        getCurrentOptions.addClickListener(click -> {
            DataBaseProperties properties = taskDao.getDBProperties();
            urlText.setValue(properties.getUrl());
            userText.setValue(properties.getUser());
            passwdText.setValue(properties.getPasswd());
        });
        HorizontalLayout btnLayout = new HorizontalLayout(getCurrentOptions, confirmButton);
        Checkbox checkbox = new Checkbox();
        checkbox.setValue(true);
        checkbox.addValueChangeListener(event -> {
            Boolean enable = event.getValue();
            urlLayout.setEnabled(enable);
            userLayout.setEnabled(enable);
            passwdLayout.setEnabled(enable);
            btnLayout.setEnabled(enable);
        });
        checkbox.setValue(false);
        HorizontalLayout enableLayout = new HorizontalLayout(new Label("Enable db options?"), checkbox);
        VerticalLayout dataBaseLayout = new VerticalLayout(enableLayout, urlLayout, userLayout, passwdLayout, btnLayout);
        add(dataBaseLayout);
    }

}
