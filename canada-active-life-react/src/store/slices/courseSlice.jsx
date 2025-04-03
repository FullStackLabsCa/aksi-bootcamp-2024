import {createSlice} from "@reduxjs/toolkit";

const courseSlice = createSlice({
    name: 'courses',
    initialState: {
        offeredCourses: []
    },
    reducers: {
        populateOfferedCourses: (state, action) => {
            const offeredCourses = action.payload.offeredCourses
            localStorage.setItem('offeredCourseList', JSON.stringify(offeredCourses))
            return {
                ...state,
                offeredCourses: offeredCourses
            }
        }
    }
})

export const {
    populateOfferedCourses
} = courseSlice.actions

export default courseSlice.reducer