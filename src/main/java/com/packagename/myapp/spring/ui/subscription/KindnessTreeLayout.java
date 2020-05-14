package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.dto.KindnessTreeDao;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.apache.http.HttpEntity;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@Route("kindness")
public class KindnessTreeLayout extends VerticalLayout {

    @Autowired
    private KindnessTreeDao kindnessTreeDao;

    private static String[] orphanageTypeIndexes = new String[] {"П9147","П1360","П2558","П0967","П1142","П3357","П4254",
            "П1946","П2017","П5373","П3985","ПИ376","П5460","П2804","П2283","П2431","П2937","П3994","П3206","П5943","П1882",
            "П4530","П4452","П1808","ПИ402","П1266","П1654","П2240","П1465","П2103","П3786","П3506","П3254","П1457","П6411",
            "П1643","П4536","П2965","П3842","П1247"};
    private static String[] schoolTypeIndexes =  new String[] {"П1511","П4452","П2118","П1808","П1266","П1654","П1140","П1261",
            "ПИ451","П1526","П1465","П1032","П3302","П5373","П1985","П5000","П3786","П2100","П3506","П3254","П1457","П2936",
            "П6411","П1643","П7000","П4254","П2017","П1152","ПИ402","П4536","П1029","П7807","П5378","П3842","П1721","П1467",
            "П2965","П1989","П1972","ПР195"};
    private static String[] agedTypeIndexes = new String[] {"П1463","П7358","П2207","П1201","П4422","П2780","П5317","П2474",
            "П1982","П1338","П6238","П3122","П6588","П4324","П1265","П5943","П3994","П1511","П1815","П1991","П1230","П5944",
            "П1140","П1526","П1465","П1032","П3302","П1985","П5000","П2100","П3254","П2936","П1981","П1029","П7807","П3842",
            "П3700","П1467","П2965","П1721"};
    private static String[] militaryTypeIndexes = new String[] {"П9147","П3357","П7938","П1975","П2017","П5373","П3832",
            "ПР150","П2431","П2937","П2118","П3206","П1882","П4452","П1808","ПИ402","П1266","П1654","П3786","П3506","П1462",
            "П6238","П4014","П4316","П1917","П5000","П3254","П1457","П6411","П3300","П4852","П1859","П4536","П2965","П3842",
            "П1465","П1265","П4015","П4324","П3750"};

    public KindnessTreeLayout() {
        setSizeFull();
        initButtons();
    }

    private void initButtons() {
        Button addBids = new Button("Create bids");
        addBids.addClickListener(click -> {
            createNewBids();
        });
        Button clearOldBids = new Button("Cancel old bids");
        clearOldBids.addClickListener(click -> {
            cancelOldBids();
        });
        add(addBids,clearOldBids);
    }

    private void cancelOldBids() {

    }

    private void createNewBids() {
        List<Integer> ids = kindnessTreeDao.getOrganizationIdsByType(0);
        sendRequestForCreateNewBid(orphanageTypeIndexes, ids);

        ids = kindnessTreeDao.getOrganizationIdsByType(1);
        sendRequestForCreateNewBid(schoolTypeIndexes, ids);

        ids = kindnessTreeDao.getOrganizationIdsByType(2);
        sendRequestForCreateNewBid(agedTypeIndexes, ids);

        ids = kindnessTreeDao.getOrganizationIdsByType(3);
        sendRequestForCreateNewBid(militaryTypeIndexes, ids);

        System.out.println("All good");
    }

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

    private void sendRequestForCreateNewBid(String[] orphanageTypeIndexes, List<Integer> ids) {
        for (Integer id : ids) {
            for (String index : orphanageTypeIndexes) {
                try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                    HttpPost httpPost = new HttpPost("http://localhost:8087/api/v1.0/bid");
                    httpPost.setHeader("Content-type", "application/json; charset=utf-8");
                    String json = "{\n" +
                            "   \"subscriptionIndex\":\"" + index + "\",\n" +
                            "   \"allocation\":[\n" +
                            "      0,0,0,0,0,0,1,1,1,1,1,1\n" +
                            "   ],\n" +
                            "   \"year\":2020,\n" +
                            "   \"count\":3,\n" +
                            "   \"acceptorId\":" + id + "\n" +
                            "}";
                    StringEntity stringEntity = new StringEntity(json,"UTF-8");
                    httpPost.setEntity(stringEntity);
                    httpclient.execute(httpPost, responseHandler);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        }
    }

}
