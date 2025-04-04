import {ToggleButton, ToggleButtonGroup} from "react-bootstrap";
import * as PropTypes from "prop-types";

export default function EnrollmentStatusFilter(props) {
    return <>
        <h6 className="text-muted mb-1 d-block">Availability for Enrollment</h6>
        <ToggleButtonGroup type="radio"
                           name="enrollmentStatus"
                           className="mb-3"
                           value={props.filteredOfferedCoursesState.filterEnrollmentStatus}
                           onChange={props.onChange}>
            <ToggleButton id="course-enrollment-all" value="">
                ALL
            </ToggleButton>
            <ToggleButton id="course-enrollment-open" value="open">
                OPEN
            </ToggleButton>
            <ToggleButton id="course-enrollment-closed" value="closed">
                CLOSED
            </ToggleButton>
        </ToggleButtonGroup>
    </>;
}

EnrollmentStatusFilter.propTypes = {
    filteredOfferedCoursesState: PropTypes.any,
    onChange: PropTypes.func
};