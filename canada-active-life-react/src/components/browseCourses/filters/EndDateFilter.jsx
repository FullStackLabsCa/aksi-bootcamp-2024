import DatePicker from "react-datepicker";
import * as PropTypes from "prop-types";

export default function EndDateFilter(props) {
    return <>
        <h6 className="text-muted mb-1 d-block">End Date</h6>
        <DatePicker
            showMonthYearDropdown
            selected={props.filteredOfferedCoursesState.filterEndDate}
            onChange={props.onChange}
            className="form-control mb-3"
            dateFormat="yyyy-MM-dd"
            placeholderText="Choose a Date"
        />
    </>;
}

EndDateFilter.propTypes = {
    filteredOfferedCoursesState: PropTypes.any,
    onChange: PropTypes.func
};