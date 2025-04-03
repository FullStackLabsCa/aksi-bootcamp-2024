import './App.css'

import Welcome from "./components/Welcome.jsx";
import Courses from "./components/Courses.jsx";
import Login from "./components/Login.jsx";
import {BrowserRouter, Link, Route, Routes} from "react-router-dom";

function App() {
    return (
        <BrowserRouter>
            <nav className="nav-bar">
                <Link to="/">Welcome</Link>
                <span> | </span>
                <Link to="/browseCourses">Courses</Link>
                <span> | </span>
                <Link to="/login">Login</Link>
                <span> | </span>
                <Link to="/signup">SignUp</Link>
                <span className="not-display"> | </span>
                <Link to="/cart" className="not-display">Cart</Link>
                <span className="not-display"> | </span>
                <Link to="/checkout" className="not-display">Checkout</Link>
                <span className="not-display"> | </span>
                <Link to="/dashboard" className="not-display">Dashboard</Link>
            </nav>
            <Routes>
                <Route path="/" element={<Welcome/>}/>
                <Route path="/browseCourses" element={<Courses/>}/>
                <Route path="/login" element={<Login/>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App
