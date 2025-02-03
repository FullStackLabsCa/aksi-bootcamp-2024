package org.reactivestax.canada_active_life.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestax.canada_active_life.domain.AgeGroup;
import org.reactivestax.canada_active_life.domain.Category;
import org.reactivestax.canada_active_life.domain.Facility;
import org.reactivestax.canada_active_life.domain.SubCategory;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteriaDTO {

    private LocalDate startDate;
    private LocalDate endDate;

    private String city;
    private int facilityId;

    private String availableForEnrollment;

    private int ageGroupId;
    private int categoryId;
    private int subCategoryId;
}
