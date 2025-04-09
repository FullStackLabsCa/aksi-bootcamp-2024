import Card from "react-bootstrap/Card";
import ListGroup from "react-bootstrap/ListGroup";
import {Button} from "react-bootstrap";

export function FamilyMember() {
    return <Card style={{width: "18rem", alignContent: "center"}}>
        <Card.Body>
            <Card.Title>Family Member Name</Card.Title>
            <ListGroup className="list-group-flush mb-3 w-100">
                <ListGroup.Item>Phone Number: </ListGroup.Item>
                <ListGroup.Item>Email Address: </ListGroup.Item>
                <ListGroup.Item>Is Active: </ListGroup.Item>
            </ListGroup>
            <Button className="" variant="primary">View Registrations</Button>
        </Card.Body>
    </Card>;
}