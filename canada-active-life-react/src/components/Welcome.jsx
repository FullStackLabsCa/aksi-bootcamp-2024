import {Link} from "react-router-dom";

function Welcome() {
    return (
        <>
            <h1>Welcome to Canada Active Life</h1>
            <p>Where all get active....</p>
            <nav>
                <Link to="/aboutUs">Learn More About Us Here</Link>
            </nav>
            {/*<Routes>*/}
            {/*    <Route path="/aboutUs" element={<AboutUs />} />*/}
            {/*</Routes>*/}
        </>
    )
}

export default Welcome