import Course from "./Course.jsx";
import {useDispatch, useSelector} from "react-redux";
import {populateOfferedCourses} from "../store/slices/courseSlice.jsx";
import {useEffect, useReducer} from "react";

const initialState = {
    filteredOfferedCourses: [],
    filterWord: ''
}

function filterCoursesReducer(state, action) {
    switch (action.type) {
        case 'filter': { // TODO
            return {filterWord: action.filterWord,
                filteredOfferedCourses: action.offeredCourses.filter(course => course.course.name.includes(action.filterWord))}}
        case 'initFilteredList': {
            return {filteredOfferedCourses: action.offeredCourses}}
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
            body: JSON.stringify({
                availableForEnrollment: "OPEN"
            })
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
                offeredCourses: offeredCourses
            });
        }
    }, [offeredCourses]);

    return (
        <>
            <h3>Offered Courses</h3>
            <input className="button"
                   type="text"
                   placeholder="Search Course"
                   value={filteredOfferedCoursesState.filterWord}
                   onChange={(e) =>
                       filterDispatch({
                           type: 'filter',
                           filterWord: e.target.value,
                           offeredCourses: offeredCourses
                       })
                   }/>
            {filteredOfferedCoursesState.filteredOfferedCourses.map((offeredCourse, index) => {
                return <Course key={index} course={offeredCourse}/>
            })}
        </>
    )
}

export default Courses