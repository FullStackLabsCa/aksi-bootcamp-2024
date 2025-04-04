import {createSlice} from "@reduxjs/toolkit";

const courseSlice = createSlice({
    name: 'courses',
    initialState: {
        offeredCourses: []
    },
    reducers: {
        populateOfferedCourses: (state, action) => {
            const offeredCourses = action.payload.offeredCourses
            sessionStorage.setItem('offeredCourseList', JSON.stringify(offeredCourses)) // TODO Remove this and put the whole store state in storage using Middleware
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