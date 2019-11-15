package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Publication;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UIScope
public class PublicationsLayout extends VerticalLayout {

    private Grid<Publication> publicationGrid = new Grid<>();

    public PublicationsLayout() {
        setSizeFull();
        initGrid();
    }

    private void initGrid() {
        publicationGrid.setSizeFull();
        publicationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        publicationGrid.addColumn(Publication::getId).setHeader("Id");
        publicationGrid.addColumn(Publication::getPublTypeId).setHeader("Publ Type");
        publicationGrid.addColumn(Publication::getTitle).setHeader("Title");
        publicationGrid.addColumn(Publication::getAnnotation).setHeader("Annotation");
        publicationGrid.addColumn(Publication::getAgeId).setHeader("Age Id");
        publicationGrid.addColumn(Publication::getCountryId).setHeader("Country Id");
        publicationGrid.addColumn(Publication::getRegionsToString).setHeader("Regions");
        publicationGrid.addColumn(Publication::getLanguagesToString).setHeader("Languages");
        publicationGrid.addColumn(Publication::getThematicsToString).setHeader("Thematics");
        publicationGrid.addColumn(Publication::getSocial).setHeader("Social");
        publicationGrid.addColumn(Publication::getInn).setHeader("Inn");
        publicationGrid.addColumn(Publication::getVatId).setHeader("Vat");
        add(publicationGrid);
    }

    public void buildLayout(List<Publication> publicationList) {
        publicationGrid.setItems(publicationList);
    }

}
