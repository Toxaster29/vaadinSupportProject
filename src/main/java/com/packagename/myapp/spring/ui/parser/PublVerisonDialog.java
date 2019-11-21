package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.entity.parser.newFormat.PublVersion;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@UIScope
public class PublVerisonDialog extends Dialog {

    private Grid<PublVersion> publVerisonDialogGrid = new Grid<>();
    private Label annotationLabel = new Label("");

    public PublVerisonDialog() {
        setSizeFull();
        setWidth("1200px");
        setHeight("700px");
        publVerisonDialogGrid.setSizeFull();
        publVerisonDialogGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        publVerisonDialogGrid.addColumn(PublVersion::getId).setHeader("Id");
        publVerisonDialogGrid.addColumn(PublVersion::getName).setHeader("Name").setWidth("260px");
        //TODO Добавить массивы или вынести в отдельный диалог
        publVerisonDialogGrid.addColumn(PublVersion::getStandard).setHeader("Standard");
        publVerisonDialogGrid.addColumn(PublVersion::getWeight).setHeader("Weight");
        publVerisonDialogGrid.addColumn(PublVersion::getPages).setHeader("Pages");
        publVerisonDialogGrid.addColumn(PublVersion::getFormatId).setHeader("Format Id");
        publVerisonDialogGrid.addColumn(PublVersion::getHeight).setHeader("Height");
        publVerisonDialogGrid.addColumn(PublVersion::getWidth).setHeader("Width");
        publVerisonDialogGrid.addColumn(PublVersion::getTimeId).setHeader("Time Id");
        publVerisonDialogGrid.addColumn(PublVersion::getCount).setHeader("Count");
        VerticalLayout mainLayout = new VerticalLayout(annotationLabel, publVerisonDialogGrid);
        mainLayout.setSizeFull();
        add(mainLayout);
    }

    public void buildDialog(List<PublVersion> publVersionList, String annotation) {
        if (publVersionList != null) {
            publVerisonDialogGrid.setItems(publVersionList);
        }
        if (annotation != null) {
            annotationLabel.setVisible(true);
            annotationLabel.setText(annotation);
        } else annotationLabel.setVisible(false);
    }

}
