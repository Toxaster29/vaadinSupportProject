package com.packagename.myapp.spring.ui.subscription;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

@Route("canceldoc")
public class CancelDoscLayout extends VerticalLayout {

    ResponseHandler<String> responseHandler = new MyResponseHandler();

    public CancelDoscLayout() {
        TextArea idsOrderArea = new TextArea("For order ids");
        Button cancelOrderButton = new Button("Cancel order");
        cancelOrderButton.addClickListener(click -> {
           cancelOrders(idsOrderArea.getValue());
        });
        TextArea idsCstArea = new TextArea("For cst ids");
        Button cancelCstButtun = new Button("Cancel cst");
        cancelCstButtun.addClickListener(click -> {
            cancelCst(idsCstArea.getValue());
        });
        add(idsOrderArea, cancelOrderButton, idsCstArea, cancelCstButtun);
    }

    private void cancelCst(String value) {
        String[] ids = value.split("\n");
        for (String id : ids) {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost("http://subs-subscription-service.russianpost.ru/api/v1.0/cst/" + id + "/cancel");
                httpPost.setHeader("Accept", "application/json");
                httpclient.execute(httpPost, responseHandler);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

    }

    private void cancelOrders(String value) {
        String[] ids = value.split("\n");
        for (String id : ids) {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost("http://subs-subscription-service.russianpost.ru/api/v1.0/order/" + id + "/cancel");
                httpPost.setHeader("Accept", "application/json");
                httpclient.execute(httpPost, responseHandler);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

    }
}
