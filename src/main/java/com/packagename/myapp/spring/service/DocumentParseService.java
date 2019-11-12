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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DocumentParseService {

    @Autowired
    private ParserDao parserDao;

    public void fillCampaignParams(List<SPublication> publications, Format endJson, List<STopic> topicList, List<STopicIn> topicInList, List<SIndex> indexList, List<SPrice> priceList) {
        List<Campaign> campaignList = new ArrayList<>();
        List<Publication> publicationList = getPublicationParams(publications, endJson, topicList, topicInList, indexList, priceList);
        campaignList.add(new Campaign(
                (byte) 2020,
                (byte) 1,
                getAcceptParams(),
                publicationList,
                getCatalogParams(indexList, endJson, publications, priceList, publicationList)));
        endJson.setCampaign(campaignList);
    }

    private List<Accept> getAcceptParams() {
        List<Accept> acceptList = new ArrayList<>();

        return acceptList;
    }

    private List<Catalog> getCatalogParams(List<SIndex> indexList, Format endJson, List<SPublication> publications
            , List<SPrice> priceList, List<Publication> publicationList) {
        List<Catalog> catalogList = new ArrayList<>();
        indexList.forEach(sIndex -> {
            SPublication sPublication = getPublicationById(sIndex.getPublicationId(), publications);
            catalogList.add(new Catalog(
                    sIndex.getId(),
                    sIndex.getComplexName(),
                    null,
                    sIndex.getAgencyId(),
                    null,
                    getExpeditionIdByName(sIndex.getSystem(), endJson.getExpedition()),
                    null,
                    null,
                    getTermByPublicationData(sPublication),
                    getSubVersion(publicationList, sPublication),
                    getSubVariant(sIndex.getId(), priceList, endJson.getVat())));
        });
        return catalogList;
    }

    private List<SubsVariant> getSubVariant(String id, List<SPrice> priceList, List<Directory> vat) {
        List<SubsVariant> subsVariants = new ArrayList<>();
        for (SPrice price : priceList) {
            if (price.getIndexId().equals(id)) {
                subsVariants.add(new SubsVariant(
                        null,
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

    private List<SubsVersion> getSubVersion(List<Publication> publications, SPublication sPublication) {
        List<SubsVersion> subsVersions = new ArrayList<>();
        Publication publication = null;
        if (sPublication != null) {
            for (Publication publ : publications) {
                if (publ.getId().equals(getIdWithoutLetter(sPublication.getId()))) {
                    publication = publ;
                    break;
                }
            }
            if (publication != null) {
                Publication finalPublication = publication;
                publication.getPublVersion().forEach(version -> {
                    subsVersions.add(new SubsVersion(finalPublication.getId(), version.getId())); //TODO разобраться с идентификаторами у PublVersion
                });
            }
        }
        return subsVersions;
    }

    private List<Term> getTermByPublicationData(SPublication sPublication) {
        List<Term> terms = new ArrayList<>();
        if (sPublication != null) {
            Byte mspMonth = 1;
            Integer year = 2020; //TODO значения нужно брать из компании или иных объектов в дальнейшем
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
            if (exp.getName().toUpperCase().equals(system.toUpperCase())) return exp.getId();
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

    private List<Publication> getPublicationParams(List<SPublication> publications, Format format, List<STopic> topicList,
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
                    getThematicByName(topicList, topicInList, sPublication.getId(), format.getThematic()),
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
            getThematicByName(topicList, topicInList, sPublication.getId(), format.getThematic());
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
                        publication.getId(),
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

    private Integer[] getThematicByName(List<STopic> topicList, List<STopicIn> topicInList, String id, List<Directory> thematic) {
        List<Integer> thematicNumber = topicInList.stream().filter(sTopicIn -> sTopicIn.getPublicationId().equals(id))
                .map(data -> data.getRubricId()).collect(Collectors.toList());
        if (!thematicNumber.isEmpty()) {
            List<Integer> thematicIds = new ArrayList<>();
            List<Integer> finalThematicIds = thematicIds;
            thematicNumber.forEach(data -> {
                String topic = topicList.stream().filter(sTopic -> sTopic.getRubricId() == data).findFirst().get().getRubricName();
                Directory theme = thematic.stream().filter(them -> them.getName()
                        .toUpperCase().equals(topic.toUpperCase())).findFirst().orElse(null);
                if (theme == null) {
                    thematic.forEach(th -> {
                            if (th.getName().contains(".")) {
                                String[] words = topic.split("\\.");
                                String[] wordsTh = th.getName().split("\\.");
                                for (String word : words) {
                                        for (String wordTh : wordsTh) {
                                            if (word.trim().toUpperCase().equals(wordTh.trim().toUpperCase())) {
                                                finalThematicIds.add(th.getId());
                                            }
                                        }
                                    }
                            } else {
                                if (th.getName().toUpperCase().equals(topic.toUpperCase())) {
                                    finalThematicIds.add(th.getId());
                                }
                            }
                    });
                } else finalThematicIds.add(theme.getId());
            });
            thematicIds = new ArrayList<>(new LinkedHashSet<>(thematicIds));
            if (thematicIds.isEmpty()) {
                finalThematicIds.add(614); //TODO Доработать добавление тем это щас заглушка если тем вообще не нашло
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

}