import Card from 'react-bootstrap/Card';
import ListGroup from 'react-bootstrap/ListGroup';
import {Button} from "react-bootstrap";
import {useSelector} from "react-redux";
import {Link} from "react-router-dom"

function Course({course}) {
    return (
        <div className="m-3">
            <Card style={{width: '16rem'}}>
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
                        className={`d-block mb-1 ${!useSelector(state => state.member.isLoggedIn) ? 'visually-hidden' : ''}`}>
                        Add To Cart
                    </Button>
                    <Button as={Link} to={`/offeredCourse/${course.course.courseId}`}
                            className="d-block">
                        View Details
                    </Button>
                </Card.Body>
            </Card>
        </div>
    );
}

export default Course;