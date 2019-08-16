package com.packagename.myapp.spring.ui;

import com.packagename.myapp.spring.dto.TaskDao;
import com.packagename.myapp.spring.entity.DataBaseProperties;
import com.vaadin.flow.component.button.Button;
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

    private HorizontalLayout urlLayout = new HorizontalLayout();
    private HorizontalLayout userLayout = new HorizontalLayout();
    private HorizontalLayout passwdLayout = new HorizontalLayout();


    public MainPage() {
        Button buttonContract = new Button("Work with contracts");
        buttonContract.addClickListener(e -> {
            buttonContract.getUI().ifPresent(ui -> ui.navigate("contract"));
        });
        add(buttonContract);
        Button contractChangeParameter = new Button("Editing contract data");
        contractChangeParameter.addClickListener(e -> {
            buttonContract.getUI().ifPresent(ui -> ui.navigate("contractParam"));
        });
        add(contractChangeParameter);
        Button contractChangeSign = new Button("Change contract sign");
        contractChangeSign.addClickListener(e -> {
            buttonContract.getUI().ifPresent(ui -> ui.navigate("contractSign"));
        });
        add(contractChangeSign);
        Button buttonRegional = new Button("Work with regional distributors");
        buttonRegional.addClickListener(e -> {
            buttonRegional.getUI().ifPresent(ui -> ui.navigate("region"));
        });
        add(buttonRegional);
        initDataBaseOption();
    }

    private void initDataBaseOption() {

        TextField urlText = new TextField();
        urlText.setWidth("350px");
        TextField userText = new TextField();
        TextField passwdText = new TextField();
        urlLayout.add(new Label("DataBase Url"), urlText);
        userLayout.add(new Label("DataBase User"), userText);
        passwdLayout.add(new Label("DataBase Password"), passwdText);
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
        add(urlLayout, userLayout, passwdLayout, btnLayout);
    }

}
