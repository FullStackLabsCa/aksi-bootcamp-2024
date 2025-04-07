import {createSlice} from "@reduxjs/toolkit";

const memberSlice = createSlice({
    name: 'member',
    initialState: {
        isLoggedIn: false
    },
    reducers: {
        updateLoginStatus: (state, action) => {
            const isMemberLoggedIn = action.payload.loginStatus
            return {
                ...state,
                isLoggedIn: isMemberLoggedIn
            }
        }
    }
})

export const {
    updateLoginStatus
} = memberSlice.actions

export default memberSlice.reducer