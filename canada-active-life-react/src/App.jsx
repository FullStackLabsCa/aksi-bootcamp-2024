import './App.css'

import Welcome from "./pages/Welcome.jsx";
import BrowseCourses from "./pages/BrowseCourses.jsx";
import Login from "./pages/Login.jsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import NavigationBar from "./components/NavigationBar.jsx";
import OfferedCourse from "./pages/OfferedCourse.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import MemberSignUp from "./pages/MemberSignUp.jsx";
import MemberAddToFamily from "./pages/MemberAddToFamily.jsx";
import Cart from "./components/Cart.jsx";

function App() {
    return (
        <BrowserRouter>
            <NavigationBar />
            <Routes>
                <Route path="/" element={<Welcome/>}/>
                <Route path="/browseCourses" element={<BrowseCourses/>}/>
                <Route path="/offeredCourse/:courseId" element={<OfferedCourse />} />
                <Route path="/login" element={<Login/>}/>
                <Route path="/dashboard" element={<Dashboard/>}/>
                <Route path="/signup" element={<MemberSignUp />}/>
                <Route path="/addFamilyMemberToGroup" element={<MemberAddToFamily />}/>
                <Route path="/cart" element={<Cart />}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App
