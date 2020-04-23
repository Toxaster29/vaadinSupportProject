package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.dto.ExcelParserDao;
import com.packagename.myapp.spring.entity.contract.ContractEntity;
import com.packagename.myapp.spring.entity.contract.EntityFromTable;
import com.packagename.myapp.spring.entity.contract.TableMainData;
import com.packagename.myapp.spring.entity.excelParser.PublisherFromExcel;
import com.packagename.myapp.spring.entity.schedule.PublisherSchedule;
import com.packagename.myapp.spring.entity.schedule.ScheduleDates;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class ExcelParserService {

    @Autowired
    private static TypeParseService parseService = new TypeParseService();

    @Autowired
    ExcelParserDao excelParserDao;

    private int publisherIdCell = 0;
    private int contractIdCell = 1;
    private int dateEarlyStart = 2;
    private int dateMainStart = 5;
    private int dateCurrentMonthStart = 9;

    public static List<EntityFromTable> readFromExcel(InputStream stream, String value, String docNumberFieldValue, String yearFieldValue,
                                                      String halfFieldValue, String payer, String startValue) throws IOException {
        Workbook workbook = new XSSFWorkbook(stream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> rows = datatypeSheet.rowIterator();
        List<EntityFromTable> entityList = new ArrayList<>();
        while (rows.hasNext()) {
            Row documentRow = rows.next();
            if (Integer.parseInt(startValue) <= documentRow.getRowNum()) {
                EntityFromTable entityFromTable = new EntityFromTable();
                if (documentRow.getCell(Integer.parseInt(value)).getCellTypeEnum().equals(CellType.STRING)) {
                    entityFromTable.setId(documentRow.getCell(Integer.parseInt(value)).getStringCellValue());
                } else if (documentRow.getCell(Integer.parseInt(value)).getCellTypeEnum().equals(CellType.NUMERIC)) {
                    entityFromTable.setId(String.valueOf(BigDecimal.valueOf(documentRow.getCell(Integer.parseInt(value)).getNumericCellValue()).setScale(0)));
                }
                entityFromTable.setDocNumber(documentRow.getCell(Integer.parseInt(docNumberFieldValue)).getStringCellValue());
                entityFromTable.setPayer(documentRow.getCell(Integer.parseInt(payer)).getStringCellValue());
                entityFromTable.setYear(Integer.parseInt(yearFieldValue));
                entityFromTable.setHalf(Integer.parseInt(halfFieldValue));
                entityList.add(entityFromTable);
            }
        }
        workbook.close();
        return entityList;
    }

    public static List<TableMainData> readFromExcelSecond(InputStream stream) throws IOException {
        Workbook workbook = new XSSFWorkbook(stream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> rows = datatypeSheet.rowIterator();
        List<TableMainData> entityList = new ArrayList<>();
        while (rows.hasNext()) {
            Row documentRow = rows.next();
            TableMainData fromTable = new TableMainData();
            Iterator<Cell> cellIterator = documentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                switch (currentCell.getColumnIndex()) {
                    case 2:
                        fromTable.setId(getContext(currentCell));
                        break;
                    case 5:
                        fromTable.setDocumentNumber(getContext(currentCell));
                        break;
                    case 6:
                        fromTable.setDocumentDate(getContext(currentCell) != null
                                ? parseDate(getContext(currentCell)) : null);
                        break;
                    case 7:
                        fromTable.setFirstPeriod(getContext(currentCell));
                        break;
                    case 8:
                        fromTable.setSecondPeriod(getContext(currentCell));
                        break;
                }
            }
            entityList.add(fromTable);
        }
        workbook.close();
        return entityList;
    }

    private static LocalDate parseDate(String context) {
        return parseService.parseDate(context);
    }

    private static String getContext(Cell currentCell) {
        if (currentCell.getCellTypeEnum() == CellType.STRING) {
            return currentCell.getStringCellValue();
        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
            String.valueOf(currentCell.getNumericCellValue());
        }
        return  String.valueOf(currentCell.getDateCellValue());
    }

    public List<PublisherSchedule> readFromExcelSchedules(InputStream inputStream, int startRow) throws IOException {
        List<PublisherSchedule> schedules = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> rows = datatypeSheet.rowIterator();
        while (rows.hasNext()) {
            Row documentRow = rows.next();
            if (documentRow.getRowNum() >= startRow) {
                PublisherSchedule publisherSchedule = new PublisherSchedule(getPublisherId(documentRow.getCell(publisherIdCell)),
                        getContractId(documentRow.getCell(contractIdCell)), null);
                List<ScheduleDates> scheduleDates = new ArrayList<>();
                scheduleDates.add(createDateByParams("EARLY", null, dateEarlyStart, documentRow));
                scheduleDates.add(createDateByParams("MAIN", null, dateMainStart, documentRow));
                scheduleDates.add(createDateByParams("CURRENT", "FEBRUARY", dateCurrentMonthStart, documentRow));
                scheduleDates.add(createDateByParams("CURRENT", "MARCH", dateCurrentMonthStart + 4 , documentRow));
                scheduleDates.add(createDateByParams("CURRENT", "APRIL", dateCurrentMonthStart + 8 , documentRow));
                scheduleDates.add(createDateByParams("CURRENT", "MAY", dateCurrentMonthStart + 12 , documentRow));
                scheduleDates.add(createDateByParams("CURRENT", "JUNE", dateCurrentMonthStart + 16 , documentRow));
                publisherSchedule.setDates(scheduleDates);
                schedules.add(publisherSchedule);
            }
        }
        return schedules;
    }

    private LocalDate convertToLocalDate(Cell dateToConvert) {
        if (dateToConvert.getCellTypeEnum().equals(CellType.STRING)) {
            return LocalDate.parse(dateToConvert.getStringCellValue());
        }
        return dateToConvert.getDateCellValue().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private ScheduleDates createDateByParams(String name, String month, int start, Row documentRow) {
        LocalDate docGeneration = convertToLocalDate(documentRow.getCell(start));
        LocalDate tfpsDate = convertToLocalDate(documentRow.getCell(start + 1));
        LocalDate onlineDate = convertToLocalDate(documentRow.getCell(start + 2));
        LocalDate publisherDate = convertToLocalDate(documentRow.getCell(start + 3));
        return new ScheduleDates(name, month, docGeneration, tfpsDate, onlineDate, name.equals("EARLY") ? null : publisherDate);
    }

    private Integer getContractId(Cell cell) {
        if (cell.getCellTypeEnum().equals(CellType.STRING)) {
            if (!cell.getStringCellValue().equals("-")) {
                return Integer.parseInt(cell.getStringCellValue());
            } else return null;
        } else return ((int) cell.getNumericCellValue());
    }

    private String getPublisherId(Cell cell) {
        if (cell.getCellTypeEnum().equals(CellType.STRING)) {
            return cell.getStringCellValue();
        } else return String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()).setScale(0));

    }

    public List<PublisherFromExcel> readPublisherDataFromExcel(InputStream inputStream) throws IOException {
        List<PublisherFromExcel> publisherFromExcelList = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> rows = datatypeSheet.rowIterator();
        while (rows.hasNext()) {
            Row documentRow = rows.next();
            if (documentRow.getRowNum() > 0) {
                Iterator<Cell> cellIterator = documentRow.iterator();
                PublisherFromExcel fromExcel = new PublisherFromExcel();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    switch (currentCell.getColumnIndex()) {
                        case 1:
                            fromExcel.setPublisherName(currentCell.getStringCellValue());
                            break;
                        case 2:
                            if (currentCell.getCellTypeEnum().equals(CellType.STRING)) {
                                fromExcel.setPrice(Integer.parseInt(currentCell.getStringCellValue()));
                            } else fromExcel.setPrice((int) currentCell.getNumericCellValue());
                            break;
                        case 3:
                            if (currentCell.getCellTypeEnum().equals(CellType.STRING)) {
                                fromExcel.setInn(currentCell.getStringCellValue());
                            } else fromExcel.setInn(String.valueOf(currentCell.getNumericCellValue()));
                            break;
                        case 4:
                            if (currentCell.getCellTypeEnum().equals(CellType.STRING)) {
                                fromExcel.setHid(currentCell.getStringCellValue().trim());
                            } else fromExcel.setHid(String.valueOf(BigDecimal.valueOf(currentCell.getNumericCellValue()).setScale(0)));
                            break;
                        case 5:
                            fromExcel.setManager(currentCell.getStringCellValue());
                            break;
                        case 6:
                            fromExcel.setContractNumber(currentCell.getStringCellValue());
                    }
                }
                if (fromExcel.getPublisherName() == null) {
                    break;
                } else publisherFromExcelList.add(fromExcel);
            }
        }
        return publisherFromExcelList;
    }

    public void setNmcByExcelData(List<PublisherFromExcel> publisherFromExcelList) {
        List<ContractEntity> contractList = excelParserDao.getContractForNmcUpdate(2020, 2);
        Map<String, List<ContractEntity>> contractMapByPublisher = contractList.stream().collect(groupingBy(ContractEntity::getLegalHid));
        publisherFromExcelList.forEach(publisher -> {
            publisher.setContractId(selectContractFromMap(contractMapByPublisher, publisher.getHid(), publisher.getContractNumber()));
        });
        Map<Integer, List<PublisherFromExcel>> groupPublisherMap = publisherFromExcelList.stream()
                .filter(publisher -> publisher.getContractId() > 0).collect(groupingBy(PublisherFromExcel::getPrice));
        for (Map.Entry<Integer, List<PublisherFromExcel>> entry : groupPublisherMap.entrySet()) {
            Set<Integer> ids = new LinkedHashSet<>(entry.getValue().stream().map(PublisherFromExcel::getContractId)
                    .collect(Collectors.toList()));
            excelParserDao.updateNmc(entry.getKey(), ids);
        }
        System.out.println("Complete");
    }

    private Integer selectContractFromMap(Map<String, List<ContractEntity>> contractMapByPublisher, String hid, String contractNumber) {
        List<ContractEntity> contractList = contractMapByPublisher.get(hid);
        if (contractList != null) {
            List<ContractEntity> contractEntitiesByContractNumber = contractList.stream()
                    .filter(contract -> contract.getDocNumber().equals(contractNumber)).collect(Collectors.toList());
            if (contractEntitiesByContractNumber.size() == 1) {
                return contractEntitiesByContractNumber.get(0).getId();
            } else {
                System.out.println("Издатель: " + hid + " не найдено контрактов с №" + contractNumber);
            }
        } else {
            System.out.println("Издатель: " + hid + " нет контрактов");
        }
        return 0;
    }
}
