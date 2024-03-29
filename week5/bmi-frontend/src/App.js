import React, { useState, useEffect } from 'react';
import axios from 'axios';

const App = () => {
    const [persons, setPersons] = useState([]);
    const [person, setPerson] = useState({ name: '', weight: 0, height: 0 });

    const fetchPersons = async () => {
        try {
            const response = await axios.get('http://20.163.60.62:8081/api/persons');
            setPersons(response.data);
        } catch (error) {
            console.error('Error fetching persons:', error);
        }
    };

    const createPerson = async () => {
        try {
            await axios.post('http://20.163.60.62:8081/api/persons', person);
            // Refresh the list of persons after creating a new one
            fetchPersons();
        } catch (error) {
            console.error('Error creating person:', error);
        }
    };

    useEffect(() => {
        // Fetch initial list of persons when the component mounts
        fetchPersons();
    }, []);

    return (
        <div>
            <h1>BMI Calculator</h1>
            <form>
                <label htmlFor="name">Name:</label>
                <input
                    type="text"
                    id="name"
                    value={person.name}
                    onChange={(e) => setPerson({ ...person, name: e.target.value })}
                    placeholder="Name"
                />
                <br />
                <label htmlFor="weight">Weight:</label>
                <input
                    type="number"
                    id="weight"
                    value={person.weight}
                    onChange={(e) => setPerson({ ...person, weight: parseFloat(e.target.value) })}
                    placeholder="Weight (kg)"
                />
                <br />
                <label htmlFor="height">Height:</label>
                <input
                    type="number"
                    id="height"
                    value={person.height}
                    onChange={(e) => setPerson({ ...person, height: parseFloat(e.target.value) })}
                    placeholder="Height (m)"
                />
                <br />
                <button type="button" onClick={createPerson}>
                    Calculate BMI
                </button>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Weight (kg)</th>
                        <th>Height (m)</th>
                        <th>BMI</th>
                    </tr>
                </thead>
                <tbody>
                    {persons.map((p) => (
                        <tr key={p.id}>
                            <td>{p.name}</td>
                            <td>{p.weight}</td>
                            <td>{p.height}</td>
                            <td>{p.bmi}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default App;
