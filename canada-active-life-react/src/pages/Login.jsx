import {Button, FloatingLabel} from "react-bootstrap";
import Form from 'react-bootstrap/Form';
import {Link} from "react-router-dom";
import {useState} from "react";
import Cookies from 'js-cookie';
import LoginOTP from "../components/LoginOTP.jsx";
import LoginError from "../components/LoginError.jsx";
import {useDispatch} from "react-redux";
import {updateLoginStatus} from "../store/slices/memberSlice.jsx";

const handleLoginAuthentication = async ({event, memberLoginId, password, setShowOtpPopUp, setLoginFailed}) => {
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

    return await fetch("http://localhost:30002/CanadaActiveLife/v1/login", requestOptions)
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then((data) => {
            // console.log(data)
            Cookies.set('jwt', data.token)
            setShowOtpPopUp(true)
            return data
        }).catch(e => {
            console.error("FETCH FAILED:", e);
            setLoginFailed(true)
            return undefined;
        });
}

const handleLoginOTPAuthentication = async ({setOtpVerificationFailed, otp, storeDispatch}) => {
    console.log('Sending Request for Handle Verify')
    return await fetch("http://localhost:30002/CanadaActiveLife/v1/login/2fa", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${Cookies.get('jwt')}`
        },
        body: JSON.stringify({
            "otpEnteredByUser": otp
        })
    }).then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json()
    }).then(data => {
        alert(data.message)
        Cookies.set('jwt', data.data.jwtToken)
        storeDispatch(updateLoginStatus({loginStatus: true}))
    }).catch(e => {
        console.error("FETCH FAILED:", e);
        setOtpVerificationFailed(true)
    })
}

export default function Login() {
    const [memberLoginId, setMemberLoginId] = useState('');
    const [password, setPassword] = useState('');
    const [showOtpPopUp, setShowOtpPopUp] = useState(false);
    const [loginFailed, setLoginFailed] = useState(false);
    const [otpVerificationFailed, setOtpVerificationFailed] = useState(false);
    const storeDispatch = useDispatch()

    return (
        <div className="d-flex flex-column align-items-center justify-content-center" style={{minHeight: '70vh'}}>
            <h1 className="mb-4">Login</h1>
            <Form
                onSubmit={(event) =>
                    handleLoginAuthentication({event, memberLoginId, password, setShowOtpPopUp, setLoginFailed})}
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
                    <Form.Control
                        type="text"
                        placeholder="Member Login Id"
                        autoComplete='username'
                    />
                </FloatingLabel>
                <FloatingLabel
                    className="mb-4"
                    controlId="loginPassword"
                    label="Password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    required
                >
                    <Form.Control
                        type="password"
                        placeholder="Password"
                        autoComplete='current-password'
                    />
                </FloatingLabel>
                <Button
                    type="submit"
                    variant="primary"
                    className="mb-2"
                >
                    Login
                </Button>
            </Form>
            <LoginOTP
                show={showOtpPopUp}
                onHide={() => setShowOtpPopUp(false)}
                onVerify={() => handleLoginOTPAuthentication({setOtpVerificationFailed, storeDispatch})}
            />
            <Link to="/signup">Sign-Up</Link>
            <LoginError
                show={loginFailed}
                message="Incorrect MemberLoginId or Password"
                onClose={() => setLoginFailed(false)}
            />
            <LoginError
                show={otpVerificationFailed}
                message="Incorrect OTP"
                onClose={() => setOtpVerificationFailed(false)}
            />
        </div>
    )
}