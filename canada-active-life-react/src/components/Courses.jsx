import Course from "./Course.jsx";
import {useDispatch} from "react-redux";
import {populateOfferedCourses} from "../store/slices/courseSlice.jsx";
import {useEffect, useReducer} from "react";

const initialState = {
    filteredOfferedCourses: [],
    filterWord: ''
}

function filterCoursesReducer(state, action) {
    switch (action.type) {
        case 'filter': {
            return {
                filterWord: action.filterWord,
                filteredOfferedCourses: action.offeredCourses.filter(course => course.title.includes(action.filterWord))
            }
        }
        case 'initFilteredList': {
            return {
                filteredOfferedCourses: action.offeredCourses
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

    useEffect(() => {
        fetchOfferedCourses(storeDispatch)
    }, []);

    const [state, filterDispatch] = useReducer(filterCoursesReducer, initialState)

    return (
        <>
            <h3>Offered Courses</h3>
            <input className="button"
                   type="text"
                   placeholder="Search Course"
                   value={state.filterWord}
                   onChange={(e) =>
                       filterDispatch({
                           type: 'filter',
                           filterWord: e.target.value,
                           offeredCourses: offeredCourses
                       })
                   }/>
            {state.filteredOfferedCourses.map((offeredCourse, index) => {
                return <Course key={index} course={offeredCourse}/>
            })}
        </>
    )
}

export default Courses