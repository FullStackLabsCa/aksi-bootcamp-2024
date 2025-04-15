import {Col, Row} from "react-bootstrap";
import {FamilyMember} from "../components/FamilyMember.jsx";
import {useSelector} from "react-redux";
import {useEffect, useState} from "react";
import Cookies from "js-cookie";
import {useNavigate} from "react-router-dom";

function Dashboard() {

    const memberLoginId = useSelector(state => state.member.memberLoginId)
    const navigate = useNavigate();
    const [familyMemberInfo, setFamilyMemberInfo] = useState(null);

    useEffect(() => {
        if (!memberLoginId) {
            navigate('/login');
        }
    }, [memberLoginId, navigate]);

    useEffect(() => {
        const fetchFamilyMemberInfo = async () => {
            console.log("Fetching info for:", memberLoginId);
            try {
                const response = await fetch(`http://localhost:30002/CanadaActiveLife/v1/members?memberLoginId=${memberLoginId}`, {
                    method: "GET",
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${Cookies.get('jwt')}`
                    }
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                setFamilyMemberInfo(data);
            } catch (error) {
                console.error("Error fetching family member info:", error);
            }
        };

        if (memberLoginId) {
            fetchFamilyMemberInfo();
        }
    }, [memberLoginId]);

    // TODO use effect to fetch all familyMembers for this particular group.

    const familyMembers = [
        {name: "Alice", homePhoneNumber: "123-456-7890", emailAddress: "alice@example.com", active: true},
        {name: "Bob", homePhoneNumber: "987-654-3210", emailAddress: "bob@example.com", active: false},
        {name: "Bob", homePhoneNumber: "987-654-3210", emailAddress: "bob@example.com", active: false},
        {name: "Bob", homePhoneNumber: "987-654-3210", emailAddress: "bob@example.com", active: false},
        {name: "Charlie", homePhoneNumber: "555-555-5555", emailAddress: "charlie@example.com", active: true},
    ];

    return (
        <div>
            <Row className="mt-4 mb-3 d-flex flex-column align-content-center text-center">
                {familyMemberInfo ? <FamilyMember member={familyMemberInfo}/> :
                    <p>Family Member Info Could Not be fetched...</p>}
            </Row>
            <div className="bg-dark-subtle py-2 mb-3">
                <h3 className="text-center m-0">Family Members</h3>
            </div>
            <Row className="mt-4 mb-3 justify-content-center text-center gx-4">
                {familyMembers.map((member, index) => (
                    <Col key={index} xs={12} sm={6} md={4} lg={3}
                         className="m-lg-1 mb-3 d-flex align-items-stretch">
                        <FamilyMember member={member}/>
                    </Col>
                ))}
            </Row>
        </div>
    )
}

export default Dashboard