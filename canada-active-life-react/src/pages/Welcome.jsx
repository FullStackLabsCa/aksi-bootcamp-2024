import {Link} from "react-router-dom";
import {Button} from "react-bootstrap";

function Welcome() {
    return (
        <div className="d-flex flex-column align-items-center justify-content-center" style={{minHeight: '100vh'}}>
            <h1>Welcome to Canada Active Life</h1>
            <p>Where all get active....</p>
            <Button as={Link} to="/browseCourses" className="mt-3">Browse Courses</Button>
        </div>
    )
}

export default Welcome