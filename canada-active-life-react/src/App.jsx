import './App.css'

import Welcome from "./components/Welcome.jsx";
import Courses from "./components/Courses.jsx";
import Login from "./components/Login.jsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import NavigationBar from "./components/NavigationBar.jsx";

function App() {
    return (
        <BrowserRouter>
            <NavigationBar />
            <Routes>
                <Route path="/" element={<Welcome/>}/>
                <Route path="/browseCourses" element={<Courses/>}/>
                <Route path="/login" element={<Login/>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App
