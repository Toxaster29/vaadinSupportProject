package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.contract.UfpsEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {

        public List<UfpsEntity> getUfpsEntityList() {
        List<UfpsEntity> ufpsEntities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                     new FileInputStream(getFileFromResources("ufps.txt")), "UTF8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                ufpsEntities.add(getEntityFromLine(line));
            }
            return ufpsEntities;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return ufpsEntities;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return ufpsEntities;
        }
    }

    private UfpsEntity getEntityFromLine(String line) {
            String[] entityArray = line.split("\t");
            return new UfpsEntity(entityArray[0], entityArray[1], entityArray[2], entityArray[3], entityArray[4]);
    }

    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
    }



}
