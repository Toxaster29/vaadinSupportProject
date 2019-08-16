package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.EntityFromTable;
import com.packagename.myapp.spring.entity.TableMainData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelParserService {

    @Autowired
    private static TypeParseService parseService = new TypeParseService();

    public static List<EntityFromTable> readFromExcel(InputStream stream) throws IOException {
        Workbook workbook = new XSSFWorkbook(stream);
        Sheet datatypeSheet = workbook.getSheetAt(0);
        Iterator<Row> rows = datatypeSheet.rowIterator();
        List<EntityFromTable> entityList = new ArrayList<>();
        while (rows.hasNext()) {
            Row documentRow = rows.next();
            EntityFromTable entityFromTable = new EntityFromTable();
            Iterator<Cell> cellIterator = documentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                switch (currentCell.getColumnIndex()) {
                    case 0:
                        entityFromTable.setId(getContext(currentCell));
                        break;
                    case 1:
                        entityFromTable.setPayer(getContext(currentCell));
                        break;
                    case 2:
                        entityFromTable.setProvider(getContext(currentCell));
                        break;
                    case 3:
                        entityFromTable.setDocNumber(getContext(currentCell));
                        break;
                    case 4:
                        entityFromTable.setDocDate(getContext(currentCell));
                        break;
                }
            }
            entityList.add(entityFromTable);
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

}
