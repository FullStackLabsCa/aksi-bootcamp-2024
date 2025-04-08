import {Button, FloatingLabel} from "react-bootstrap";
import Form from 'react-bootstrap/Form';
import {Link} from "react-router-dom";
import {useState} from "react";

const tryLogin = async ({event, memberLoginId, password}) => {
    event.preventDefault()
    // Make a call to the backend using ID and Password
    const myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const raw = JSON.stringify({
        "memberLoginId": memberLoginId,
        "familyPin": password
    });

    const requestOptions = {
        method: "POST",
        headers: myHeaders,
        body: raw
    };

    const response = await fetch("http://localhost:30002/CanadaActiveLife/v1/login", requestOptions)
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then((data) => {
            console.log(data)
            return data
        }).catch(e => {
        console.error("FETCH FAILED:", e);
    });

    return response;
}

export default function Login() {
    const [memberLoginId, setMemberLoginId] = useState('');
    const [password, setPassword] = useState('');

    return (
        <div className="d-flex flex-column align-items-center justify-content-center" style={{minHeight: '70vh'}}>
            <h1 className="mb-4">Login</h1>
            <Form
                onSubmit={(event) =>
                    tryLogin({event, memberLoginId, password})}
                className="d-flex flex-column align-items-center justify-content-center"
            >
                <FloatingLabel
                    controlId="memberLoginId"
                    label="Member Login Id"
                    className="mb-3"
                    value={memberLoginId}
                    onChange={(event) => setMemberLoginId(event.target.value)}
                    required
                >
                    <Form.Control type="text" placeholder="Member Login Id"/>
                </FloatingLabel>
                <FloatingLabel
                    className="mb-4"
                    controlId="loginPassword"
                    label="Password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    required
                >
                    <Form.Control type="text" placeholder="Password"/>
                </FloatingLabel>
                <Button
                    type="submit"
                    variant="primary"
                    className="mb-2"
                >
                    Login
                </Button>
            </Form>
            <Link to="/signup">Sign-Up</Link>
        </div>
    )
}