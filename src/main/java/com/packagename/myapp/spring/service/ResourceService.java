package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.insert.EmailPhone;
import com.packagename.myapp.spring.entity.report.online.PochtaIdInfo;
import com.packagename.myapp.spring.entity.ufps.UfpsEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceService {

        public List<UfpsEntity> getUfpsEntityList() {
        List<UfpsEntity> ufpsEntities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                     new FileInputStream("C:\\Users\\Антон\\IdeaProjects\\vaadinSupportProject\\src\\main\\resources\\ufps.txt"), "UTF8"))) {
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


    public Map<Integer, String> getMacroRegionEntityMap() {
            Map<Integer, String> macroRegionMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("C:\\Users\\Антон\\IdeaProjects\\vaadinSupportProject\\src\\main\\resources\\macroRegion.txt"), "UTF8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entityArray = line.split("\t");
                macroRegionMap.put(Integer.parseInt(entityArray[1]), entityArray[0]);
            }
            return macroRegionMap;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return macroRegionMap;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return macroRegionMap;
        }
    }

    public Map<String, String> getHidInfoMap() {
        Map<String, String> hidMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("C:\\Users\\Антон\\IdeaProjects\\vaadinSupportProject\\src\\main\\resources\\hidInfo.txt"), "UTF8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entityArray = line.split("\t");
                hidMap.put(entityArray[0], entityArray[1]);
            }
            return hidMap;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return hidMap;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return hidMap;
        }
    }

    public Map<String, PochtaIdInfo> getHidEmailPhoneMap() {
        Map<String, PochtaIdInfo> hidMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("C:\\Users\\Антон\\IdeaProjects\\vaadinSupportProject\\src\\main\\resources\\hidInfo.txt"), "UTF8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] entityArray = line.split("\t");
                hidMap.put(entityArray[0], new PochtaIdInfo(entityArray[1],entityArray[2], entityArray[3]));
            }
            return hidMap;
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            return hidMap;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            return hidMap;
        }
    }
}
