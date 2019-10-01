package com.packagename.myapp.spring.ui.euv;

import com.packagename.myapp.spring.dto.SeuvDao;
import com.packagename.myapp.spring.entity.euv.ShpiTableEntity;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route("shpiSearch")
public class ShpiSearchLayout extends VerticalLayout {

    @Autowired
    private SeuvDao seuvDao;
    @Autowired
    private ShpiHistoryLayout historyLayout;

    private Grid<ShpiTableEntity> entityGrid = new Grid<>();
    private Checkbox onlyProblem = new Checkbox("Имеют проблемы");
    private List<ShpiTableEntity> tableEntityList = new ArrayList<>();

    public ShpiSearchLayout() {
        setSizeFull();
        initHeader();
        initSearchPanel();
        initResultTable();
        initBottomButtons();
    }

    private void initResultTable() {
        entityGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        entityGrid.addColumn(ShpiTableEntity::getCodeShpi).setHeader("Код ШПИ");
        entityGrid.addColumn(ShpiTableEntity::getActionCount).setHeader("Количество событий");
        entityGrid.addColumn(new ComponentRenderer<>(entity -> {
            if (!entity.getHaveProblem()) {
                return new Icon(VaadinIcon.CHECK);
            } else {
                return new Icon(VaadinIcon.CLOSE);
            }
        })).setHeader("Статус");
        entityGrid.setWidth("5d");
        entityGrid.addItemDoubleClickListener(event -> {
            Dialog dialog = new Dialog();
            historyLayout.setShpi(event.getItem().getCodeShpi());
            dialog.setHeight("calc(100vh - (2*var(--lumo-space-m)))");
            dialog.setWidth("calc(100vw - (4*var(--lumo-space-m)))");
            dialog.add(historyLayout);
            dialog.open();
        });
        add(entityGrid);
    }

    private void initSearchPanel() {
        Label dateLabel = new Label("Укажите интервал:");
        DatePicker startDate = new DatePicker();
        startDate.setValue(LocalDate.now());
        DatePicker endDate = new DatePicker();
        endDate.setValue(LocalDate.now());
        Checkbox euvCheckBox = new Checkbox("Система EUV");
        Checkbox eoCheckBox = new Checkbox("Система EO");
        Button searchButton = new Button("Search");
        Notification notification = new Notification("Изменение фильтрации данных", 2000, Notification.Position.TOP_END);
        searchButton.addClickListener(click -> {
           searchByParams(startDate.getValue(), endDate.getValue(), euvCheckBox.getValue(), eoCheckBox.getValue());
        });
        onlyProblem.addValueChangeListener(event -> {
           loadData();
           notification.open();
        });
        HorizontalLayout dateLayout = new HorizontalLayout(dateLabel, new Label("С"), startDate, new Label("По"),
                endDate, euvCheckBox, eoCheckBox, onlyProblem, searchButton);
        add(dateLayout);
    }

    private void searchByParams(LocalDate start, LocalDate end, Boolean euv, Boolean eo) {
       tableEntityList = seuvDao.searchShpiByParams(start, end, euv, eo);
       loadData();
    }

    private void loadData() {
        if(onlyProblem.getValue()) {
            entityGrid.setItems(tableEntityList.stream().filter(c -> c.getHaveProblem()));
        } else entityGrid.setItems(tableEntityList);
    }

    private void initHeader() {
        Label headerLabel = new Label("Search shpi here");
        add(headerLabel);
    }

    private void initBottomButtons() {
        Button menuButton = new Button("Menu");
        menuButton.addClickListener(click -> {
            menuButton.getUI().ifPresent(ui -> ui.navigate(""));
        });
        add(menuButton);
    }
}
