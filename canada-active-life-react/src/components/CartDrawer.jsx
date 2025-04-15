import { Offcanvas, Card, Button, Stack } from 'react-bootstrap';

export default function CartDrawer({ show, onHide, cartItems = [] }) {
    const total = cartItems.reduce((sum, item) => sum + item.price, 0);

    const onRemove = () => {
        console.log("Item will be removed from the cart. Making a backend Call.")
    }

    return (
        <Offcanvas show={show} onHide={onHide} placement="end">
            <Offcanvas.Header closeButton>
                <Offcanvas.Title>Your Cart</Offcanvas.Title>
            </Offcanvas.Header>
            <Offcanvas.Body>
                {cartItems.length === 0 ? (
                    <p>Your cart is empty.</p>
                ) : (
                    <Stack gap={3}>
                        {cartItems.map((item) => (
                            <Card key={item.id} className="shadow-sm">
                                <Card.Body>
                                    <Card.Title className="mb-2">{item.name}</Card.Title>
                                    <Card.Text className="text-muted">
                                        Price: ${item.price.toFixed(2)}
                                    </Card.Text>
                                    <div className="d-flex justify-content-end">
                                        <Button
                                            variant="outline-danger"
                                            size="sm"
                                            onClick={() => onRemove(item.id)}
                                        >
                                            Remove
                                        </Button>
                                    </div>
                                </Card.Body>
                            </Card>
                        ))}
                    </Stack>
                )}

                {cartItems.length > 0 && (
                    <div className="mt-4 border-top pt-3">
                        <div className="d-flex justify-content-between mb-3">
                            <strong>Total</strong>
                            <strong>${total.toFixed(2)}</strong>
                        </div>
                        <Button variant="success" className="w-100">
                            Checkout
                        </Button>
                    </div>
                )}
            </Offcanvas.Body>
        </Offcanvas>
    );
}
