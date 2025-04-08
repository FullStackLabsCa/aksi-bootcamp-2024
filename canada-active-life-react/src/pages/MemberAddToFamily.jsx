import {Button, FloatingLabel, Form, Modal} from "react-bootstrap";
import {useNavigate} from "react-router-dom";
import {useState} from "react";

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

    const navigator = useNavigate();
    const [formValidated, setFormValidated] = useState(false);


    const handleAddFamilyMember = (e) => {
        e.preventDefault();
        const form = e.currentTarget;

        if (form.checkValidity() === false) {
            e.stopPropagation();
        } else {
            console.log({
                name,
                dob,
                gender,
                emailAddress,
                streetNumber,
                streetName,
                city,
                province,
                country,
                homePhoneNumber,
                businessPhoneNumber,
                preferredContactMethod,
                language,
                memberLoginId,
            });
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
                    <FloatingLabel controlId="fullName" label="Full Name">
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
                    <FloatingLabel controlId="dateOfBirth" label="Date Of Birth">
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
                    <FloatingLabel controlId="gender" label="Gender">
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
                    <FloatingLabel controlId="email" label="Email Address">
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
                    <FloatingLabel controlId="stNum" label="Street Number">
                        <Form.Control
                            type="text"
                            placeholder="Street Number"
                            value={streetNumber}
                            onChange={(e) => setStreetNumber(e.target.value)}
                        />
                    </FloatingLabel>
                    <FloatingLabel controlId="stName" label="Street Name">
                        <Form.Control
                            type="text"
                            placeholder="Street Name"
                            value={streetName}
                            onChange={(e) => setStreetName(e.target.value)}
                        />
                    </FloatingLabel>
                    <FloatingLabel controlId="city" label="City">
                        <Form.Control
                            type="text"
                            placeholder="City"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                            required
                        />
                    </FloatingLabel>
                    <FloatingLabel controlId="province" label="Province">
                        <Form.Control
                            type="text"
                            placeholder="Province"
                            value={province}
                            onChange={(e) => setProvince(e.target.value)}
                            required
                        />
                    </FloatingLabel>
                    <FloatingLabel controlId="country" label="Country">
                        <Form.Control
                            type="text"
                            placeholder="Country"
                            value={country}
                            onChange={(e) => setCountry(e.target.value)}
                            required
                        />
                    </FloatingLabel>
                    <FloatingLabel controlId="homePhNum" label="Home Phone Number">
                        <Form.Control
                            type="tel"
                            placeholder="123-456-7890"
                            pattern="\d{3}-\d{3}-\d{4}"
                            value={homePhoneNumber}
                            onChange={(e) => setHomePhoneNumber(e.target.value)}
                        />
                        <Form.Control.Feedback type="invalid">
                            Please enter valid Phone Number.
                        </Form.Control.Feedback>
                    </FloatingLabel>
                    <FloatingLabel controlId="busPhNum" label="Business Phone Number">
                        <Form.Control
                            type="tel"
                            placeholder="123-456-7890"
                            pattern="\d{3}-\d{3}-\d{4}"
                            value={businessPhoneNumber}
                            onChange={(e) => setBusinessPhoneNumber(e.target.value)}
                        />
                        <Form.Control.Feedback type="invalid">
                            Please enter valid Phone Number.
                        </Form.Control.Feedback>
                    </FloatingLabel>
                    <FloatingLabel controlId="contactMethod" label="Preferred Contact Method">
                        <Form.Select
                            value={preferredContactMethod}
                            onChange={(e) => setPreferredContactMethod(e.target.value)}
                        >
                            <option value="SMS">SMS</option>
                            <option value="Email">Email</option>
                            <option value="Phone">Phone</option>
                        </Form.Select>
                    </FloatingLabel>
                    <FloatingLabel controlId="lang" label="Mother Tongue">
                        <Form.Control
                            type="text"
                            placeholder="Language"
                            value={language}
                            onChange={(e) => setLanguage(e.target.value)}
                        />
                    </FloatingLabel>
                    <FloatingLabel controlId="memberLoginId" label="Member Login Id">
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
            </Form>

        </Modal>
    )
}

export default MemberAddToFamily