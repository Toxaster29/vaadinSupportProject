package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.dto.InsertDao;
import com.packagename.myapp.spring.dto.report.ReportDao;
import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.schedule.*;
import com.packagename.myapp.spring.service.ExcelParserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Route("schedule")
public class ScheduleView extends VerticalLayout {

    private InsertDao insertDao;

    private ReportDao reportDao;

    private ExcelParserService excelParserService;

    private List<PublisherSchedule> schedules = new ArrayList<>();

    private int startRow = 3;

    public ScheduleView(@Autowired ExcelParserService excelParserService, @Autowired InsertDao insertDao,
                        @Autowired ReportDao reportDao) {
        this.excelParserService = excelParserService;
        this.insertDao = insertDao;
        this.reportDao = reportDao;
        initHeader();
        initUploadLayout();
        initSomeChangeDateLayout();
    }

    private void initSomeChangeDateLayout() {
        TextField generateDateField = new TextField("Generate documents");
        TextField ufpsDateField = new TextField("Ufps");
        Button forAllMonth = new Button("Generate for all months");
        forAllMonth.addClickListener(click -> {
           changeAllMonthByData(generateDateField.getValue(), ufpsDateField.getValue());
        });
        HorizontalLayout changeDateLayout = new HorizontalLayout(generateDateField, ufpsDateField, forAllMonth);
        add(changeDateLayout);
    }

    private void changeAllMonthByData(String generateDateValue, String ufpsDateValue) {

    }

