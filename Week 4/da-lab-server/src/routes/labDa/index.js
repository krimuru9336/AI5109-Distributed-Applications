import express from "express";
import { con } from "../../index.js";

const router = express.Router();

router.get('/', (req, res) => {
  res.send("HERE")
});

router.get('/students', (req, res) => {
  con.query('SELECT * FROM lab_da.ex1', (err, rows) => {
    if (err) throw err;

    console.log('Data received from Db:');
    res.send(rows);
  });
});

router.post('/add', (req, res) => {
  console.log(req.body)
  const { name, phone } = req.body;

  if (!name || !phone) {
    return res.status(400).json({ error: 'Name and phone are required.' });
  }

  const newStudent = { name, phone };

  con.query('INSERT INTO lab_da.ex1 SET ?', newStudent, (err, result) => {
    if (err) {
      console.error('Error inserting record:', err);
      return res.status(500).json({ error: 'Failed to insert record.' });
    }
    console.log(newStudent)
    console.log('Inserted new student record with ID:', result.insertId);
    res.status(201).json({ message: 'Student record created successfully' });
  });
});


router.delete('/students/:id', (req, res) => {
  const studentId = req.params.id;

  con.query('DELETE FROM lab_da.ex1 WHERE id = ?', [studentId], (err, result) => {
    if (err) {
      console.error('Error deleting record:', err);
      return res.status(500).json({ error: 'Failed to delete record.' });
    }

    if (result.affectedRows === 0) {
      return res.status(404).json({ error: 'Student record not found.' });
    }

    console.log('Deleted student record with ID:', studentId);
    res.status(200).json({ message: 'Student record deleted successfully' });
  });
});

export default router

// Author: Dipesh Kewalramani Date: 05.11.2023

