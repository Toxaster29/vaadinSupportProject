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
        Button contractChangeParameter = new Button("Review contract data");
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
        Button parseFileButton = new Button("Parse file");
        parseFileButton.addClickListener(click -> {
           parseFileButton.getUI().ifPresent(ui -> ui.navigate("parserView"));
        });
        Button createReportButton = new Button("Reports");
        createReportButton.addClickListener(click -> {
            createReportButton.getUI().ifPresent(ui -> ui.navigate("report"));
        });
        Button scheduleButton = new Button("Schedule");
        scheduleButton.addClickListener(click -> {
            scheduleButton.getUI().ifPresent(ui -> ui.navigate("schedule"));
        });
        VerticalLayout subscriptionLayout = new VerticalLayout(new Label("Subscription"));
        HorizontalLayout inlineButtons = new HorizontalLayout(buttonContract, contractChangeParameter, contractChangeSign,
                buttonRegional, parseFileButton, createReportButton, scheduleButton);
        Button magazineButton = new Button("Magazine");
        magazineButton.addClickListener(click -> {
            magazineButton.getUI().ifPresent(ui -> ui.navigate("magazine"));
        });
        Button avatarButton = new Button("Avatar");
        avatarButton.addClickListener(click -> {
            avatarButton.getUI().ifPresent(ui -> ui.navigate("avatar"));
        });
        Button excelParserButton = new Button("Excel parser");
        excelParserButton.addClickListener(click -> {
           excelParserButton.getUI().ifPresent(ui -> ui.navigate("excelParser"));
        });
        Button cstButton = new Button("CST");
        cstButton.addClickListener(click -> {
            cstButton.getUI().ifPresent(ui -> ui.navigate("cst"));
        });
        Button dbInsertButton = new Button("Db insert");
        dbInsertButton.addClickListener(click -> {
           dbInsertButton.getUI().ifPresent(ui -> ui.navigate("insert"));
        });
        Button ufpsButton = new Button("Ufps");
        ufpsButton.addClickListener(click -> {
           ufpsButton.getUI().ifPresent(ui -> ui.navigate("ufps"));
        });
        Button annulmentButton = new Button("Annulment");
        annulmentButton.addClickListener(click -> {
            annulmentButton.getUI().ifPresent(ui -> ui.navigate("annulment"));
        });
        Button kindnessTree = new Button("Kindness Tree");
        kindnessTree.addClickListener(click -> {
            kindnessTree.getUI().ifPresent(ui -> ui.navigate("kindness"));
        });
        HorizontalLayout secondLineButtons = new HorizontalLayout(magazineButton, avatarButton, excelParserButton, cstButton,
                dbInsertButton, ufpsButton, annulmentButton, kindnessTree);
        subscriptionLayout.add(inlineButtons, secondLineButtons);
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
