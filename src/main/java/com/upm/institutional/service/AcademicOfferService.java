package com.upm.institutional.service;

import com.upm.institutional.model.AcademicOffer;
import com.upm.institutional.repository.AcademicOfferRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicOfferService {

    private final AcademicOfferRepository repository;

    public List<AcademicOffer> findAll() {
        return repository.findAll(Sort.by("area", "course"));
    }

    public List<String> findDistinctAreas() {
        return repository.findDistinctAreas();
    }

    public List<AcademicOffer> search(String area, String query) {
        if (area != null && !area.isEmpty() && query != null && !query.isEmpty()) {
            // Filter by both
            // This is a simple in-memory filter or could be a custom query.
            // For simplicity and since we have dynamic queries, let's do a stream filter or
            // simple repository method if possible.
            // But let's stick to repository methods for cleanness.
            // We don't have a specific method for both, so let's use stream for now or add
            // a custom query.
            // Let's rely on finding all and filtering in stream for flexibility if dataset
            // is small,
            // or better, implement a specification.
            // Given the requirement, exact match for area and partial for query.
            List<AcademicOffer> all = repository.findAll();
            return all.stream()
                    .filter(o -> o.getArea().equalsIgnoreCase(area))
                    .filter(o -> o.getCourse().toLowerCase().contains(query.toLowerCase()))
                    .toList();
        } else if (area != null && !area.isEmpty()) {
            return repository.findByAreaContainingIgnoreCase(area);
        } else if (query != null && !query.isEmpty()) {
            return repository.findByAreaContainingIgnoreCaseOrCourseContainingIgnoreCase(query, query);
        } else {
            return findAll();
        }
    }

    @Transactional
    public void saveFromExcel(MultipartFile file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<AcademicOffer> offers = new ArrayList<>();

            // Iterate through rows, skipping header
            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue; // Skip header

                // Check if row is empty
                if (isRowEmpty(row))
                    continue;

                String area = getCellValueAsString(row.getCell(0));
                String course = getCellValueAsString(row.getCell(1));
                String classCount = getCellValueAsString(row.getCell(2));

                if (area != null && !area.isEmpty() && course != null && !course.isEmpty()) {
                    AcademicOffer offer = AcademicOffer.builder()
                            .area(area)
                            .course(course)
                            .classCount(classCount != null ? classCount : "")
                            .build();
                    offers.add(offer);
                }
            }

            if (!offers.isEmpty()) {
                repository.deleteAll(); // Requirement implies replacing the list? "cargar un excel... para cargarlo".
                                        // Usually implies update or replace. Let's replace for simplicity as it
                                        // effectively "reloads" the offer.
                repository.saveAll(offers);
            }
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null)
            return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
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
