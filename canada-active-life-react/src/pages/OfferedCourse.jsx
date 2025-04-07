import {useParams} from "react-router-dom";
import {useSelector} from "react-redux";
import Card from "react-bootstrap/Card";
import ListGroup from "react-bootstrap/ListGroup";
import {Button} from "react-bootstrap";

export default function OfferedCourse() {
    const {courseId} = useParams()
    const offeredCourses = useSelector(state => state.courses.offeredCourses)
    const offeredCourse = offeredCourses.filter(course => course.offeredCourseId == courseId)
    return (
        <div className="d-flex flex-column align-items-center">
            <h3 className="mb-4">{offeredCourse[0]?.course.name}</h3>
            <h6 className='mb-4'>{offeredCourse[0]?.course.description}</h6>
            <h6>Dates:</h6>
            <Card className="d-flex flex-column align-items-center mb-4" style={{width: "auto", minWidth: "20rem"}}>
                <ListGroup variant="flush">
                    <ListGroup.Item>Start Date: {offeredCourse[0]?.startDate.toString()}</ListGroup.Item>
                    <ListGroup.Item>End Date: {offeredCourse[0]?.endDate.toString()}</ListGroup.Item>
                </ListGroup>
            </Card>
            <h6>Timings:</h6>
            <Card className="d-flex flex-column align-items-center mb-4" style={{width: "auto", minWidth: "20rem"}}>
                <ListGroup variant="flush">
                    <ListGroup.Item>
                        Start Time: {new Date(offeredCourse[0]?.startTime).toLocaleTimeString([], {
                        hour: '2-digit',
                        minute: '2-digit'
                    })}
                    </ListGroup.Item>
                    <ListGroup.Item>
                        End Time: {new Date(offeredCourse[0]?.endTime).toLocaleTimeString([], {
                        hour: '2-digit',
                        minute: '2-digit'
                    })}
                    </ListGroup.Item>
                </ListGroup>
            </Card>
            <h6>Location:</h6>
            <Card className="d-flex flex-column align-items-center mb-4" style={{width: "auto", minWidth: "20rem"}}>
                <ListGroup variant="flush" className="align-content-center">
                    <ListGroup.Item>{offeredCourse[0]?.facility.name}</ListGroup.Item>
                    <ListGroup.Item>Address: {offeredCourse[0]?.facility.streetNumber} {offeredCourse[0]?.facility.streetName}</ListGroup.Item>
                    <ListGroup.Item>City: {offeredCourse[0]?.facility.city}, {offeredCourse[0]?.facility.province}, {offeredCourse[0]?.facility.postalCode}</ListGroup.Item>
                    <ListGroup.Item>{offeredCourse[0]?.facility.description}</ListGroup.Item>
                </ListGroup>
            </Card>
            <Card className="d-flex flex-column align-items-center mb-4">
                <Button
                    disabled={offeredCourse[0]?.availableForEnrollment !== 'OPEN'}
                    variant={(offeredCourse[0]?.availableForEnrollment !== 'OPEN') ? "secondary" : "primary"}
                    className={`d-block ${!useSelector(state => state.member.isLoggedIn) ? 'visually-hidden' : 'show'}`}>
                    Add To Cart
                </Button>
            </Card>
        </div>
    )
}