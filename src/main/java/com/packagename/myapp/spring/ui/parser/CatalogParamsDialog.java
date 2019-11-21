package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Catalog;
import com.packagename.myapp.spring.entity.parser.newFormat.SubsVariant;
import com.packagename.myapp.spring.entity.parser.newFormat.SubsVersion;
import com.packagename.myapp.spring.entity.parser.newFormat.Term;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class CatalogParamsDialog extends Dialog {

    private Grid<Term> termGrid = new Grid<>();
    private Grid<SubsVersion> subsVersionGrid = new Grid<>();
    private Grid<SubsVariant> subsVariantGrid = new Grid<>();

    public CatalogParamsDialog() {
        setSizeFull();
        initAllGrid();
        VerticalLayout mainLayout = new VerticalLayout(termGrid, subsVersionGrid, subsVariantGrid);
        mainLayout.setSizeFull();
        add(mainLayout);
    }

    private void initAllGrid() {
        setWidth("1000px");
        setHeight("700px");
        termGrid.setSizeFull();
        subsVariantGrid.setSizeFull();
        subsVersionGrid.setSizeFull();
        termGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        subsVersionGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        subsVariantGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        termGrid.addColumn(Term::getMonth).setHeader("Month");
        termGrid.addColumn(Term::getDate).setHeader("Date");
        subsVariantGrid.addColumn(SubsVariant::getAcceptId).setHeader("Accept id");
        subsVariantGrid.addColumn(SubsVariant::getRegionsToString).setHeader("Regions");
        subsVariantGrid.addColumn(SubsVariant::getMsp).setHeader("Msp");
        subsVariantGrid.addColumn(SubsVariant::getPrice).setHeader("Price");
        subsVariantGrid.addColumn(SubsVariant::getVatId).setHeader("Vat id");
        subsVariantGrid.addColumn(SubsVariant::getState).setHeader("State");
        subsVersionGrid.addColumn(SubsVersion::getPublicationId).setHeader("Publication Id");
        subsVersionGrid.addColumn(SubsVersion::getPublVerionId).setHeader("Publverion Id");
    }

    public void buildDialog(Catalog catalog) {
        if (catalog.getTerm() != null) {
            termGrid.setItems(catalog.getTerm());
        }
        if (catalog.getSubsVersion() != null) {
            subsVersionGrid.setItems(catalog.getSubsVersion());
        }
        if (catalog.getSubsVariant() != null) {
            subsVariantGrid.setItems(catalog.getSubsVariant());
        }
    }

}
