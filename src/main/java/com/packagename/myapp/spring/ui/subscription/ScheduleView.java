package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.entity.schedule.PublisherSchedule;
import com.packagename.myapp.spring.entity.schedule.ScheduleDates;
import com.packagename.myapp.spring.service.ExcelParserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route("schedule")
public class ScheduleView extends VerticalLayout {

    private ExcelParserService excelParserService;

    private List<PublisherSchedule> schedules = new ArrayList<>();

    private int startRow = 3;

    public ScheduleView(@Autowired ExcelParserService excelParserService) {
        this.excelParserService = excelParserService;
        initHeader();
        initUploadLayout();
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
        add(upload, changeDates);
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
