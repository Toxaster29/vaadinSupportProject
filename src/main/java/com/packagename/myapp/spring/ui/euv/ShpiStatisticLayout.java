package com.packagename.myapp.spring.ui.euv;

import com.packagename.myapp.spring.dto.SeuvDaoImpl;
import com.packagename.myapp.spring.entity.euv.EuvStatisticEntity;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("shpiStat")
public class ShpiStatisticLayout extends VerticalLayout {

    @Autowired
    private SeuvDaoImpl seuvDao;

    private TextField euvCount = new TextField("EUV операции");
    private TextField eoCount = new TextField("EO операции");
    private TextField loginCount = new TextField("Количество авторзаций");

    public ShpiStatisticLayout() {
        initHeader();
        initStatisticLine();
        initStatisticTable();
        initBottomButtons();
    }

    private void initStatisticTable() {

    }

    private void initBottomButtons() {
        Button menuButton = new Button("Menu");
        menuButton.addClickListener(click -> {
            menuButton.getUI().ifPresent(ui -> ui.navigate(""));
        });
        add(menuButton);
    }

    private void initStatisticLine() {
        HorizontalLayout statisticLayout = new HorizontalLayout(euvCount, eoCount, loginCount);
        add(statisticLayout);
    }

    private void initHeader() {
        Label headerLabel = new Label("SHPI statistic here");
        Button updateButton = new Button("Update");
        updateButton.addClickListener(click -> {
            updateStatisticData();
        });
        HorizontalLayout headerLayout = new HorizontalLayout(headerLabel, updateButton);
        add(headerLayout);
    }

    private void updateStatisticData() {
        EuvStatisticEntity statisticEntity = seuvDao.getStatistic();
        euvCount.setValue(statisticEntity.getEuvOperationCount().toString());
        eoCount.setValue(statisticEntity.getEoOperationCount().toString());
        loginCount.setValue(statisticEntity.getUserWebFromLoginCount().toString());
    }
}
