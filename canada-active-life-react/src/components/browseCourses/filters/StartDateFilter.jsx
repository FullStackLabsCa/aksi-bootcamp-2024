import DatePicker from "react-datepicker";
import * as PropTypes from "prop-types";

export default function StartDateFilter(props) {
    return <>
        <h6 className="text-muted mb-1 d-block">Start Date</h6>
        <DatePicker
            showMonthYearDropdown
            selected={props.filteredOfferedCoursesState.filterStartDate}
            onChange={props.onChange}
            className="form-control mb-3"
            dateFormat="yyyy-MM-dd"
            placeholderText="Choose a Date"
        />
    </>;
}

StartDateFilter.propTypes = {
    filteredOfferedCoursesState: PropTypes.any,
    onChange: PropTypes.func
};
