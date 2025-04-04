import CourseNameFilter from "./filters/CourseNameFilter.jsx";
import EnrollmentStatusFilter from "./filters/EnrollmentStatusFilter.jsx";
import * as PropTypes from "prop-types";

export default function BrowseFilters(props) {
    return <div className="col-md-3">
        <h3>Filters</h3>
        <CourseNameFilter filteredOfferedCoursesState={props.filteredOfferedCoursesState} onChange={props.onCourseNameFilterChange}/>
        <EnrollmentStatusFilter filteredOfferedCoursesState={props.filteredOfferedCoursesState}
                                onChange={props.onEnrollmentStatusFilterChange}/>

    </div>;
}

BrowseFilters.propTypes = {
    filteredOfferedCoursesState: PropTypes.any,
    onCourseNameFilterChange: PropTypes.func,
    onEnrollmentStatusFilterChange: PropTypes.func
};