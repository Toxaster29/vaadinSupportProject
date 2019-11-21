package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Catalog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UIScope
public class CatalogLayout extends VerticalLayout {

    private Grid<Catalog> catalogGrid = new Grid<>();

    private CatalogParamsDialog catalogParamsDialog;

    public CatalogLayout(@Autowired CatalogParamsDialog catalogParamsDialog) {
        this.catalogParamsDialog = catalogParamsDialog;
        setSizeFull();
        catalogGrid.setSizeFull();
        catalogGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        catalogGrid.addColumn(Catalog::getIndex).setHeader("Index");
        catalogGrid.addColumn(Catalog::getName).setHeader("Name");
        catalogGrid.addColumn(Catalog::getComment).setHeader("Comment");
        catalogGrid.addColumn(Catalog::getAgencyId).setHeader("Agency Id");
        catalogGrid.addColumn(Catalog::getDistributionId).setHeader("Distribution Id");
        catalogGrid.addColumn(Catalog::getExpeditionId).setHeader("Expedition Id");
        catalogGrid.addColumn(Catalog::getClientId).setHeader("Client Id");
        catalogGrid.addColumn(Catalog::getCellophane).setHeader("Cellophane");
        catalogGrid.addItemDoubleClickListener(click -> {
           if (click.getItem() != null) {
               catalogParamsDialog.buildDialog(click.getItem());
               catalogParamsDialog.open();
           }
        });
        add(catalogGrid);
    }

    public void buildLayout(List<Catalog> catalogList) {
        catalogGrid.setItems(catalogList);
    }
}
