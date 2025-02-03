package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteriaDTO {

    private LocalDate startDate;
    private LocalDate endDate;

    private List<String> cities;
    private List<Integer> facilityIds;

    private String availableForEnrollment;

    private List<Integer> ageGroupIds;
    private List<Integer> categoryIds;
    private List<Integer> subCategoryIds;
}
