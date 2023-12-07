/*
 * Author: Christian Jumtow
 * Created: 30.11.2023
 * MNr.: 1166358
 */

import './App.css';

import React, { useState, useEffect } from 'react';

function App() {
  const [bmis, setBmis] = useState([]);
  const [bmi, setBmi] = useState(null);
  const [formData, setFormData] = useState({
    name: 'John',
    weight: 100,
    height: 170
  });

  useEffect(() => {
    fetchData();
  }, []); // Daten laden, wenn die Komponente montiert wird

  const fetchData = async () => {
    try {
      //20.55.35.13/bmi
      const response = await fetch('https://20.55.35.13/bmi');
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setBmis(data);
    } catch (error) {
      console.error('There was a problem fetching the data:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('https://20.55.35.13/bmi', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      const data = await response.json();
      setBmi(data);
      fetchData(); // Aktualisierte Daten abrufen
    } catch (error) {
      console.error('There was a problem submitting the form:', error);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <div>
      <h1>BMI Calculator</h1>
      <form onSubmit={handleSubmit}>
        <label htmlFor="name">Name:</label><br />
        <input type="text" id="name" name="name" value={formData.name} onChange={handleChange} /><br />
        <label htmlFor="weight">Weight in kg:</label><br />
        <input type="number" id="weight" name="weight" value={formData.weight} onChange={handleChange} /><br />
        <label htmlFor="height">Height in cm:</label><br />
        <input type="number" id="height" name="height" value={formData.height} onChange={handleChange} /><br /><br />
        <input type="submit" value="Submit" />
      </form>

      {bmi && (
        <div>
          <p>{`Hi ${bmi.name}, your BMI is:`}</p>
          <p>{bmi.bmi}</p>
        </div>
      )}

      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>BMI</th>
          </tr>
        </thead>
        <tbody>
          {bmis.length === 0 ? (
            <tr>
              <td colSpan="2">No BMIs Available</td>
            </tr>
          ) : (
            bmis.map((bmi, index) => (
              <tr key={index}>
                <td>{bmi.name}</td>
                <td style={{textAlign: 'center'}}>{bmi.bmi}</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}

export default App;
