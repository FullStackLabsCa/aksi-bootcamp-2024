import Card from "react-bootstrap/Card";
import ListGroup from "react-bootstrap/ListGroup";
import {Button} from "react-bootstrap";

export function FamilyMember({member}) {
    return <Card style={{width: "16rem", alignContent: "center"}}>
        <Card.Body>
            <Card.Title>{member.name}</Card.Title>
            <ListGroup className="list-group-flush mb-3 w-100">
                <ListGroup.Item>Ph. No.: {member.phone}</ListGroup.Item>
                <ListGroup.Item>Email Address: {member.email}</ListGroup.Item>
                <ListGroup.Item className="align-items-center">
                    Active Status:
                    <span
                        className={`ms-2 rounded-circle`}
                        style={{
                            width: '12px',
                            height: '12px',
                            backgroundColor: member.isActive ? 'green' : 'red',
                            display: 'inline-block'
                        }}
                    />
                </ListGroup.Item>
            </ListGroup>
            <Button className={member.isActive ? "showing mb-2" : "visually-hidden"}  variant="outline-primary">View Enrollments</Button>
            <Button className={!member.isActive ? "showing mb-1" : "visually-hidden"} variant="outline-success">Activate</Button>
            <Button className={member.isActive ? "showing mb-1" : "visually-hidden"} variant="outline-danger">DeActivate</Button>
        </Card.Body>
    </Card>;
}