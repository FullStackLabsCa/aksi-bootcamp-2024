import './App.css'

import Welcome from "./pages/Welcome.jsx";
import BrowseCourses from "./pages/BrowseCourses.jsx";
import Login from "./components/Login.jsx";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import NavigationBar from "./components/NavigationBar.jsx";

function App() {
    return (
        <BrowserRouter>
            <NavigationBar />
            <Routes>
                <Route path="/" element={<Welcome/>}/>
                <Route path="/browseCourses" element={<BrowseCourses/>}/>
                <Route path="/login" element={<Login/>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App
