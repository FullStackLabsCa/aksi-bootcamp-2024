import Card from "react-bootstrap/Card";
import ListGroup from "react-bootstrap/ListGroup";
import {Button} from "react-bootstrap";
import Cookies from "js-cookie";
import {useSelector} from "react-redux";
import {useState} from "react";

export function FamilyMember({member}) {

    const actorId = useSelector(state => state.member.memberLoginId)
    const [isActive, setIsActive] = useState(member.active);

    const handleDeactivateMember = async () => {
        try {
            const response = await fetch(`http://localhost:30002/CanadaActiveLife/v1/members?memberLoginId=${member.memberLoginId}`, {
                method: "DELETE",
                headers: {
                    'Content-Type': 'application/json',
                    'x-security-header': actorId,
                    Authorization: `Bearer ${Cookies.get('jwt')}`
                }
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            setIsActive(false)
        } catch (error) {
            console.error("Error Deactivating family member:", error);
        }
    }

    const handleActivateMember = async () => {
        try {
            const response = await fetch(`http://localhost:30002/CanadaActiveLife/v1/members/activate?memberLoginId=${member.memberLoginId}`, {
                method: "GET",
                headers: {
                    'Content-Type': 'application/json',
                    'x-security-header': actorId,
                    Authorization: `Bearer ${Cookies.get('jwt')}`
                }
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            setIsActive(true)
        } catch (error) {
            console.error("Error Deactivating family member:", error);
        }
    }

    return <Card style={{width: "18rem", alignContent: "center"}}>
        <Card.Body>
            <Card.Title>{member.name}</Card.Title>
            <ListGroup className="list-group-flush mb-3 w-100">
                <ListGroup.Item>Ph. No.: {member.homePhoneNumber}</ListGroup.Item>
                <ListGroup.Item>Email Address: {member.emailAddress}</ListGroup.Item>
                <ListGroup.Item className="align-items-center">
                    Active Status:
                    <span
                        className={`ms-2 rounded-circle`}
                        style={{
                            width: '12px',
                            height: '12px',
                            backgroundColor: isActive ? 'green' : 'red',
                            display: 'inline-block'
                        }}
                    />
                </ListGroup.Item>
            </ListGroup>
            <Button
                className={isActive ? "showing mb-2" : "visually-hidden"}
                variant="outline-primary"
            >
                View Enrollments
            </Button>
            <Button
                className={!isActive ? "showing mb-1" : "visually-hidden"}
                variant="outline-success"
                onClick={handleActivateMember}
            >
                Activate
            </Button>
            <Button
                className={isActive ? "showing mb-1" : "visually-hidden"}
                variant="outline-danger"
                onClick={handleDeactivateMember}
            >
                DeActivate
            </Button>
        </Card.Body>
    </Card>;
}