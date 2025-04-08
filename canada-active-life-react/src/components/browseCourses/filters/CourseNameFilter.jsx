import * as PropTypes from "prop-types";

export default function CourseNameFilter(props) {
    return <input className="form-control mb-3"
                  type="text"
                  placeholder="Course Name"
                  value={props.filteredOfferedCoursesState.filterWord ?? ""}
                  onChange={props.onChange
                  }/>;
}

CourseNameFilter.propTypes = {
    filteredOfferedCoursesState: PropTypes.any,
    onChange: PropTypes.func
};