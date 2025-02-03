package org.reactivestax.canada_active_life.repo;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.reactivestax.canada_active_life.domain.*;
import org.springframework.data.jpa.domain.Specification;

public class OfferedCourseSpec {
    public static Specification<OfferedCourse> isAvailableForEnrollment(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("availableForEnrollment"), "OPEN");
    }

    public static Specification<OfferedCourse> inCity(String city){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Facility> facilityJoin = root.join("facility", JoinType.INNER);
            return criteriaBuilder.equal(facilityJoin.get("city"), city);
        };
    }

    public static Specification<OfferedCourse> inFacility(int facilityId){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Facility> facilityJoin = root.join("facility", JoinType.INNER);
            return criteriaBuilder.equal(facilityJoin.get("facilityId"), facilityId);
        };
    }

    public static Specification<OfferedCourse> forAgeGroup(int ageGroupId){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, AgeGroup>  courseAgeGroupJoin = courseCourseJoin.join("ageGroup", JoinType.INNER);
            return criteriaBuilder.equal(courseAgeGroupJoin.get("ageGroupId"), ageGroupId);
        };
    }

    public static Specification<OfferedCourse> inSubCategory(int subCategoryId){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, SubCategory>  courseSubCategoryJoin = courseCourseJoin.join("subCategory", JoinType.INNER);
            return criteriaBuilder.equal(courseSubCategoryJoin.get("subCategoryId"), subCategoryId);
        };
    }

    public static Specification<OfferedCourse> inCategory(int categoryId){
        return (root, query, criteriaBuilder) -> {
            Join<OfferedCourse, Course> courseCourseJoin = root.join("course", JoinType.INNER);
            Join<Course, SubCategory>  courseSubCategoryJoin = courseCourseJoin.join("subCategory", JoinType.INNER);
            Join<SubCategory, Category>  subCategoryCategoryJoin = courseSubCategoryJoin.join("category", JoinType.INNER);
            return criteriaBuilder.equal(subCategoryCategoryJoin.get("categoryId"), categoryId);
        };
    }


}
