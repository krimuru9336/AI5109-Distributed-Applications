import React, { useState, useEffect } from 'react';

const App = () => {
  const [bmi, setBmi] = useState(null);
  const [entries, setEntries] = useState([]);
  const [formData, setFormData] = useState({
    name: '',
    weight: 0,
    height: 0,
  });

  const calculateBmi = async () => {
    try {
      const response = await fetch('https://172.178.97.205/api/bmi/calcbmi', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) {
        throw new Error('Failed to calculate BMI');
      }

      const data = await response.json();

      setBmi(data[data.length-1]);
      setEntries(data);
      // Fetch updated entries after calculating BMI
      //fetchEntries();
    } catch (error) {
      console.error(error.message);
    }
  };

  const fetchEntries = async () => {
    try {
      const response = await fetch('https://172.178.97.205/api/bmi/calcbmi');
      const data = await response.json();
      setEntries(data);
    } catch (error) {
      console.error(error.message);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    calculateBmi();
  };

  useEffect(() => {
    // Fetch entries on initial mount
    fetchEntries();
  }, []);

  return (
    <div>
      {bmi && (
        <div>
          <p>Hello {bmi.name}</p>
          <p>Your BMI is: {bmi.bmi}</p>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <label htmlFor="name">Name:</label>
        <input
          type="text"
          required
          id="name"
          name="name"
          value={formData.name}
          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        />
        <br />
        <br />

        <label htmlFor="weight">Weight:</label>
        <input
          type="number"
          required
          id="weight"
          name="weight"
          value={formData.weight}
          onChange={(e) => setFormData({ ...formData, weight: e.target.value })}
        />
        <br />
        <br />

        <label htmlFor="height">Height:</label>
        <input
          type="number"
          required
          id="height"
          name="height"
          value={formData.height}
          onChange={(e) => setFormData({ ...formData, height: e.target.value })}
        />
        <br />
        <br />

        <input type="submit" value="Submit" />
      </form>

      <p>DB entries:</p>
      {entries.map((entry) => (
        <p key={entry.id}>
          Name: {entry.name} Weight: {entry.weight} Height: {entry.height} BMI: {entry.bmi}
        </p>
      ))}
    </div>
  );
};

export default App;
