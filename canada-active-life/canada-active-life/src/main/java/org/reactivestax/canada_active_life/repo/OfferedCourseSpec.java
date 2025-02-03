package org.reactivestax.canada_active_life.repo;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.reactivestax.canada_active_life.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class OfferedCourseSpec {
    public static Specification<OfferedCourse> isAvailableForEnrollment(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("availableForEnrollment"), "OPEN");
    }

    public static Specification<OfferedCourse> inCities(List<String> cities){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Facility> facilityJoin = root.join("facility", JoinType.INNER);
            return facilityJoin.get("city").in(cities);
        };
    }

    public static Specification<OfferedCourse> inFacilities(List<Integer> facilityIds){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Facility> facilityJoin = root.join("facility", JoinType.INNER);
            return facilityJoin.get("facilityId").in(facilityIds);
        };
    }

    public static Specification<OfferedCourse> forAgeGroups(List<Integer> ageGroupIds){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, AgeGroup>  courseAgeGroupJoin = courseCourseJoin.join("ageGroup", JoinType.INNER);
            return courseAgeGroupJoin.get("ageGroupId").in(ageGroupIds);
        };
    }

    public static Specification<OfferedCourse> inSubCategories(List<Integer> subCategoryIds){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, SubCategory>  courseSubCategoryJoin = courseCourseJoin.join("subCategory", JoinType.INNER);
            return courseSubCategoryJoin.get("subCategoryId").in(subCategoryIds);
        };
    }

    public static Specification<OfferedCourse> inCategories(List<Integer> categoryIds){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, SubCategory>  courseSubCategoryJoin = courseCourseJoin.join("subCategory", JoinType.INNER);
            Join<SubCategory, Category>  subCategoryCategoryJoin = courseSubCategoryJoin.join("category", JoinType.INNER);
            return subCategoryCategoryJoin.get("categoryId").in(categoryIds);
        };
    }


}
