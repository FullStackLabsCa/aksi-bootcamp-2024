import {Col, Row} from "react-bootstrap";
import {FamilyMember} from "../components/FamilyMember.jsx";

function Dashboard() {
    return (
        <div>
            <Row className="mt-4 mb-3 d-flex flex-column align-content-center text-center">
                <FamilyMember/>
            </Row>
            <div className="bg-dark-subtle py-2 mb-3">
                <h3 className="text-center m-0">Family Members</h3>
            </div>
            <Row className="mt-4 mb-3 justify-content-center text-center gx-4">
                <Col xs={12} sm={6} md={4} lg={3}>
                    <FamilyMember />
                </Col>
                <Col xs={12} sm={6} md={4} lg={3}>
                    <FamilyMember />
                </Col>
                <Col xs={12} sm={6} md={4} lg={3}>
                    <FamilyMember />
                </Col>
            </Row>
        </div>
    )
}

export default Dashboard