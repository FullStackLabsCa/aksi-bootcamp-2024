import {Button, FloatingLabel} from "react-bootstrap";
import Form from 'react-bootstrap/Form';
import {Link} from "react-router-dom";


export default function Login() {
    return (
        <div className="d-flex flex-column align-items-center justify-content-center" style={{minHeight: '70vh'}}>
            <h1 className="mb-4">Login</h1>
            <FloatingLabel
                controlId="memberLoginId"
                label="Member Login Id"
                className="mb-3"
            >
                <Form.Control type="text" placeholder="" />
            </FloatingLabel>
            <FloatingLabel className="mb-4" controlId="loginPassword" label="Password">
                <Form.Control type="password" placeholder="Password" />
            </FloatingLabel>
            <Button className="mb-2">Login</Button>
            <Link to="/signup">Sign-Up</Link>
        </div>
    )
}