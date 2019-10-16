package com.packagename.myapp.spring.ui.parser;

import com.packagename.myapp.spring.service.DocumentParseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.MultiFileReceiver;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Route("parser")
public class FileParseLayout extends VerticalLayout {

    private DocumentParseService parseService;

    public FileParseLayout(@Autowired DocumentParseService parseService) {
        this.parseService = parseService;
        setSizeFull();
        initHeader();
        initFileUpload();
        initOtherComponents();
    }

    private void initOtherComponents() {

    }

    private void initFileUpload() {
        Upload upload = new Upload((MultiFileReceiver) (filename, mimeType) -> {
            File file = new File(new File("src\\main\\resources\\uploaded-files"), filename);
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                return null;
            }
        });
        add(upload);
        Button readData = new Button("Read files data");
        readData.addClickListener(click -> {
            try {
                readAllArchives();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        add(readData);
    }

    private void readAllArchives() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get("src\\main\\resources\\uploaded-files"))) {
            paths.filter(Files::isRegularFile).forEach(e -> {
                ZipFile zipFile = null;
                List<String> lineList = new ArrayList<>();
                try {
                    zipFile = new ZipFile(e.toFile());
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while(entries.hasMoreElements()){
                        ZipEntry entry = entries.nextElement();
                        InputStream stream = zipFile.getInputStream(entry);
                        readOldFormatData(stream, lineList);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (!lineList.isEmpty()) {
                    parseService.parseFileData(lineList);
                }
            });
        }
    }

    private void readOldFormatData(InputStream stream, List<String> lineList) {
        InputStreamReader isr = new InputStreamReader(stream, Charset.forName("cp1251"));
        BufferedReader br = new BufferedReader(isr);
        br.lines().forEach(line -> {
            lineList.add(line);
        });
    }

    private void initHeader() {
        Label headLabel = new Label("Parse old standard files");
        add(headLabel);
    }
}
