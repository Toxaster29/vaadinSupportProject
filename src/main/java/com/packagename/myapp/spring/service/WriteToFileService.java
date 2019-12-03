package com.packagename.myapp.spring.service;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
public class WriteToFileService {

    private static String path = "";

    public void writeStringListToFile(List<String> list) throws IOException {
        if (!list.isEmpty()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            list.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        }
    }

}
