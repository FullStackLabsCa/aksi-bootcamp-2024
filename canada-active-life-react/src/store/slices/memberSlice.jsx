import {createSlice} from "@reduxjs/toolkit";

const memberSlice = createSlice({
    name: 'member',
    initialState: {
        isLoggedIn: false,
        memberLoginId: ''
    },
    reducers: {
        updateLoginStatus: (state, action) => {
            return {
                ...state,
                isLoggedIn: action.payload.loginStatus
            }
        },
        updateMemberLoginId: (state, action) => {
            return {
                ...state,
                memberLoginId: action.payload.memberLoginId
            }
        }
    }
})

export const {
    updateLoginStatus,
    updateMemberLoginId
} = memberSlice.actions

export default memberSlice.reducer