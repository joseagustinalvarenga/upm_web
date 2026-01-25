package com.upm.institutional.service;

import com.upm.institutional.model.Professional;
import com.upm.institutional.repository.ProfessionalRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    public Page<Professional> findAll(Pageable pageable) {
        return professionalRepository.findAll(pageable);
    }

    public Page<Professional> search(String profession, String locality, Pageable pageable) {
        if ((profession == null || profession.isEmpty()) && (locality == null || locality.isEmpty())) {
            return professionalRepository.findAll(pageable);
        }
        // Basic implementation: filtering by containing strings
        // Ideally use Specifications for more complex dynamic queries
        return professionalRepository.findByProfessionContainingIgnoreCaseOrNameContainingIgnoreCase(
                profession != null ? profession : "",
                profession != null ? profession : "", // Searching name in profession field for simplicity or use
                                                      // Specifications
                pageable);
    }

    // Better search method using Specifications could be implemented here

    public Professional save(Professional professional) {
        return professionalRepository.save(professional);
    }

    public void delete(Long id) {
        professionalRepository.deleteById(id);
    }

    public Professional findById(Long id) {
        return professionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
    }

    public void importFromExcel(MultipartFile file) throws IOException {
        List<Professional> professionals = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header row if present
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                Professional professional = new Professional();

                // Assuming columns: Name | Profession | Phone | Locality
                Cell nameCell = currentRow.getCell(0);
                if (nameCell != null) {
                    professional.setName(getCellValueAsString(nameCell));
                }

                Cell professionCell = currentRow.getCell(1);
                if (professionCell != null) {
                    professional.setProfession(getCellValueAsString(professionCell));
                }

                Cell phoneCell = currentRow.getCell(2);
                if (phoneCell != null) {
                    professional.setPhone(getCellValueAsString(phoneCell));
                }

                Cell localityCell = currentRow.getCell(3);
                if (localityCell != null) {
                    professional.setLocality(getCellValueAsString(localityCell));
                }

                if (professional.getName() != null && !professional.getName().isEmpty()) {
                    professionals.add(professional);
                }
            }
        }
        professionalRepository.saveAll(professionals);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Avoid scientific notation for phone numbers
                    return String.format("%.0f", cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
