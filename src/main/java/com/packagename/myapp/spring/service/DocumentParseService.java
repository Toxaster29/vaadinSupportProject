package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.dto.ParserDao;
import com.packagename.myapp.spring.entity.parser.DirectoryData;
import com.packagename.myapp.spring.entity.parser.newFormat.*;
import com.packagename.myapp.spring.entity.parser.oldFormat.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DocumentParseService {

    @Autowired
    private ParserDao parserDao;

    private static String FOR_ALL_TEXT = "для всех";
    private static String FOR_PHYSICS_TEXT = "для физических лиц";
    private static String FOR_INDIVIDUAL_TEXT = "для индивидуальных подписчиков";

    public void fillCampaignParams(List<SPublication> publications, Format endJson, List<ConnectivityThematicEntity> thematicEntities,
                                   List<STopicIn> topicInList, List<SIndex> indexList, List<SPrice> priceList,
                                   String halfYear, String yearFieldValue, Accept acceptSelectValue, List<Complex> complexList) {
        List<Campaign> campaignList = new ArrayList<>();
        List<Publication> publicationList = getPublicationParams(publications, endJson, thematicEntities, topicInList, indexList, priceList);
        campaignList.add(new Campaign(
                Integer.valueOf(yearFieldValue),
                Byte.valueOf(halfYear),
                setAcceptByParam(acceptSelectValue),
                publicationList,
                getCatalogParams(indexList, endJson, publications, priceList, publicationList, yearFieldValue, halfYear,
                        acceptSelectValue.getId(), complexList)));
        endJson.setCampaign(campaignList);
    }

    private List<Accept> setAcceptByParam(Accept acceptSelectValue) {
        List<Accept> acceptList = new ArrayList<>();
        acceptList.add(acceptSelectValue);
        return acceptList;
     }

    private List<Catalog> getCatalogParams(List<SIndex> indexList, Format endJson, List<SPublication> publications,
                                           List<SPrice> priceList, List<Publication> publicationList, String yearFieldValue,
                                           String halfYear, Integer acceptId, List<Complex> complexList) {
        List<Catalog> catalogList = new ArrayList<>();
        indexList.forEach(sIndex -> {
            SPublication sPublication = getPublicationById(sIndex.getPublicationId(), publications);
            catalogList.add(new Catalog(
                    sIndex.getId(),
                    sIndex.getComplexName(),
                    sIndex.getDescription(),
                    sIndex.getAgencyId(),
                    null,
                    getExpeditionIdByName(sIndex.getSystem(), endJson.getExpedition()),
                    getClientIdTypeByName(sIndex.getSubsCategory(), endJson.getClient()),
                    null,
                    getTermByPublicationData(sPublication, yearFieldValue, halfYear),
                    getSubVersion(publicationList, sPublication, complexList, sIndex.getId()),
                    getSubVariant(sIndex.getId(), priceList, endJson.getVat(), acceptId)));
        });
        return catalogList;
    }

    private Integer getClientIdTypeByName(String subsCategory, List<Directory> client) {
        if (FOR_ALL_TEXT.toUpperCase().equals(subsCategory.toUpperCase())) {
            return 7; //TODO захордкожено по описанию формата возможно лучше переделать потом
        } else if (FOR_PHYSICS_TEXT.toUpperCase().equals(subsCategory.toUpperCase())) {
            return 5;
        }  else if (FOR_INDIVIDUAL_TEXT.toUpperCase().equals(subsCategory.toUpperCase())) {
            return 1;
        } else {
            for (Directory directory : client) {
                if (subsCategory.toUpperCase().equals(directory.getName().toUpperCase())) return directory.getId();
            }
        }
        return null;
    }

    private List<SubsVariant> getSubVariant(String id, List<SPrice> priceList, List<Directory> vat, Integer acceptId) {
        List<SubsVariant> subsVariants = new ArrayList<>();
        for (SPrice price : priceList) {
            if (price.getIndexId().equals(id)) {
                subsVariants.add(new SubsVariant(
                        acceptId,
                        null,
                        price.getMsp(),
                        getPriceFromPriceWithVat(price.getPriceAndNDS()),
                        getVatFromPriceWithVat(price.getPriceAndNDS(), vat),
                        getStateFromPrice(price.getSubscriptionAcceptance())));
            }
        }
        return subsVariants;
    }

    private Byte getStateFromPrice(String subscriptionAcceptance) {
        if (subscriptionAcceptance.equals("Открыта")) return 1;
        return 0;
    }

    private Integer getVatFromPriceWithVat(String priceAndNDS, List<Directory> vat) {
        String nds = priceAndNDS.split(",")[1];
        return vat.stream().filter(v -> v.getName().equals(nds)).findFirst().get().getId();
    }

    private Integer getPriceFromPriceWithVat(String priceAndNDS) {
        String price = priceAndNDS.split(",")[0];
        String endPrice = String.valueOf(new BigDecimal(price).setScale(2).multiply(BigDecimal.valueOf(100)).setScale(0));
        return Integer.parseInt(endPrice);
    }

    private List<SubsVersion> getSubVersion(List<Publication> publications, SPublication sPublication, List<Complex> complexList, String id) {
        List<SubsVersion> subsVersions = new ArrayList<>();
        List<String> indexesFromComplex = complexList.stream().filter(c -> c.getIndexComplex().equals(id))
                .map(Complex::getIndexInclude).collect(Collectors.toList());
        if (indexesFromComplex.isEmpty()) {
            subsVersions.addAll(getSubVersionFromPublication(sPublication.getId(), publications));
        } else {
            indexesFromComplex.forEach(index -> {
                subsVersions.addAll(getSubVersionFromPublication(index, publications));
            });
        }
        return subsVersions;
    }

    private List<SubsVersion> getSubVersionFromPublication(String index, List<Publication> publications) {
        List<SubsVersion> subsVersions = new ArrayList<>();
        for (Publication publ : publications) {
                if (publ.getId().equals(getIdWithoutLetter(index))) {
                    publ.getPublVersion().forEach(version -> {
                        subsVersions.add(new SubsVersion(publ.getId(), version.getId()));
                    });
                    break;
                }
        }
        return subsVersions;
    }

    private List<Term> getTermByPublicationData(SPublication sPublication, String yearFieldValue, String halfYear) {
        List<Term> terms = new ArrayList<>();
        if (sPublication != null) {
            Byte mspMonth = halfYear.equals("1") ? (byte) 1 : (byte) 7;
            Integer year = Integer.valueOf(yearFieldValue);
            String[] dates = sPublication.getDates().split(",");
            if (dates.length > 0) {
                for (int i = 1; i<=dates.length; i++) {
                    List<Integer> day = getDateFromLine(dates[i-1]);
                    if (!day.isEmpty()) {
                        for (Integer date : day) {
                            try {
                                terms.add(new Term(mspMonth, LocalDate.of(year, i, date)));
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    }
                }
            }
        }
        return terms;
    }

    private List<Integer> getDateFromLine(String date) {
        List<Integer> dates = new ArrayList<>();
        if (!date.isEmpty()) {
            Pattern patternLongDate = Pattern.compile("[0-9]{2}.[0-9]{2}.[0-9]{4}");
            Matcher matcherLongDate = patternLongDate.matcher(date);
            Pattern patternNumeric = Pattern.compile("[0-9]+");
            if (matcherLongDate.find()) {
                while(matcherLongDate.find()) {
                    dates.add(Integer.parseInt(date.substring(matcherLongDate.start(), matcherLongDate.start() + 1)));
                }
            } else if (date.contains("№")) {
                if (date.indexOf(":") > 0) {
                    String dateWithoutNumber = date.substring(date.indexOf(":"));
                    addDatesToList(dates, dateWithoutNumber, patternNumeric);
                }
            } else {
                addDatesToList(dates, date, patternNumeric);
            }
        }
        return dates;
    }

    private void addDatesToList(List<Integer> dates, String date, Pattern patternNumeric) {
        Matcher matcherNumeric = patternNumeric.matcher(date);
        while(matcherNumeric.find()) {
            dates.add(Integer.parseInt(date.substring(matcherNumeric.start(), matcherNumeric.end())));
        }
    }

    private SPublication getPublicationById(String publicationId, List<SPublication> publications) {
        for(SPublication publication : publications) {
            if(publication.getId().equals(publicationId)) return publication;
        }
        return null;
    }

    private Integer getExpeditionIdByName(String system, List<Directory> expedition) {
        for (Directory exp : expedition) {
            if (exp.getName().toUpperCase().equals(system.toUpperCase().trim())) return exp.getId();
        }
        return null;
    }

    public void fillDictionaryData(Format endJson) {
        List<DirectoryData> directoryData = parserDao.getDictionaryData();
        endJson.setDistribution(directoryData.stream().filter(data -> data.getDirectoryId() == 1)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setSupply(directoryData.stream().filter(data -> data.getDirectoryId() == 2)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setExpedition(directoryData.stream().filter(data -> data.getDirectoryId() == 3)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setClient(directoryData.stream().filter(data -> data.getDirectoryId() == 4)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setPublType(directoryData.stream().filter(data -> data.getDirectoryId() == 5)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setVat(directoryData.stream().filter(data -> data.getDirectoryId() == 6)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setAge(directoryData.stream().filter(data -> data.getDirectoryId() == 7)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setThematic(directoryData.stream().filter(data -> data.getDirectoryId() == 8)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setFormat(directoryData.stream().filter(data -> data.getDirectoryId() == 9)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setTime(directoryData.stream().filter(data -> data.getDirectoryId() == 10)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setPeriodType(directoryData.stream().filter(data -> data.getDirectoryId() == 11)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setRegion(directoryData.stream().filter(data -> data.getDirectoryId() == 12)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setCountry(directoryData.stream().filter(data -> data.getDirectoryId() == 13)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
        endJson.setLanguage(directoryData.stream().filter(data -> data.getDirectoryId() == 14)
                .map(data -> new Directory(data.getId(), data.getName())).collect(Collectors.toList()));
    }

    public void fillAgencyParams(List<SAgency> agencies, Format format) {
        List<Agency> newFormatAgencyList = new ArrayList<>();
        agencies.forEach(sAgency -> newFormatAgencyList.add(new Agency(sAgency.getId(), null, null, sAgency.getName(),
                sAgency.getInn(), sAgency.getEmail(), sAgency.getPhone())));
        format.setAgency(newFormatAgencyList);
    }

    private List<Publication> getPublicationParams(List<SPublication> publications, Format format, List<ConnectivityThematicEntity> thematicEntities,
                                                   List<STopicIn> topicInList, List<SIndex> indexList, List<SPrice> priceList) {
        List<Publication> publicationList = new ArrayList<>();
        publications.forEach(sPublication -> {
            SIndex index = getIndexByPublicationId(indexList, sPublication.getId());
            Publication publication = new Publication(getIdWithoutLetter(sPublication.getId()),
                    getPublicationTypeIdByName(sPublication.getType(), format.getPublType()),
                    sPublication.getName(),
                    sPublication.getAnnotation(),
                    getAgeFromName(sPublication.getName(), format.getAge()),
                    getCountryIdByName(sPublication.getCountry(), format.getCountry()),
                    null, //В тестовых данных исключительно с параметром "Центральное" в исходных данных
                    getLanguagesByName(sPublication.getLanguage(), format.getLanguage()),
                    getThematicByName(topicInList, sPublication.getId(), thematicEntities),
                    null,
                    null,
                    getInnFromAgency(format.getAgency(), index),
                    getNdsForPublication(priceList, format.getVat(), index),
                    null,
                    null,
                    null,
                    null);
            fillPublVersion(publication, sPublication, format.getFormat(), format.getTime());
            publicationList.add(publication);
        });
        return publicationList;
    }

    private void fillPublVersion(Publication publication, SPublication sPublication, List<Directory> format, List<Directory> time) {
        String[] formats = sPublication.getOutputDaysOfWeekFormat().split(",");
        List<PublVersion> publVersions = new ArrayList<>();
        Integer timed = getTimedId(sPublication.getPeriod(), time);
        for (int i = 0; i < formats.length; i++) {
            String[] weights = sPublication.getWeightDaysOfWeek().split(",");
            String[] pages = sPublication.getOutputDaysOfWeekBand().split(",");
            if (!formats[i].isEmpty() && !formats[i].equals("0")) {
                PublVersion publVersion = new PublVersion(
                        (publication.getId() * 100) + i ,
                        publication.getTitle(),
                        publication.getRegions(),
                        null,
                        0,
                        Integer.parseInt(weights[i]),
                        Integer.parseInt(pages[i]),
                        getFormatId(formats[i], format),
                        null,
                        null,
                        timed,
                        sPublication.getOutputCountForPeriod(),
                        getIssueForPublVersion(sPublication));
                publVersions.add(publVersion);
            }
        }
        publication.setPublVersion(publVersions);
    }

    private List<Issue> getIssueForPublVersion(SPublication sPublication) {
        List<Issue> issues = new ArrayList<>();
        String[] counts = sPublication.getOutputCountYear().split(",");
        for(int i = 1; i<=counts.length; i++) {
            issues.add(new Issue(i, Integer.parseInt(counts[i-1])));
        }
        return issues;
    }

    private Integer getTimedId(String period, List<Directory> time) {
        for(Directory timed : time) {
            if(timed.getName().substring(2).toUpperCase().equals(period.toUpperCase())) return timed.getId();
        }
        return null;
    }

    private Integer getFormatId(String oldFormat, List<Directory> format) {
        for (Directory form : format) {
            if (form.getName().toUpperCase().equals(oldFormat.toUpperCase())) return form.getId();
        }
        return format.get(1).getId();
    }

    private Long getIdWithoutLetter(String id) {
        Pattern pattern = Pattern.compile("[а-яА-Яa-zA-Z]");
        Matcher matcher = pattern.matcher(id);
        if (matcher.find()) {
            return Long.parseLong(matcher.replaceAll("0"));
        }
        return Long.parseLong(id);
    }

    private Integer getNdsForPublication(List<SPrice> priceList, List<Directory> vatDir, SIndex index) {
        if (index != null) {
            SPrice price = getPriceByIndexId(priceList, index.getId());
            if(price != null) {
                String[] vat = price.getPriceAndNDS().split(",");
                final Integer[] retVal = {0};
                vatDir.forEach(v -> {
                    if (v.getName().equals(vat[1])) {
                        retVal[0] = v.getId();
                    }
                });
                return retVal[0];
            }
        }
        return null;
    }

    private SPrice getPriceByIndexId(List<SPrice> priceList, String id) {
        for (SPrice price : priceList) {
            if (price.getIndexId().equals(id)) return price;
        }
        return null;
    }

    private Long getInnFromAgency(List<Agency> agency, SIndex index) {
        if (index != null) {
                Agency ag = agency.stream().filter(ags -> ags.getId() == index.getAgencyId()).findFirst().get();
                return ag.getInn() != null ? Long.parseLong(ag.getInn()) : null;
        }
        return null;
    }

    private SIndex getIndexByPublicationId(List<SIndex> indexList, String id) {
        for (SIndex sIndex : indexList) {
            if (sIndex.getPublicationId() != null && id != null) {
                if (sIndex.getPublicationId().equals(id)) return sIndex;
            }
        }
        return null;
    }

    private Integer[] getThematicByName(List<STopicIn> topicInList, String id, List<ConnectivityThematicEntity> thematicEntities) {
        List<Integer> thematicNumber = topicInList.stream().filter(sTopicIn -> sTopicIn.getPublicationId().equals(id))
                .map(data -> data.getRubricId()).collect(Collectors.toList());
        if (!thematicNumber.isEmpty()) {
            List<Integer> thematicIds = new ArrayList<>();
            for (Integer number : thematicNumber) {
                for (ConnectivityThematicEntity entity : thematicEntities) {
                    if (number == entity.getOldId()) {
                        for (Directory directory : entity.getDirectory()) {
                            thematicIds.add(directory.getId());
                        }
                    }
                }
            }
            return thematicIds.toArray(new Integer[0]);
        } else return null;
    }

    private Integer[] getLanguagesByName(String language, List<Directory> languages) {
        List<Integer> idList = new ArrayList<>();
        if (language.split(" ").length > 1) {
            String[] words = language.split(" ");
            for (int i = 0; i < words.length; i++) {
                if (!words[i].equals("и")) {
                    for (Directory lang : languages) {
                        if (lang.getName().toUpperCase().equals(words[i].trim().toUpperCase())) {
                            idList.add(lang.getId());
                            break;
                        }
                    }
                }
            }
        } else {
            for (Directory lang : languages) {
                if (lang.getName().toUpperCase().equals(language.toUpperCase())) {
                    idList.add(lang.getId());
                    break;
                }
            }
        }
        return idList.toArray(new Integer[0]);
    }

    private Integer getAgeFromName(String name, List<Directory> age) {
        Pattern pattern = Pattern.compile("\\([0-9]{1,2}\\+{1}\\)");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            return age.stream().filter(directory -> directory.getName().toUpperCase()
                    .equals(name.substring(matcher.start() + 1, matcher.end() - 1).toUpperCase())).findFirst().get().getId();
        } else return 0;
    }

    private Integer getCountryIdByName(String s, List<Directory> directories) {
        if (s.equals("Россия")) s = "Российская федерация";
        Integer id = null;
        for (Directory country : directories) {
            if (country.getName().toUpperCase().equals(s.toUpperCase())) {
                id = country.getId();
                break;
            }
        }
        return id;
    }

    private Integer getPublicationTypeIdByName(String s, List<Directory> publTypeList) {
        return publTypeList.stream().filter(directory -> directory.getName().toUpperCase()
                .equals(s.toUpperCase())).findFirst().get().getId();
    }

    public List<String> parseLine(String line) {
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

    public List<Accept> getAcceptList() {
       return parserDao.getAcceptList();
    }

    public List<ConnectivityThematicEntity> getConnectivityThematicFromDataBase(List<Directory> thematic) {
        List<ConnectivityThematicEntity> connectivityThematicEntities = new ArrayList<>();
        List<ConnectionThematic> connectionThematicList = parserDao.getConnectivityThematicEntities();
        for (ConnectionThematic connection : connectionThematicList) {
            connectivityThematicEntities.add(new ConnectivityThematicEntity(connection.getOldId(), connection.getOldName(),
                    getDirectors(thematic, connection.getNewIds())));
        }
        return connectivityThematicEntities;
    }

    private List<Directory> getDirectors(List<Directory> thematic, String newIds) {
        List<Directory> directories = new ArrayList<>();
        String[] ids = newIds.split(";");
        for (String id : ids) {
            for (Directory theme : thematic) {
                if (Integer.parseInt(id) == theme.getId()) {
                    directories.add(theme);
                    break;
                }
            }
        }
        return directories;
    }

    public void uploadConnectionThematicToDB(List<ConnectivityThematicEntity> connectivityThematicEntities) {
        List<ConnectionThematic> connectionThematicList = new ArrayList<>();
        for (ConnectivityThematicEntity thematic : connectivityThematicEntities) {
            connectionThematicList.add(new ConnectionThematic(thematic.getOldId(), thematic.getOldName(), thematic.getDirectoryId()));
        }
        parserDao.uploadConnectionData(connectionThematicList);
    }

    public void fillTerrainParams(List<SArea> areaList, Format endJson) {
        List<Terrain> terrains = new ArrayList<>();
        for (SArea area : areaList) {
            terrains.add(new Terrain(area.getId(), area.getName(), area.getId(), null));
        }
        endJson.setTerrain(terrains);
    }
}