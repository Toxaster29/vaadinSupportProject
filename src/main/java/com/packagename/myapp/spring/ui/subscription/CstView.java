package com.packagename.myapp.spring.ui.subscription;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Route("cst")
public class CstView extends VerticalLayout {

    private static String publisherCstName = "publisherCst";
    private static String unionCstName = "unionCst";

    public CstView() {
        TextArea dataArea = new TextArea();
        add(dataArea);
        Button confirmButton = new Button("Confirm");
        confirmButton.addClickListener(click -> {
            createCstRequest(dataArea.getValue());
        });
        add(confirmButton);
        TextArea cancelArea = new TextArea();
        add(cancelArea);
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(click -> {
            cancelDocuments(cancelArea.getValue());
        });
        add(cancelButton);
    }

    private void cancelDocuments(String value) {
        String [] hids = value.split(";");
        for(String hid : hids) {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpPut httpPut = new HttpPut(String.format("http://subs-avatar.russianpost.ru/api/v1.0/document/%s/cancel", hid));
                httpPut.setHeader("hid", "15621057-10cf-4788-b9d8-f71006e887fb");
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
    }

    private void createCstRequest(String value) {
        String[] indexes = value.split(";");
        for (String index : indexes) {
            try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
            RequestBody body = RequestBody.create(mediaType, "{\r\n   \"index\": \"" + index +"\",\r\n   \"includeOtherIndex\": true,\r\n   \"date\": \"2020-01-01\",\r\n   \"publisherId\" : \"90483\"\r\n}");
            Request request = new Request.Builder()
                    .url("http://10.2.60.76:8080/api/v1.0/cst/union")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.code());
            } catch (Exception e) {
                System.err.println(e);
            }
        }
        System.out.println("Complete");
    }

}
