package com.packagename.myapp.spring.ui;

import com.packagename.myapp.spring.dto.SeuvDaoImpl;
import com.packagename.myapp.spring.entity.LogShpiAction;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Route("shpi")
@SpringComponent
public class ShpiHistoryLayout extends VerticalLayout {

    private Grid<LogShpiAction> shpiActionGrid = new Grid<>();
    TextField shpiTextField = new TextField();

    @Autowired
    private SeuvDaoImpl seuvDao;

    public ShpiHistoryLayout() {
        setSizeFull();
        initHeader();
        initShpiLayout();
        initTable();
        initBottomButtons();
    }

    private void initBottomButtons() {
        Button menuButton = new Button("Menu");
        menuButton.addClickListener(click -> {
           menuButton.getUI().ifPresent(ui -> ui.navigate(""));
        });
        add(menuButton);
    }

    private void initTable() {
        shpiActionGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        shpiActionGrid.addColumn(LogShpiAction::getCodeShpi).setHeader("Код ШПИ").setWidth("110px");
        shpiActionGrid.addColumn(new LocalDateTimeRenderer<>(LogShpiAction::getCreateDate,
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT,
                        FormatStyle.MEDIUM))).setHeader("Время события").setWidth("150px");
        shpiActionGrid.addColumn(LogShpiAction::getSystemId).setHeader("Система").setWidth("50px");
        shpiActionGrid.addColumn(LogShpiAction::getServerId).setHeader("Сервер").setWidth("50px");
        shpiActionGrid.addColumn(LogShpiAction::getActionName).setHeader("Событие").setWidth("280px");
        shpiActionGrid.addColumn(new ComponentRenderer<>(action -> {
            if (action.getStatus()) {
                return new Icon(VaadinIcon.CHECK);
            } else {
                return new Icon(VaadinIcon.CLOSE);
            }
        })).setHeader("Статус").setWidth("50px");
        shpiActionGrid.addColumn(LogShpiAction::getDescription).setHeader("Описание").setWidth("800px");
        shpiActionGrid.setWidth("5d");
        add(shpiActionGrid);
    }

    private void initHeader() {
        Label headerLabel = new Label("Track actions with SHPI");
        add(headerLabel);
    }

    private void initShpiLayout() {
        Label label = new Label("Enter shpi code");
        Button searchButton = new Button("Search");
        searchButton.addClickListener(click -> {
            searchByShpi(shpiTextField.getValue());
        });
        HorizontalLayout shpiLayout = new HorizontalLayout();
        shpiLayout.add(label, shpiTextField, searchButton);
        add(shpiLayout);
    }

    private void searchByShpi(String value) {
        shpiActionGrid.setItems(seuvDao.getActionList(value));
    }

    public void setShpi(String codeShpi) {
        shpiTextField.setValue(codeShpi);
        searchByShpi(codeShpi);
    }

}
