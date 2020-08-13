package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.service.report.OnlineReportService;
import com.packagename.myapp.spring.service.report.ReportService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Route("report")
@Component
public class ReportLayout extends VerticalLayout {

    private ReportService reportService;
    private OnlineReportService onlineReportService;

    public ReportLayout(@Autowired ReportService reportService, @Autowired OnlineReportService onlineReportService) {
        this.reportService = reportService;
        this.onlineReportService = onlineReportService;
        initHeader();
        initReportSelectorLayout();
    }

    private void initReportSelectorLayout() {
        TextArea publisherIdField = new TextArea("Publisher hid");
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
        Button reportForSocialPublication = new Button("Report for social");
        reportForSocialPublication.addClickListener(click -> {
            reportService.createReportForSocial();
        });
        Button onlineReportByDate = new Button("Online report by date");
        onlineReportByDate.addClickListener(click -> {
            onlineReportService.createReportByDate(publisherIdField.getValue());
        });
        Button onlineReportWithRegionBuy = new Button("Online report with region buy");
        onlineReportWithRegionBuy.addClickListener(click -> {
           onlineReportService.createReportWithBuyRegion(publisherIdField.getValue());
        });
        Button onlineTopPublications = new Button("Online report top 500 publications");
        onlineTopPublications.addClickListener(click -> {
           onlineReportService.createReportOnlineTopPublications(publisherIdField.getValue());
        });
        Button onlineReportForMonth = new Button("Online report for month");
        onlineReportForMonth.addClickListener(click -> {
            onlineReportService.createOnlineReportForMonth(publisherIdField.getValue());
        });
        add(generateReportButton, publisherIdField, generatePublisherReportButton, addDataToReport, childrenDataReport,
                onlineReportWithRegion, reportByPublicationForPeriod, reportForSocialPublication, onlineReportByDate,
                onlineReportWithRegionBuy, onlineTopPublications, onlineReportForMonth);
    }

    private void initHeader() {
        Label headLabel = new Label("Create report for subscription service");
        add(headLabel);
    }
}
