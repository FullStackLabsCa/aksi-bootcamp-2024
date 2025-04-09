import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import {Link} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {updateLoginStatus, updateMemberLoginId} from "../store/slices/memberSlice.jsx";

function NavigationBar() {

    const dispatch = useDispatch()

    const handleLogOut = () => {
        dispatch(updateLoginStatus({loginStatus: false}))
        dispatch(updateMemberLoginId({memberLoginId: ''}))
    }

    return (
        <Navbar collapseOnSelect expand="lg" className="bg-body-tertiary">
            <Container>
                <Navbar.Brand href="#home">Canada Active Life</Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto">

                    </Nav>
                    <Nav>
                        <Nav.Link as={Link} to="/">Home</Nav.Link>
                        <Nav.Link as={Link} to="/browseCourses">Courses</Nav.Link>
                        <NavDropdown title="Account" id="collapsible-nav-dropdown">
                            <NavDropdown.Item
                                className={useSelector(state => state.member.isLoggedIn) ? 'visually-hidden' : 'showing'}
                                as={Link}
                                to="/login"
                            >
                                Login
                            </NavDropdown.Item>
                            <NavDropdown.Item
                                className={useSelector(state => state.member.isLoggedIn) ? 'showing' : 'visually-hidden'}
                                as={Link}
                                to="/dashboard"
                            >
                                Dashboard
                            </NavDropdown.Item>

                            <NavDropdown.Divider
                                className={useSelector(state => state.member.isLoggedIn && !state.member.isAdmin) ? 'visually-hidden' : 'showing'}
                            />

                            <NavDropdown.Item
                                className={useSelector(state => state.member.isLoggedIn) ? 'visually-hidden' : 'showing'}
                                as={Link}
                                to="/signup"
                            >
                                Sign Up
                            </NavDropdown.Item>
                            <NavDropdown.Item
                                className={useSelector(state => state.member.isLoggedIn && state.member.isAdmin) ? 'showing' : 'visually-hidden'}
                                as={Link}
                                to="/addFamilyMemberToGroup"
                            >
                                Add Family Member
                            </NavDropdown.Item>
                        </NavDropdown>
                        <Nav.Link as={Link} to="/aboutUs">About Us</Nav.Link>
                        <Nav.Link
                            className={useSelector(state => state.member.isLoggedIn) ? 'showing' : 'visually-hidden'}
                            as={Link}
                            to="/"
                            onClick={handleLogOut}
                        >
                            Logout
                        </Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavigationBar;