package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.service.ReportService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Route("report")
@Component
public class ReportLayout extends VerticalLayout {

    private ReportService reportService;

    public ReportLayout(@Autowired ReportService reportService) {
        this.reportService = reportService;
        initHeader();
        initReportSelectorLayout();
    }

    private void initReportSelectorLayout() {
        TextField publisherIdField = new TextField("Publisher hid");
        Button generateReportButton = new Button("Create report for All subscription");
        generateReportButton.addClickListener(click -> {
           reportService.createAllocationReportForSubscription();
        });
        add(generateReportButton);
        Button generatePublisherReportButton = new Button("Create report for publisher with hid");
        generatePublisherReportButton.addClickListener(click -> {
            String publisherId = publisherIdField.getValue();
            if (publisherId != null && !publisherId.isEmpty()) {
                reportService.createPublisherOutputReport(publisherId);
            } else {
                reportService.createOutputReportForAllPublishers();
            }
        });
        Button addDataToReport = new Button("Add data");
        addDataToReport.addClickListener(click -> {
           reportService.addDataToReport();
        });
        Button childrenDataReport = new Button("Children publications report");
        childrenDataReport.addClickListener(click -> {
            reportService.createChildrenDataReport();
        });
        Button onlineReportWithRegion = new Button("Online report with region");
        onlineReportWithRegion.addClickListener(click -> {
            reportService.createOnlineReportWithRegion();
        });
        Button reportByPublicationForPeriod = new Button("Report by publications for period(half year)");
        reportByPublicationForPeriod.addClickListener(click -> {
           reportService.createReportByPublicationForPeriod();
        });
        add(generateReportButton, publisherIdField, generatePublisherReportButton, addDataToReport, childrenDataReport,
                onlineReportWithRegion, reportByPublicationForPeriod);
    }

    private void initHeader() {
        Label headLabel = new Label("Create report for subscription service");
        add(headLabel);
    }
}
