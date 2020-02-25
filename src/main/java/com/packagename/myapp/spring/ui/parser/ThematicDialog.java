package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.Directory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.ArrayList;
import java.util.List;

@SpringComponent
@UIScope
public class ThematicDialog extends Dialog {

    private Runnable onConfirmAction;

    private Grid<Directory> directoryGrid = new Grid<>();
    private TextField searchLine = new TextField("Search");
    private Label headerLabel = new Label();


    private List<Directory> selectedItems;
    private List<Directory> itemForAddingList = new ArrayList<>();
    private List<Directory> fullThematicList;

    public ThematicDialog() {
        setWidth("800px");
        setHeight("600px");
        directoryGrid.setSizeFull();
        directoryGrid.setSelectionMode(Grid.SelectionMode.NONE);
        directoryGrid.addColumn(Directory::getId).setHeader("Id").setWidth("80px").setFlexGrow(0);
        directoryGrid.addColumn(Directory::getName).setHeader("Name").setFlexGrow(1);
        directoryGrid.addComponentColumn(item -> createAddThematicButton(item)).setHeader("Add/Delete")
                .setWidth("140px").setFlexGrow(0);
        searchLine.setWidthFull();
        searchLine.addValueChangeListener(event -> {
            if (!event.getValue().isEmpty() && event.getValue() != null) {
                directoryGrid.setItems(fullThematicList.stream().filter(item -> item.getName().toUpperCase()
                        .contains(event.getValue().toUpperCase())));
            } else directoryGrid.setItems(fullThematicList);
        });
        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(click -> {
            selectedItems.clear();
            selectedItems.addAll(itemForAddingList);
            onConfirmAction.run();
            close();
        });
        VerticalLayout mainLayout = new VerticalLayout(headerLabel, searchLine, directoryGrid, confirmButton);
        mainLayout.setSpacing(false);
        mainLayout.setSizeFull();
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(mainLayout);
    }

    private Component createAddThematicButton(Directory item) {
        Button addItemButton = new Button("Add");
        if (isAddedItem(item)) addItemButton.setText("Delete");
        addItemButton.addClickListener(click -> {
            if (isAddedItem(item)) {
                addItemButton.setText("Add");
                itemForAddingList.remove(item);
            } else {
                addItemButton.setText("Delete");
                itemForAddingList.add(item);
            }
        });
        return addItemButton;
    }

    private boolean isAddedItem(Directory item) {
        return itemForAddingList.contains(item);
    }

    public void buildDialog(String oldName, List<Directory> thematics, List<Directory> selectedItems, Runnable onConfirmAction) {
        itemForAddingList.clear();
        searchLine.clear();
        this.onConfirmAction = onConfirmAction;
        directoryGrid.setItems(thematics);
        this.selectedItems = selectedItems;
        this.fullThematicList = thematics;
        itemForAddingList.addAll(selectedItems);
        headerLabel.setText(String.format("Select thematics from grid: %s", oldName));
        selectedItems.forEach(item -> {
            directoryGrid.select(item);
        });
    }
}
