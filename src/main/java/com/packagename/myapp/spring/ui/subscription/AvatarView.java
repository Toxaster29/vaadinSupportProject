package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.dto.DownloadDao;
import com.packagename.myapp.spring.entity.avatar.Document;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Route("avatar")
public class AvatarView extends VerticalLayout {

    private DownloadDao downloadDao;

    public AvatarView(@Autowired DownloadDao downloadDao) {
        this.downloadDao = downloadDao;
        Button downloadButton = new Button("Download");
        downloadButton.addClickListener(click -> {
            downloadData();
        });
        add(downloadButton);
    }

    private void downloadData() {
        List<Document> documents = downloadDao.getAllDocumentForOperationId("f5a2c1cf-6b5a-4c3f-ac2b-282a456543f2");
        documents.forEach(document -> {
            List<String> urls = new ArrayList<>();
            getAllLinks(document.getMeta(), urls);
            Integer start = document.getMeta().indexOf("index");
            Integer end = document.getMeta().indexOf(",", start);
            String index = document.getMeta().substring(start + 9, end - 1);
            String url = urls.stream().filter(line -> line.indexOf("stb_dos") >= 0).findFirst().get();
            System.out.println(url);
            try {
                FileUtils.copyURLToFile(new URL(url), new File("C:\\Users\\assze\\Desktop\\testData\\files\\"
                        + index + "_" + 23122019 + "_stb_dos" + ".zip"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getAllLinks(String meta, List<String> url) {
        Integer start =  meta.indexOf("url");
        if (start != -1) {
            Integer end = meta.indexOf(",", start);
            url.add(meta.substring(start + 7, end - 1));
            getAllLinks(meta.substring(end, meta.length() - 1), url);
        }

    }

}
