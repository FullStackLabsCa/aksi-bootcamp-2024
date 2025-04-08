import Toast from 'react-bootstrap/Toast';
import {useEffect} from "react";
import {ToastContainer} from "react-bootstrap";

function ErrorToast({show, message, onClose}) {

    useEffect(() => {
        if (show) {
            const timer = setTimeout(() => {
                onClose();
            }, 3000); // 2 seconds

            return () => clearTimeout(timer);
        }
    }, [show, onClose]);

    return (
        <ToastContainer position='top-center' className='p-3'>
            <Toast show={show} onClose={onClose} bg='danger'>
                <Toast.Header closeButton>
                    <strong className="me-auto">Login Failed</strong>
                </Toast.Header>
                <Toast.Body className='text-white'>{message}</Toast.Body>
            </Toast>
        </ToastContainer>
    );
}

export default ErrorToast;