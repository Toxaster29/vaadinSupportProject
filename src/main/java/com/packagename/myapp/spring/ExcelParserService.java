package com.packagename.myapp.spring;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.Iterator;

@Service
public class ExcelParserService {


    public static void readFromExcel(InputStream stream) throws IOException{
        HSSFWorkbook myExcelBook = new HSSFWorkbook(stream);
        HSSFSheet myExcelSheet = myExcelBook.getSheetAt(0);
        Iterator<Row> rows = myExcelSheet.rowIterator();

        while(rows.hasNext()) {
            Row documentRow = rows.next();
            Iterator<Cell> cellIterator = documentRow.iterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                if (currentCell.getCellTypeEnum() == CellType.STRING) {
                    System.out.print(currentCell.getStringCellValue() + "--");
                } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                    System.out.print(currentCell.getNumericCellValue() + "--");
                }
            }
            System.out.println();
        }
        myExcelBook.close();
    }

}
