package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.service.ReportService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("report")
public class ReportLayout extends VerticalLayout {

    private ReportService reportService;

    public ReportLayout(@Autowired ReportService reportService) {
        this.reportService = reportService;
        initHeader();
        initReportSelectorLayout();
    }

    private void initReportSelectorLayout() {
        Button generateReportButton = new Button("Create report");
        generateReportButton.addClickListener(click -> {
           reportService.createAllocationReportForSubscription();
        });
        add(generateReportButton);
    }


    private void initHeader() {
        Label headLabel = new Label("Create report for subscription service");
        add(headLabel);
    }
}
