package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.dto.TreatmentDao;
import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
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
import java.util.ArrayList;
import java.util.List;

@Route("magazine")
public class MagazineView extends VerticalLayout {

    private List<TreatmentEntity> searchList = new ArrayList();
    private List<TreatmentEntity> changeList = new ArrayList<>();

    private TreatmentDao treatmentDao;

    public MagazineView(@Autowired TreatmentDao treatmentDao) {
        this.treatmentDao = treatmentDao;
        setSizeFull();
        initHeader();
        initMainLayout();
    }

    private void initMainLayout() {
        TextArea searchTreatmentText = new TextArea();
        TextArea changeTreatmentText = new TextArea();
        HorizontalLayout mainLayout = new HorizontalLayout(searchTreatmentText, changeTreatmentText);
        Button loadDataButton = new Button("Load Data");
        loadDataButton.addClickListener(click -> {
            if (!searchTreatmentText.getValue().isEmpty()) {
                searchList.addAll(getTreatmentFromText(searchTreatmentText.getValue()));
            }
            if (!changeTreatmentText.getValue().isEmpty()) {
                changeList.addAll(getTreatmentFromText(changeTreatmentText.getValue()));
            }
        });
        Button changeNewspaperTreatment = new Button("Change newspaper");
        changeNewspaperTreatment.addClickListener(click -> {
            changeTreatment();
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(loadDataButton, changeNewspaperTreatment);
        add(mainLayout, buttonLayout);
    }

    private List<TreatmentEntity> getTreatmentFromText(String value) {
        List<TreatmentEntity> list = new ArrayList<>();
        for (String entity : value.split("\n")) {
            if (entity.length() > 0) {
                String wagon = entity.substring(0, entity.indexOf("-"));
                String place = entity.substring(entity.indexOf("-") + 1, entity.length());
                list.add(new TreatmentEntity(Integer.parseInt(wagon), Integer.parseInt(place)));
            }
        }
        return list;
    }

    private void changeTreatment() {
        searchList.forEach(treatmentEntity -> {
            List<Integer> codes = treatmentDao.getTreatmentByParams(treatmentEntity);
            if (!codes.isEmpty()) {
                Integer number = searchList.indexOf(treatmentEntity);
                for(Integer code : codes) {
                    createRequestToService(code, changeList.get(number));
                }
            }
        });
        System.out.println("Complete");
    }

    private void createRequestToService(Integer code, TreatmentEntity treatmentEntity) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPut httpPut = new HttpPut("http://10.2.60.77:8081//api/v1.0/link");
            httpPut.setHeader("Content-type", "application/json");
            httpPut.setHeader("hid", "15621057-10cf-4788-b9d8-f71006e887fb");
            String json ="{\n" +
                    "   \"zipCode\": \"" + code + "\",\n" +
                    "   \"type\": \"PAPER\",\n" +
                    "   \"treatment\": {\n" +
                    "        \"wagon\": " + treatmentEntity.getWagon() + ",\n" +
                    "        \"place\": " + treatmentEntity.getPlace() + ",\n" +
                    "        \"liter\": null\n" +
                    "    }  \n" +
                    "}";
            StringEntity stringEntity = new StringEntity(json);
            httpPut.setEntity(stringEntity);
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    System.out.println(code);
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

    private void initHeader() {
        add(new Label("Change magazine treatment"));
    }

}
