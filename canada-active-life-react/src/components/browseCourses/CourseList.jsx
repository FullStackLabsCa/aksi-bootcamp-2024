import * as PropTypes from "prop-types";

export default function CourseList(props) {
    return <div className="col-md-9">
        <div className="row">
            {props.filteredOfferedCoursesState.filteredOfferedCourses.map(props.prop1)}
        </div>
    </div>;
}

CourseList.propTypes = {
    filteredOfferedCoursesState: PropTypes.any,
    prop1: PropTypes.func
};