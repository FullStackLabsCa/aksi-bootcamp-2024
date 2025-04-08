import { Modal, Button, Form } from 'react-bootstrap';
import { useState } from 'react';

function LoginOTP({ show, onHide, onVerify }) {
    const [otp, setOtp] = useState('');

    const handleSubmit = () => {
        onVerify({otp: otp});
    };

    return (
        <Modal show={show} onHide={onHide} centered>
            <Modal.Header closeButton>
                <Modal.Title>Enter 6-digit OTP</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form.Control
                    type="text"
                    placeholder="Enter OTP"
                    value={otp}
                    maxLength={6}
                    onChange={(e) => setOtp(e.target.value)}
                />
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={onHide}>Cancel</Button>
                <Button variant="primary" onClick={handleSubmit}>Submit</Button>
            </Modal.Footer>
        </Modal>
    );
}

export default LoginOTP;