    private void initUploadLayout() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            com.vaadin.flow.component.Component component = createComponent(event.getMIMEType(),
                    event.getFileName(), buffer.getInputStream());
        });
        Button changeDates = new Button("Load schedules");
        changeDates.addClickListener(click -> {
            schedules.forEach(entity -> {
                entity.getDates().forEach(date -> {
                    loadDatesToService(entity.getPublisherId(),
                            entity.getContractId() != null ? entity.getContractId().toString() : null , date);
                });
            });
            System.out.println("End");
        });
        Button forAllPublisherButton = new Button("For all publishers");
        forAllPublisherButton.addClickListener(click -> {
           createAllSchedules();
        });
        Button withoutScheduleReport = new Button("Without schedule");
        withoutScheduleReport.addClickListener(click -> {
            generateWithoutScheduleReport();
        });
        Button findAllFederalAndLocalPublishers = new Button("Find publisher by type");
        findAllFederalAndLocalPublishers.addClickListener(click -> {
            findAllPublishersByType();
        });
        add(upload, changeDates, forAllPublisherButton, withoutScheduleReport, findAllFederalAndLocalPublishers);
    }

    private void findAllPublishersByType() {

        int year = 2021;
        int half = 1;
        List<String> publisherWithSchedule = insertDao.getPublishersWithScheduleByYearAndHalf(year, half);
        List<String> hidsLocalPublisher = insertDao.getAllLocalPublisher(publisherWithSchedule);
        writeToFile(StringUtils.join(hidsLocalPublisher, "\n"), "dataFile");
        System.out.println("ReportReady");
    }

    private void generateWithoutScheduleReport() {

        int year = 2021;
        int half = 1;
        List<CatalogPeriod> catalogPeriods = reportDao.getPeriodList(year, half);
        List<String> publisherWithSchedule = insertDao.getPublishersWithScheduleByYearAndHalf(year, half);
        List<String> publisherWithEmptySchedule = insertDao.getPublishersWithEmptyScheduleByYearAndHalf(year, half);
        String periods = StringUtils.join(catalogPeriods.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");
        List<String> publisherWithoutScheduleList = insertDao.getAllWithoutScheduleByPeriod(periods, publisherWithSchedule);
        List<String> publisherWithEmptyScheduleAndContract = insertDao.getPublisherWithContract(publisherWithEmptySchedule, year, half);
        List<ScheduleWithoutEntity> scheduleWithoutEntities = new ArrayList<>();
        publisherWithoutScheduleList.forEach(hid -> {
            scheduleWithoutEntities.add(createReportEntityWithComment("Не создано расписание", hid, year, half, periods));
        });
        publisherWithEmptyScheduleAndContract.forEach(hid -> {
            scheduleWithoutEntities.add(createReportEntityWithComment("Расписание создано но не привязано к договору", hid, year, half, periods));
        });
        List<String> fileData = generateReportDataWithoutSchedulers(scheduleWithoutEntities);
        writeToFile(StringUtils.join(fileData, "\n"), "dataFile");
        System.out.println("ReportReady");

    }

    private List<String> generateReportDataWithoutSchedulers(List<ScheduleWithoutEntity> scheduleWithoutEntities) {
        List<String> lines = new ArrayList<>();
        scheduleWithoutEntities.forEach(entity -> {
            lines.add(entity.getHalf() + "\t" + entity.getContragentName() + "\t" + entity.getLegalHid() + "\t"
                    + entity.getIndexes() + "\t" + entity.getManager() + "\t" + entity.getComment());
        });
        return lines;
    }

    private ScheduleWithoutEntity createReportEntityWithComment(String comment, String hid, int year, int half, String periods) {
        PublisherData publisherData = insertDao.getPublisherDataByHid(hid);
        List<String> indexes = insertDao.getAllIndexForPublisherByHid(hid, periods);
        String indexesLine = StringUtils.join(indexes, ",");
        return new ScheduleWithoutEntity(year + "-" + half, publisherData.getName(), publisherData.getHid(),
                indexesLine, publisherData.getManagerHid(), comment);
    }

    private void createAllSchedules() {

        List<String> publisherWithSchedule = insertDao.getPublishersWithSchedule();
        List<PublisherWithContract> publisherWithContracts = insertDao.getAllPublisherByYearAndHalf(publisherWithSchedule);
        publisherWithContracts.forEach(insertDao::setContractIdForPublisher);
        List<PublisherWithContract> federalOrRegional = new ArrayList<>();
        List<PublisherWithContract> local = new ArrayList<>();
        publisherWithContracts.forEach(publisher -> {
            if (publisher.getIsLocal()) {
                local.add(publisher);
            } else federalOrRegional.add(publisher);
        });
        generateJsonForPublisherWithContract(federalOrRegional, local);
        System.out.println("Normal");
    }


    private void generateJsonForPublisherWithContract(List<PublisherWithContract> federalOrRegional, List<PublisherWithContract> local) {
        String forLocalPublisher = generateJsonText(local);
        String forFederalOrRegionalPublisher = generateJsonText(federalOrRegional);
        writeToFile(forLocalPublisher, "local");
        writeToFile(forFederalOrRegionalPublisher, "federalOrRegional");
    }

    private String generateJsonText(List<PublisherWithContract> local) {
        String publisherText = "";
        for (PublisherWithContract publisher : local) {
            publisherText += String.format("{\n" +
                    "        \"publisherId\": \"%s\",\n" +
                    "        \"contractId\": \"%s\"\n" +
                    "    },\n", publisher.getHid(), publisher.getContractId() != null ? publisher.getContractId() : "-");
        }
        return publisherText;
    }

    private void writeToFile(String text, String fileName) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter(String.format("C:\\Users\\Антон\\Desktop\\%s.txt", fileName)));
            pw.write(text);
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void loadDatesToService(String publisherId, String contractId, ScheduleDates dates) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut("http://subs-tictac-service.russianpost.ru/api/v1.0/schedule/date/novalidation/iknowwhatiamdoing");
            httpPut.setHeader("Content-type", "application/json");
            httpPut.setHeader("hid", "15621057-10cf-4788-b9d8-f71006e887fb");
            String json = "{\n" +
                    "  \"dates\": {\n" +
                    "     \"CONTRACT_GENERATION\":\"" + dates.getDocGeneration() + "\",\n" +
                    "     \"ONLINE\": \"" + dates.getOnlineDate() + "\",\n" +
                    "     \"TCFPS\": \"" + dates.getTfpsDate() + "\"" +
                    getPublisherDate(dates.getPublisherDate()) +
                    "   },\n" +
                    "  \"publisherId\": \"" + publisherId + "\",\n" +
                    "  \"year\": 2020,\n" +
                    "  \"halfYear\": 1,\n" +
                    getContractId(contractId) +
                    "  \"campaignType\": \"" + dates.getName() + "\",\n" +
                    "  \"month\": " + getMonthFromDates(dates.getMonth()) + "\n" +
                    "}";
            StringEntity stringEntity = new StringEntity(json);
            httpPut.setEntity(stringEntity);
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    System.out.println("Error");
                }
                return null;
            };
            String responseBody = httpclient.execute(httpPut, responseHandler);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private String getMonthFromDates(String month) {
        return month != null ? "\"" + month + "\"" : "null";
    }

    private String getContractId(String contractId) {
        return contractId != null ? "\"contractId\": \"" + contractId + "\"," : "";
    }

    private String getPublisherDate(LocalDate publisherDate) {
        return publisherDate != null ? ",\n     \"PUBLISHER\":\"" + publisherDate + "\"\n" : "";
    }

    private void initHeader() {
        Label headLabel = new Label("Schedule for subscription service");
        add(headLabel);
    }

    private com.vaadin.flow.component.Component createComponent(String mimeType, String fileName, InputStream inputStream) {
        try {
            schedules = excelParserService.readFromExcelSchedules(inputStream, startRow - 1);
            System.out.println("Good");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
