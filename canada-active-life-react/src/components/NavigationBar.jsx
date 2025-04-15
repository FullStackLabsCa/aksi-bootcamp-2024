import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import {Link} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {updateLoginStatus, updateMemberLoginId} from "../store/slices/memberSlice.jsx";
import Cookies from "js-cookie";
import CartDrawer from "./CartDrawer.jsx";
import {useState} from "react";

function NavigationBar() {

    const dispatch = useDispatch()
    const isMemberLoggedIn = useSelector(state => state.member.isLoggedIn)
    const isAdmin = useSelector(state => state.member.isAdmin)
    const [showCart, setShowCart] = useState(false);

    const cartItems = [
        {
            id: 1,
            name: "Yoga for Beginners",
            price: 25.0,
        },
        {
            id: 2,
            name: "Swimming Lessons - Intermediate",
            price: 40.0,
        },
        {
            id: 3,
            name: "Zumba Dance Session",
            price: 15.0,
        },
        {
            id: 11,
            name: "Yoga 11 for Beginners",
            price: 25.0,
        },
        {
            id: 21,
            name: "Swimming 21 Lessons - Intermediate",
            price: 40.0,
        },
    ];

    const handleLogOut = () => {
        dispatch(updateLoginStatus({loginStatus: false}))
        dispatch(updateMemberLoginId({memberLoginId: ''}))
        Cookies.remove('jwt')
    }

    const handleCloseCart = () => setShowCart(false);
    const handleShowCart = () => setShowCart(true);

    return (
        <>
            <Navbar collapseOnSelect expand="lg" className="bg-body-tertiary">
                <Container>
                    <Navbar.Brand href="#home">Canada Active Life</Navbar.Brand>
                    <Navbar.Toggle aria-controls="responsive-navbar-nav"/>
                    <Navbar.Collapse id="responsive-navbar-nav">
                        <Nav className="me-auto">

                        </Nav>
                        <Nav>
                            <Nav.Link as={Link} to="/">Home</Nav.Link>
                            <Nav.Link as={Link} to="/browseCourses">Courses</Nav.Link>
                            <NavDropdown title="Account" id="collapsible-nav-dropdown">
                                <NavDropdown.Item
                                    className={isMemberLoggedIn ? 'visually-hidden' : 'showing'}
                                    as={Link}
                                    to="/login"
                                >
                                    Login
                                </NavDropdown.Item>
                                <NavDropdown.Item
                                    className={isMemberLoggedIn ? 'showing' : 'visually-hidden'}
                                    as={Link}
                                    to="/dashboard"
                                >
                                    Dashboard
                                </NavDropdown.Item>

                                <NavDropdown.Divider
                                    className={(isMemberLoggedIn && !isAdmin) ? 'visually-hidden' : 'showing'}
                                />

                                <NavDropdown.Item
                                    className={isMemberLoggedIn ? 'visually-hidden' : 'showing'}
                                    as={Link}
                                    to="/signup"
                                >
                                    Sign Up
                                </NavDropdown.Item>
                                <NavDropdown.Item
                                    className={(isMemberLoggedIn && isAdmin === true) ? 'showing' : 'visually-hidden'}
                                    as={Link}
                                    to="/addFamilyMemberToGroup"
                                >
                                    Add Family Member
                                </NavDropdown.Item>
                            </NavDropdown>
                            <Nav.Link className='visually-hidden' as={Link} to="/aboutUs">About Us</Nav.Link>
                            <Nav.Link
                                className={isMemberLoggedIn ? 'showing' : 'visually-hidden'}
                                as={Link}
                                to="/"
                                onClick={handleLogOut}
                            >
                                Logout
                            </Nav.Link>
                            <Nav.Link
                                className={isMemberLoggedIn ? 'showing' : 'visually-hidden'}
                                onClick={handleShowCart}
                            >
                                <i className="bi bi-cart3" style={{ fontSize: '1.0rem' }}></i>
                            </Nav.Link>
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>

            <CartDrawer
                show={showCart}
                onHide={handleCloseCart}
                cartItems={cartItems}
            />
        </>
    );
}

export default NavigationBar;