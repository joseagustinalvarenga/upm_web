package com.upm.institutional.config;

import com.upm.institutional.model.Sede;
import com.upm.institutional.repository.SedeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

@Component
@Slf4j
public class SedeDataInitializer implements CommandLineRunner {

    private final SedeRepository sedeRepository;
    private static final String EXCEL_FILE_NAME = "SEDES UPM 2025.xlsx";

    public SedeDataInitializer(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        File file = new File(EXCEL_FILE_NAME);
        if (!file.exists()) {
            log.info("Excel file '{}' not found. Skipping sedes import.", EXCEL_FILE_NAME);
            return;
        }

        log.info("Starting import of sedes from '{}'...", EXCEL_FILE_NAME);

        try (FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowsProcessed = 0;
            int rowsSkipped = 0;

            // Start from row 1 to skip header
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    String nombre = getCellValue(row.getCell(0));
                    String direccion = getCellValue(row.getCell(1));
                    String telefono = getCellValue(row.getCell(2));

                    if (nombre.isEmpty()) {
                        rowsSkipped++;
                        continue;
                    }

                    saveOrUpdateSede(nombre, direccion, telefono);
                    rowsProcessed++;
                } catch (Exception e) {
                    log.error("Error processing row {}: {}", i, e.getMessage());
                    rowsSkipped++;
                }
            }
            log.info("Sedes import completed. Processed: {}, Skipped: {}", rowsProcessed, rowsSkipped);

        } catch (Exception e) {
            log.error("Failed to import sedes from Excel file successfully", e);
        }
    }

    private void saveOrUpdateSede(String name, String address, String phone) {
        Sede sede = sedeRepository.findByName(name)
                .orElse(new Sede());

        sede.setName(name);
        sede.setAddress(address);
        sede.setPhone(phone);

        // Set default values for other fields if it's a new sede
        if (sede.getId() == null) {
            sede.setDescription("Sede oficial de la Universidad Popular de Misiones en " + name);
        }

        sedeRepository.save(sede);
        log.debug("Saved sede: {}", name);
    }

    private String getCellValue(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Handle pure numbers (like phones entered as numbers)
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                // Convert double to string and remove .0 if it's an integer
                double value = cell.getNumericCellValue();
                if (value == (long) value) {
                    return String.format("%d", (long) value);
                }
                return String.valueOf(value);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }
}
