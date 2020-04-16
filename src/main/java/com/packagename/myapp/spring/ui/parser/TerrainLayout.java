package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Terrain;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UIScope
public class TerrainLayout extends VerticalLayout {

    Grid<Terrain> terrainGrid= new Grid<>();

    public TerrainLayout() {
        setSizeFull();
        terrainGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        terrainGrid.setSizeFull();
        terrainGrid.addColumn(Terrain::getId).setHeader("Id");
        terrainGrid.addColumn(Terrain::getName).setHeader("Name");
        terrainGrid.addColumn(Terrain::getRegionid).setHeader("Region Id");
        terrainGrid.addColumn(Terrain::zipcodesToString).setHeader("Zip codes");
        add(terrainGrid);
    }

    public void buildLayout(List<Terrain> terrainList) {
        if (terrainList != null) {
            terrainGrid.setItems(terrainList);
        }
    }

}
