import Dropdown from 'react-bootstrap/Dropdown';
import Form from 'react-bootstrap/Form';

export default function MultiSelectDropdown({title, placeholder, options, selected, onChange}) {
    const selectedArray = Array.isArray(selected) ? selected : [];

    const toggleOption = (value) => {
        if (selectedArray.includes(value)) {
            onChange(selectedArray.filter((item) => item !== value));
        } else {
            onChange([...selectedArray, value]);
        }
    };

    return (
        <>
            <h6 className="text-muted mb-1 d-block">{title}</h6>
            <Dropdown className='mb-3'>
                <Dropdown.Toggle variant="outline-secondary" id="multi-select-dropdown">
                    {selectedArray.length > 0 ? selectedArray.join(', ') : placeholder}
                </Dropdown.Toggle>

                <Dropdown.Menu>
                    {options.map((opt) => (
                        <Form.Check
                            type="checkbox"
                            key={opt.value}
                            id={`dropdown-check-${opt.value}`}
                            label={opt.label}
                            checked={selectedArray.includes(opt.value)}
                            onChange={() => toggleOption(opt.value)}
                            className="px-3"
                        />
                    ))}
                </Dropdown.Menu>
            </Dropdown>
        </>
    );
}
