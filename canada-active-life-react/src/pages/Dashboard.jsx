import {Col, Row} from "react-bootstrap";
import {FamilyMember} from "../components/FamilyMember.jsx";

function Dashboard() {
    const familyMembers = [
        { name: "Alice", phone: "123-456-7890", email: "alice@example.com", isActive: true },
        { name: "Bob", phone: "987-654-3210", email: "bob@example.com", isActive: false },
        { name: "Charlie", phone: "555-555-5555", email: "charlie@example.com", isActive: true },
        // ...more members
    ];

    return (
        <div>
            <Row className="mt-4 mb-3 d-flex flex-column align-content-center text-center">
                <FamilyMember member={familyMembers[0]}/>
            </Row>
            <div className="bg-dark-subtle py-2 mb-3">
                <h3 className="text-center m-0">Family Members</h3>
            </div>
            <Row className="mt-4 mb-3 justify-content-center text-center gx-4">
                {familyMembers.map((member, index) => (
                    <Col key={index} xs={12} sm={6} md={4} lg={3} className="d-flex align-items-stretch">
                        <FamilyMember member={member} />
                    </Col>
                ))}
            </Row>
        </div>
    )
}

export default Dashboard