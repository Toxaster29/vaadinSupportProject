package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Format;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@UIScope
public class NewFormatResultDialog extends Dialog {

    private Format format;

    private PublicationsLayout publicationsLayout;
    private AgencyLayout agencyLayout;
    private TerrainLayout terrainLayout;
    private CatalogLayout catalogLayout;

    public NewFormatResultDialog(@Autowired PublicationsLayout publicationsLayout, @Autowired AgencyLayout agencyLayout,
                                 @Autowired TerrainLayout terrainLayout, @Autowired CatalogLayout catalogLayout) {
        this.publicationsLayout = publicationsLayout;
        this.agencyLayout = agencyLayout;
        this.terrainLayout = terrainLayout;
        this.catalogLayout = catalogLayout;
        setHeight("calc(100vh - (2*var(--lumo-space-m)))");
        setWidth("calc(100vw - (4*var(--lumo-space-m)))");
        Label headerLabel = new Label("New format moderation");
        PagedTabs tabs = new PagedTabs();
        tabs.setSizeFull();
        tabs.add(publicationsLayout, "Publication");
        tabs.add(agencyLayout, "Agency");
        tabs.add(terrainLayout, "Terrain");
        tabs.add(catalogLayout, "Catalog");
        VerticalLayout mainLayout = new VerticalLayout(headerLabel, tabs);
        mainLayout.setSizeFull();
        add(mainLayout);
    }

    public void buildDialog(Format format) {
        this.format = format;
        publicationsLayout.buildLayout(format.getCampaign().get(0).getPublication());
        agencyLayout.buildLayout(format.getAgency());
        terrainLayout.buildLayout(format.getTerrain());
        catalogLayout.buildLayout(format.getCampaign().get(0).getCatalog());
    }

}
