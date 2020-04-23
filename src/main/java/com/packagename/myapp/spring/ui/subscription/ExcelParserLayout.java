package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.dto.ExcelParserDao;
import com.packagename.myapp.spring.entity.excelParser.PublisherFromExcel;
import com.packagename.myapp.spring.service.ExcelParserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Route("excelParser")
public class ExcelParserLayout extends VerticalLayout {

    private ExcelParserService excelParserService;

    private List<PublisherFromExcel> publisherFromExcelList = new ArrayList<>();

    public ExcelParserLayout(@Autowired ExcelParserService excelParserService) {
        this.excelParserService = excelParserService;
        initMainLayout();
    }

    private void initMainLayout() {
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                    event.getFileName(), buffer.getInputStream());
            Object output = null;
        });
        Button changeContractDataButton = new Button("Change data");
        changeContractDataButton.addClickListener(click -> {
            excelParserService.setNmcByExcelData(publisherFromExcelList);
        });
        add(upload, changeContractDataButton);
    }

    private Component createComponent(String mimeType, String fileName, InputStream inputStream) {
        try {
            publisherFromExcelList = excelParserService.readPublisherDataFromExcel(inputStream);
            System.out.println("reading finished");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

}
