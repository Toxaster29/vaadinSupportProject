package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.EntityFromTable;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelParserService {


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

    public static List<EntityFromTable> readFromExcelSecond(InputStream stream) throws IOException {
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

    private static String getContext(Cell currentCell) {
        if (currentCell.getCellTypeEnum() == CellType.STRING) {
            return currentCell.getStringCellValue();
        } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
            String.valueOf(currentCell.getNumericCellValue());
        }
        return  String.valueOf(currentCell.getDateCellValue());
    }

}
