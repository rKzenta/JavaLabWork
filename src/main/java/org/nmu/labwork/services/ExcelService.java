package org.nmu.labwork.services;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.nmu.labwork.models.ExchangeRate;
import org.nmu.labwork.models.Phone;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ExcelService {
    public XSSFWorkbook getWorkbookWithPhones(List<Phone> phones, ExchangeRate exchangeRate) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Телефоны");

        XSSFRow headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Название");
        headerRow.createCell(1).setCellValue("Цена (гривна)");
        headerRow.createCell(2).setCellValue("Цена (доллар)");
        headerRow.createCell(3).setCellValue("Ссылка");

        int rowIndex = 1;
        for (Phone phone : phones) {
            XSSFRow row = sheet.createRow(rowIndex);
            row.createCell(0, CellType.STRING).setCellValue(phone.getName());
            row.createCell(1, CellType.NUMERIC).setCellValue(phone.getPrice());
            row.createCell(2, CellType.NUMERIC).setCellValue(BigDecimal.valueOf(phone.getPrice() / exchangeRate.getBuyRate()).setScale(2, RoundingMode.HALF_UP).doubleValue());

            XSSFCell cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(phone.getUrl());

            XSSFCellStyle cellStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setUnderline(XSSFFont.U_SINGLE);
            font.setColor(new XSSFColor(new byte[]{0,0, (byte) 255}));
            cellStyle.setFont(font);

            cell.setCellStyle(cellStyle);

            XSSFHyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(phone.getUrl());
            cell.setHyperlink(hyperlink);
            rowIndex++;
        }

        String startCell = new CellReference(0, 0).formatAsString();
        String endCell = new CellReference(rowIndex - 1, 3).formatAsString();
        AreaReference areaReference = new AreaReference(startCell + ":" + endCell, SpreadsheetVersion.EXCEL2007);

        XSSFTable table = sheet.createTable(areaReference);
        table.getCTTable().addNewAutoFilter();

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + (int) (2.5 * 256));
        }

        return workbook;
    }
}
