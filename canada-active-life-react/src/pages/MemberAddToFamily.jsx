import {Button, Col, FloatingLabel, Form, Modal, Row} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {useState} from "react";
import Cookies from "js-cookie";
import {useSelector} from "react-redux";
import ErrorToast from "../components/ErrorToast.jsx";

function MemberAddToFamily() {
    const [name, setName] = useState("")
    const [dob, setDob] = useState(null)
    const [gender, setGender] = useState("")
    const [emailAddress, setEmailAddress] = useState("")
    const [streetNumber, setStreetNumber] = useState("")
    const [streetName, setStreetName] = useState("")
    const [city, setCity] = useState("")
    const [province, setProvince] = useState("")
    const [country, setCountry] = useState("")
    const [homePhoneNumber, setHomePhoneNumber] = useState("")
    const [businessPhoneNumber, setBusinessPhoneNumber] = useState("")
    const [preferredContactMethod, setPreferredContactMethod] = useState("SMS")
    const [language, setLanguage] = useState("")
    const [memberLoginId, setMemberLoginId] = useState("")
    const [familyMemberCreationFailed, setFamilyMemberCreationFailed] = useState(false)

    const navigator = useNavigate();
    const [formValidated, setFormValidated] = useState(false);
    const actorMemberLoginId = useSelector(state => state.member.memberLoginId)


    const handleAddFamilyMember = async (e) => {
        e.preventDefault();
        const form = e.currentTarget;

        if (form.checkValidity() === false) {
            e.stopPropagation();
        } else {
            await fetch("http://localhost:30002/CanadaActiveLife/v1/members", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'x-security-header': actorMemberLoginId,
                    Authorization: `Bearer ${Cookies.get('jwt')}`
                },
                body: JSON.stringify({
                    "name": name,
                    "dob": dob,
                    "gender": gender,
                    "emailAddress": emailAddress,
                    "streetNumber": streetNumber,
                    "streetName": streetName,
                    "city": city,
                    "province": province,
                    "country": country,
                    "homePhoneNumber": homePhoneNumber,
                    "businessPhoneNumber": businessPhoneNumber,
                    "preferredContactMethod": emailAddress,
                    "language": language,
                    "memberLoginId": memberLoginId
                })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.text()
                })
                .then(data => {
                    alert(data)
                    navigator('/browseCourses', {replace: true})
                })
                .catch(e => {
                    console.error("FETCH FAILED:", e);
                    setFamilyMemberCreationFailed(true)
                })
        }

        setFormValidated(true);
    }

    return (
        <Modal show={true} centered>
            <Modal.Header>
                <Modal.Title>Add Family Member to Group</Modal.Title>
            </Modal.Header>
            <Form
                className="d-flex flex-column gap-3"
                validated={formValidated}
                onSubmit={handleAddFamilyMember}
            >
                <Modal.Body>
                    <Row>
                        <Col md={6}>
                            <FloatingLabel className="mb-1" controlId="fullName" label="Full Name">
                                <Form.Control
                                    type="text"
                                    placeholder="Full Name"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please enter your name.
                                </Form.Control.Feedback>
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="dateOfBirth" label="Date Of Birth">
                                <Form.Control
                                    type="date"
                                    placeholder="Date of Birth"
                                    value={dob || ''}
                                    onChange={(e) => setDob(e.target.value)}
                                    required
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please enter your DOB.
                                </Form.Control.Feedback>
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="gender" label="Gender">
                                <Form.Select
                                    value={gender}
                                    onChange={(e) => setGender(e.target.value)}
                                    required
                                >
                                    <option value="">Select Gender</option>
                                    <option value="Male">Male</option>
                                    <option value="Female">Female</option>
                                    <option value="Other">Other</option>
                                </Form.Select>
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="email" label="Email Address">
                                <Form.Control
                                    type="email"
                                    placeholder="Email Address"
                                    value={emailAddress}
                                    onChange={(e) => setEmailAddress(e.target.value)}
                                    required
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please enter valid Email Address.
                                </Form.Control.Feedback>
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="homePhNum" label="Home Phone Number">
                                <Form.Control
                                    type="tel"
                                    placeholder="1234567890"
                                    value={homePhoneNumber}
                                    onChange={(e) => setHomePhoneNumber(e.target.value)}
                                />
                                <Form.Control.Feedback type="invalid">
                                    Please enter valid Phone Number.
                                </Form.Control.Feedback>
                            </FloatingLabel>
                        </Col>
                        <Col md={6}>
                            <FloatingLabel className="mb-1" controlId="stNum" label="Street Number">
                                <Form.Control
                                    type="text"
                                    placeholder="Street Number"
                                    value={streetNumber}
                                    onChange={(e) => setStreetNumber(e.target.value)}
                                />
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="stName" label="Street Name">
                                <Form.Control
                                    type="text"
                                    placeholder="Street Name"
                                    value={streetName}
                                    onChange={(e) => setStreetName(e.target.value)}
                                />
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="city" label="City">
                                <Form.Control
                                    type="text"
                                    placeholder="City"
                                    value={city}
                                    onChange={(e) => setCity(e.target.value)}
                                    required
                                />
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="province" label="Province">
                                <Form.Control
                                    type="text"
                                    placeholder="Province"
                                    value={province}
                                    onChange={(e) => setProvince(e.target.value)}
                                    required
                                />
                            </FloatingLabel>
                            <FloatingLabel className="mb-1" controlId="country" label="Country">
                                <Form.Control
                                    type="text"
                                    placeholder="Country"
                                    value={country}
                                    onChange={(e) => setCountry(e.target.value)}
                                    required
                                />
                            </FloatingLabel>
                        </Col>
                    </Row>
                    <FloatingLabel className="mb-1" controlId="busPhNum" label="Business Phone Number">
                        <Form.Control
                            type="tel"
                            placeholder="1234567890"
                            value={businessPhoneNumber}
                            onChange={(e) => setBusinessPhoneNumber(e.target.value)}
                        />
                        <Form.Control.Feedback type="invalid">
                            Please enter valid Phone Number.
                        </Form.Control.Feedback>
                    </FloatingLabel>
                    <FloatingLabel className="mb-1" controlId="contactMethod" label="Preferred Contact Method">
                        <Form.Select
                            value={preferredContactMethod}
                            onChange={(e) => setPreferredContactMethod(e.target.value)}
                        >
                            <option value="SMS">SMS</option>
                            <option value="Email">Email</option>
                            <option value="Phone">Phone</option>
                        </Form.Select>
                    </FloatingLabel>
                    <FloatingLabel className="mb-1" controlId="lang" label="Mother Tongue">
                        <Form.Control
                            type="text"
                            placeholder="Language"
                            value={language}
                            onChange={(e) => setLanguage(e.target.value)}
                        />
                    </FloatingLabel>
                    <FloatingLabel className="mb-1" controlId="memberLoginId" label="Member Login Id">
                        <Form.Control
                            type="text"
                            placeholder="Member Login ID"
                            value={memberLoginId}
                            pattern="^\S+$"
                            onChange={(e) => setMemberLoginId(e.target.value)}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            Please enter valid Member Login Id.
                        </Form.Control.Feedback>
                    </FloatingLabel>
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="secondary" onClick={() => navigator('/browseCourses')}>Cancel</Button>
                    <Button variant="primary" type='submit'>Add Family Member</Button>
                </Modal.Footer>
                <ErrorToast
                    show={familyMemberCreationFailed}
                    message="Failed to Add Family Member to the Group"
                    onClose={() => setFamilyMemberCreationFailed(false)}
                />
            </Form>
        </Modal>
    )
}

export default MemberAddToFamily