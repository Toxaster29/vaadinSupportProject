package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Publication;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UIScope
public class PublicationsLayout extends VerticalLayout {

    private Grid<Publication> publicationGrid = new Grid<>();

    private PublVerisonDialog publVerisonDialog;

    public PublicationsLayout(@Autowired PublVerisonDialog publVerisonDialog) {
        this.publVerisonDialog = publVerisonDialog;
        setSizeFull();
        initGrid();
    }

    private void initGrid() {
        publicationGrid.setSizeFull();
        publicationGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        publicationGrid.addColumn(Publication::getId).setHeader("Id").setWidth("90px").setFlexGrow(0);
        publicationGrid.addColumn(Publication::getPubltypeid).setHeader("Publ Type").setWidth("100px").setFlexGrow(0);
        publicationGrid.addColumn(Publication::getTitle).setHeader("Title");
        publicationGrid.addColumn(Publication::getAnnotation).setHeader("Annotation");
        publicationGrid.addColumn(Publication::getAgeId).setHeader("Age Id").setWidth("90px").setFlexGrow(0);
        publicationGrid.addColumn(Publication::getCountryid).setHeader("Country Id").setWidth("100px").setFlexGrow(0);
        publicationGrid.addColumn(Publication::getRegionsToString).setHeader("Regions");
        publicationGrid.addColumn(Publication::getLanguagesToString).setHeader("Languages").setWidth("120px").setFlexGrow(0);
        publicationGrid.addColumn(Publication::getThematicsToString).setHeader("Thematics").setWidth("130px").setFlexGrow(0);
        publicationGrid.addColumn(Publication::getSocial).setHeader("Social").setWidth("70px").setFlexGrow(0);
        publicationGrid.addColumn(Publication::getInn).setHeader("Inn");
        publicationGrid.addColumn(Publication::getVatid).setHeader("Vat").setWidth("70px").setFlexGrow(0);
        add(publicationGrid);
        publicationGrid.addItemDoubleClickListener(click -> {
            if (click.getItem() != null) {
                publVerisonDialog.buildDialog(click.getItem().getPublversion(), click.getItem().getAnnotation());
                publVerisonDialog.open();
            }
        });
    }

    public void buildLayout(List<Publication> publicationList) {
        publicationGrid.setItems(publicationList);
    }

}
