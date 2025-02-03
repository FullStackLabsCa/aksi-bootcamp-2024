package org.reactivestax.canada_active_life.repo;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.reactivestax.canada_active_life.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class OfferedCourseSpec {

    private OfferedCourseSpec() {
    }

    public static Specification<OfferedCourse> isAvailableForEnrollment(String open) {
        return (root, query, criteriaBuilder) -> {
            if (open == null || open.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("availableForEnrollment"), open);
        };
    }

    public static Specification<OfferedCourse> inCities(List<String> cities){
        return (root, query, criteriaBuilder) -> {
            if (cities == null || cities.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<OfferedCourse, Facility> facilityJoin = root.join("facility", JoinType.INNER);
            return facilityJoin.get("city").in(cities);
        };
    }

    public static Specification<OfferedCourse> inFacilities(List<Integer> facilityIds){
        return (root, query, criteriaBuilder) -> {
            if (facilityIds == null || facilityIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<OfferedCourse, Facility> facilityJoin = root.join("facility", JoinType.INNER);
            return facilityJoin.get("facilityId").in(facilityIds);
        };
    }

    public static Specification<OfferedCourse> forAgeGroups(List<Integer> ageGroupIds){
        return (root, query, criteriaBuilder) -> {
            if (ageGroupIds == null || ageGroupIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, AgeGroup>  courseAgeGroupJoin = courseCourseJoin.join("ageGroup", JoinType.INNER);
            return courseAgeGroupJoin.get("ageGroupId").in(ageGroupIds);
        };
    }

    public static Specification<OfferedCourse> inSubCategories(List<Integer> subCategoryIds){
        return (root, query, criteriaBuilder) -> {
            if (subCategoryIds == null || subCategoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, SubCategory>  courseSubCategoryJoin = courseCourseJoin.join("subCategory", JoinType.INNER);
            return courseSubCategoryJoin.get("subCategoryId").in(subCategoryIds);
        };
    }

    public static Specification<OfferedCourse> inCategories(List<Integer> categoryIds){
        return (root, query, criteriaBuilder) -> {
            if (categoryIds == null || categoryIds.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, SubCategory>  courseSubCategoryJoin = courseCourseJoin.join("subCategory", JoinType.INNER);
            Join<SubCategory, Category>  subCategoryCategoryJoin = courseSubCategoryJoin.join("category", JoinType.INNER);
            return subCategoryCategoryJoin.get("categoryId").in(categoryIds);
        };
    }

    public static Specification<OfferedCourse> withinDates(LocalDate startDate, LocalDate endDate){
        return (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.and(
                        criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate),
                        criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate)
                );
            }
            else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
            }
            else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate);
            }
            return criteriaBuilder.conjunction();
        };
    }

}
