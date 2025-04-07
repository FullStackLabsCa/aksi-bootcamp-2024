import {configureStore} from "@reduxjs/toolkit";
import courseReducer from "./slices/courseSlice.jsx";
import memberReducer from "./slices/memberSlice.jsx"

export const store = configureStore({
    reducer: {
        courses: courseReducer,
        member: memberReducer
    }
})