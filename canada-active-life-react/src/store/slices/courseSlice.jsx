import {createSlice} from "@reduxjs/toolkit";

const courseSlice = createSlice({
    name: 'courses',
    initialState: {
        offeredCourses: []
    },
    reducers: {
        populateOfferedCourses: (state, action) => {
            const offeredCoursesList = [...state.offeredCourses, ...action.payload.offeredCourses]
            return {
                ...state,
                offeredCourses: offeredCoursesList
            }
        }
    }
})

export const {
    populateOfferedCourses
} = courseSlice.actions

export default courseSlice.reducer