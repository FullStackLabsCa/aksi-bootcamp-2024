import Course from "./Course.jsx";
import {useDispatch, useSelector} from "react-redux";
import {populateOfferedCourses} from "../../store/slices/courseSlice.jsx";
import {useEffect, useReducer} from "react";
import CourseList from "./CourseList.jsx";
import CourseNameFilter from "./filters/CourseNameFilter.jsx";
import EnrollmentStatusFilter from "./filters/EnrollmentStatusFilter.jsx";
import DatePicker from "react-datepicker";
import * as PropTypes from "prop-types";
import StartDateFilter from "./filters/StartDateFilter.jsx";
import EndDateFilter from "./filters/EndDateFilter.jsx";

const initialState = {
    previousState: {},
    filteredOfferedCourses: [],
    filterWord: '',
    filterEnrollmentStatus: '',
    filterStartDate: null,
    filterEndDate: null
}

function filterCoursesReducer(state, action) {
    const previousState = action.previousState
    const filterEnrollmentStatus = (action.filterEnrollmentStatus !== undefined) ? action.filterEnrollmentStatus : previousState.filterEnrollmentStatus
    const filterWord = (action.filterWord !== undefined) ? action.filterWord : previousState.filterWord
    const filterStartDate = (action.filterStartDate !== undefined) ? action.filterStartDate : previousState.filterStartDate
    const filterEndDate = (action.filterEndDate !== undefined) ? action.filterEndDate : previousState.filterEndDate
    switch (action.type) {
        case 'filter': {
            return {
                previousState: {
                    ...previousState,
                    filterWord: filterWord,
                    filterEnrollmentStatus: filterEnrollmentStatus,
                    filterStartDate: filterStartDate,
                    filterEndDate: filterEndDate
                },
                filterWord: filterWord,
                filterEnrollmentStatus: filterEnrollmentStatus,
                filterStartDate: filterStartDate,
                filterEndDate: filterEndDate,
                filteredOfferedCourses:
                    action.offeredCourses
                        .filter(course => course.course.name.toLowerCase().includes(filterWord.toLowerCase()))
                        .filter(course => (course.availableForEnrollment.toLowerCase().includes(filterEnrollmentStatus)))
                // TODO Add a filter for Start Date and End Date
            }
        }
        case 'initFilteredList': {
            return {
                filteredOfferedCourses: action.offeredCourses,
                previousState: action.previousState
            }
        }
        default:
            throw new Error("Unknown Action: ${action.type}")
    }
}

const fetchOfferedCourses = async (storeDispatch) => {
    try {
        const response = await fetch('http://localhost:30002/CanadaActiveLife/v1/browse_offered_courses', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({})
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const jsonData = await response.json()
        storeDispatch(populateOfferedCourses({offeredCourses: jsonData}))
    } catch (error) {
        console.log(error)
    }
}

function Courses() {
    const storeDispatch = useDispatch()
    const offeredCourses = useSelector(state => state.courses.offeredCourses)
    const [filteredOfferedCoursesState, filterDispatch] = useReducer(filterCoursesReducer, initialState)

    useEffect(() => {
        fetchOfferedCourses(storeDispatch)
    }, []);

    useEffect(() => {
        if (offeredCourses.length > 0) {
            filterDispatch({
                type: 'initFilteredList',
                offeredCourses: offeredCourses,
                previousState: initialState
            });
        }
    }, [offeredCourses]);

    return (
        <div className="d-flex flex-column align-items-center" style={{minHeight: '100vh'}}>
            <h3>Offered Courses</h3>
            <div className="container">
                <div className="row">
                    <div className="col-md-3">
                        <h3>Filters</h3>
                        <CourseNameFilter filteredOfferedCoursesState={filteredOfferedCoursesState}
                                          onChange={(e) =>
                                              filterDispatch({
                                                  type: 'filter',
                                                  filterWord: e.target.value,
                                                  offeredCourses: offeredCourses,
                                                  previousState: filteredOfferedCoursesState.previousState
                                              })}/>
                        <EnrollmentStatusFilter filteredOfferedCoursesState={filteredOfferedCoursesState}
                                                onChange={(value) =>
                                                    filterDispatch({
                                                        type: 'filter',
                                                        filterEnrollmentStatus: value,
                                                        offeredCourses: offeredCourses,
                                                        previousState: filteredOfferedCoursesState.previousState
                                                    })}/>
                        <StartDateFilter filteredOfferedCoursesState={filteredOfferedCoursesState}
                                         onChange={(date) =>
                                             filterDispatch({
                                                 type: 'filter',
                                                 filterStartDate: date,
                                                 offeredCourses: offeredCourses,
                                                 previousState: filteredOfferedCoursesState.previousState
                                             })}/>
                        <EndDateFilter filteredOfferedCoursesState={filteredOfferedCoursesState}
                                       onChange={(date) =>
                                           filterDispatch({
                                               type: 'filter',
                                               filterEndDate: date,
                                               offeredCourses: offeredCourses,
                                               previousState: filteredOfferedCoursesState.previousState
                                           })}/>
                    </div>

                    <CourseList filteredOfferedCoursesState={filteredOfferedCoursesState}
                                prop1={(offeredCourse, index) => (
                                    <div className="col-sm-12 col-md-6 col-lg-4 mb-4" key={index}>
                                        <Course course={offeredCourse}/>
                                    </div>
                                )}/>
                </div>
            </div>
        </div>
    )
}

export default Courses