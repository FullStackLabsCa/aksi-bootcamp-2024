import {configureStore} from "@reduxjs/toolkit";
import courseReducer from "./slices/courseSlice.jsx";

export const store = configureStore({
    reducer: {
        courses: courseReducer
    }
})