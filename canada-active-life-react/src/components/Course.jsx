import Card from 'react-bootstrap/Card';
import ListGroup from 'react-bootstrap/ListGroup';
import {Button} from "react-bootstrap";

function Course({course}) {
    return (
        <div className="m-3">
            <Card style={{width: '18rem'}}>
                <Card.Body>
                    <Card.Title>{course.course.name}</Card.Title>
                    <Card.Text>
                        {course.course.description}
                    </Card.Text>
                </Card.Body>
                <ListGroup className="list-group-flush">
                    <ListGroup.Item>{course.facility.name}</ListGroup.Item>
                    <ListGroup.Item>{course.facility.city}, {course.facility.province}</ListGroup.Item>
                    <ListGroup.Item>Enrollment: {course.availableForEnrollment}</ListGroup.Item>
                </ListGroup>
                <Card.Body>
                    <Button
                        disabled={course.availableForEnrollment !== 'OPEN'}
                        variant={(course.availableForEnrollment !== 'OPEN') ? "secondary" : "primary"}
                        className="d-block mb-1">
                        Add To Cart
                    </Button>
                    <Button
                        disabled={course.availableForEnrollment !== 'OPEN'}
                        variant={(course.availableForEnrollment !== 'OPEN') ? "secondary" : "success"}
                        className="d-block">
                        Register Now
                    </Button>
                </Card.Body>
            </Card>
        </div>
    );
}

export default Course;