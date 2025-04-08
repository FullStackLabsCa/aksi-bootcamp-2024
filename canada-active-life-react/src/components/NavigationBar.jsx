import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import {Link} from "react-router-dom";
import {useSelector} from "react-redux";

function NavigationBar() {
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
                                className={useSelector(state => state.member.isLoggedIn) ? 'visually-hidden' : 'show'}
                                as={Link}
                                to="/login"
                            >
                                Login
                            </NavDropdown.Item>
                            <NavDropdown.Item
                                className={useSelector(state => state.member.isLoggedIn) ? 'show' : 'visually-hidden'}
                                as={Link}
                                to="/dashboard"
                            >
                                Dashboard
                            </NavDropdown.Item>

                            <NavDropdown.Divider
                                className={useSelector(state => state.member.isLoggedIn && !state.member.isAdmin) ? 'visually-hidden' : 'show'}
                            />

                            <NavDropdown.Item
                                className={useSelector(state => state.member.isLoggedIn) ? 'visually-hidden' : 'show'}
                                as={Link}
                                to="/signup"
                            >
                                Sign Up
                            </NavDropdown.Item>
                            <NavDropdown.Item
                                className={useSelector(state => state.member.isLoggedIn && state.member.isAdmin) ? 'show' : 'visually-hidden'}
                                as={Link}
                                to="/addNewFamilyMember"
                            >
                                Add Family Member
                            </NavDropdown.Item>
                        </NavDropdown>
                        <Nav.Link as={Link} to="/aboutUs">About Us</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default NavigationBar;