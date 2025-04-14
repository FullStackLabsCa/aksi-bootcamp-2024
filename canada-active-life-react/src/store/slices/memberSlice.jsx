import {createSlice} from "@reduxjs/toolkit";

const memberSlice = createSlice({
    name: 'member',
    initialState: {
        isLoggedIn: sessionStorage.getItem('isLoggedIn'),
        memberLoginId: '',
        isAdmin: true
    },
    reducers: {
        updateLoginStatus: (state, action) => {
            sessionStorage.setItem('isLoggedIn', action.payload.loginStatus)
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