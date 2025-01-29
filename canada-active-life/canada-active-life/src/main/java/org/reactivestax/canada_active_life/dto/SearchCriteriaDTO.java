package org.reactivestax.canada_active_life.dto;

import lombok.Data;
import org.reactivestax.canada_active_life.domain.AgeGroup;
import org.reactivestax.canada_active_life.domain.Category;
import org.reactivestax.canada_active_life.domain.Facility;
import org.reactivestax.canada_active_life.domain.SubCategory;

import java.time.LocalDate;

@Data
public class SearchCriteriaDTO {

    private LocalDate startDate;
    private LocalDate endDate;

    private String city;
    private Facility facility;

    private AgeGroup ageGroup;
    private Category category;
    private SubCategory subCategory;
}
