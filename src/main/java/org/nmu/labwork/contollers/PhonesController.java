package org.nmu.labwork.contollers;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nmu.labwork.models.ExchangeRate;
import org.nmu.labwork.services.ExcelService;
import org.nmu.labwork.services.ExchangeRateService;
import org.nmu.labwork.services.SiteParsingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/phones")
public class PhonesController {
    final SiteParsingService siteParsingService;
    final ExcelService excelService;
    final ExchangeRateService exchangeRateService;

    public PhonesController(SiteParsingService siteParsingService, ExcelService excelService, ExchangeRateService exchangeRateService) {
        this.siteParsingService = siteParsingService;
        this.excelService = excelService;
        this.exchangeRateService = exchangeRateService;
    }

    @SneakyThrows
    @GetMapping
    public void get(String search, HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=%s phones.xlsx".formatted(search));
        ExchangeRate exchange = exchangeRateService.getExchangeRate();
        try (ServletOutputStream stream = response.getOutputStream()) {
            try (XSSFWorkbook workbook = excelService.getWorkbookWithPhones(siteParsingService.getPhonesBySearch(search), exchange)) {
                workbook.write(stream);
            }
        }
    }
}

