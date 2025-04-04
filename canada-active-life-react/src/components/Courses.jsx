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
            return {
                filterWord: action.filterWord,
                filteredOfferedCourses: action.offeredCourses.filter(course => course.course.name.includes(action.filterWord))
            }
        }
        case 'initFilteredList': {
            return {filteredOfferedCourses: action.offeredCourses}
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
                offeredCourses: offeredCourses
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
                        <input className="form-control"
                               type="text"
                               placeholder="Course Name"
                               value={filteredOfferedCoursesState.filterWord}
                               onChange={(e) =>
                                   filterDispatch({
                                       type: 'filter',
                                       filterWord: e.target.value,
                                       offeredCourses: offeredCourses
                                   })
                               }/>
                    </div>

                    <div className="col-md-9">
                        <div className="row">
                            {filteredOfferedCoursesState.filteredOfferedCourses.map((offeredCourse, index) => (
                                <div className="col-sm-12 col-md-6 col-lg-4 mb-4" key={index}>
                                    <Course course={offeredCourse} />
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Courses