package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Agency;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UIScope
public class AgencyLayout extends VerticalLayout {

    private Grid<Agency> agencyGrid = new Grid<>();

    public AgencyLayout() {
        setSizeFull();
        initGrid();
    }

    private void initGrid() {
        agencyGrid.setSizeFull();
        agencyGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        agencyGrid.addColumn(Agency::getId).setHeader("Id");
        agencyGrid.addColumn(Agency::getSupplyid).setHeader("Supply Id");
        agencyGrid.addColumn(Agency::getName).setHeader("Name");
        agencyGrid.addColumn(Agency::getInn).setHeader("Inn");
        agencyGrid.addColumn(Agency::getEmail).setHeader("Email");
        agencyGrid.addColumn(Agency::getPhone).setHeader("Phone");
        add(agencyGrid);
    }

    public void buildLayout(List<Agency> agencyList) {
        agencyGrid.setItems(agencyList);
    }

}
