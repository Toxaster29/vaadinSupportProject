package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.parser.Agency;
import com.packagename.myapp.spring.entity.parser.Publication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentParseService {

    public void parseFileData(List<String> lineList) {
        final String[] sender = {""};
        List<Publication> publications = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        List<String> priceList = new ArrayList<>();
        List<String> catalogList = new ArrayList<>();
        List<String> countList = new ArrayList<>();
        List<String> areaList = new ArrayList<>();
        List<Agency> agencies = new ArrayList<>();
        List<String> dispatchList = new ArrayList<>();
        lineList.stream().forEach(line -> {
            if (!line.isEmpty()) {
                int point = line.indexOf('(');
                String tagName = line.substring(0, point);
                switch (tagName) {
                    case "S_PUBL":
                        //publications.add(parsePublicationParams(parseLine(line.substring(point))));
                        break;
                    case "S_INDEX":
                        indexList.add(line.substring(point));
                        break;
                    case "S_PRICE":
                        priceList.add(line.substring(point));
                        break;
                    case "S_CATALOG":
                        catalogList.add(line.substring(point));
                        break;
                    case "S_CONT_IN":
                        countList.add(line.substring(point));
                        break;
                    case "C_FROM":
                        sender[0] = line.substring(point);
                        break;
                    case "S_AREA":
                        areaList.add(line.substring(point));
                        break;
                    case "S_AGENCY":
                        agencies.add(parseAgencyParams(parseLine(line.substring(point))));
                        break;
                    case "S_DISPATCH":
                        dispatchList.add(line.substring(point));
                        break;
                }
            }
        });
        System.out.println("Complete");
    }

    private Agency parseAgencyParams(List<String> parseLine) {

        return new Agency();
    }

    /*private Publication parsePublicationParams(List<String> parseLine) {
        return new Publication(Long.parseLong(parseLine.get(0)),
                getPublicationTypeIdByName(parseLine.get(8)),
                parseLine.get(1),
                parseLine.get(3),
                getCountryIdByName(parseLine.get(5)),
                getRegionListForPublication(),
                );
    }*/

    private List<Dictionary> getRegionListForPublication() {
        return null;
    }

    private Long getCountryIdByName(String s) {
        return null;
    }

    private Long getPublicationTypeIdByName(String s) {
        return null;
    }

    private List<String> parseLine(String line) {
        List<String> paramList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\([0-9]*\\)");
        Matcher matcher = pattern.matcher(line);
        Integer lastCount = 0;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (end - start != 2) {
                String a = line.substring(start + 1, end - 1);
                Integer count = Integer.parseInt(line.substring(start + 1, end - 1));
                if (start >= lastCount) {
                    paramList.add(line.substring(end, end + count));
                    lastCount = end + count;
                }
            } else paramList.add(null);
        }
        return paramList;
    }


}
