import React, { useState, useEffect } from 'react';
import {
    TextField,
    Button,
    FormControl,
    Table,
    TableBody,
    TableRow,
    TableCell,
    TableContainer,
    Paper,
    ThemeProvider,
    createTheme,
    IconButton,
    Typography,
    CircularProgress,
} from '@mui/material';
import { Delete } from '@mui/icons-material';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { baseUrl } from '../baseUrl';


const darkTheme = createTheme({
    palette: {
        mode: 'dark',
    },
});

const Ex1 = () => {
    const [formData, setFormData] = useState({
        name: '',
        phone: '',
    });
    const [tableData, setTableData] = useState([]);
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value,
        });
    };

    const fetchStudents = () => {
        setLoading(true);
        axios.get(`${baseUrl}students`)
            .then((response) => {
                setTableData(response.data);
                setLoading(false);
            })
            .catch((error) => {
                console.error('API request failed:', error);
                setLoading(false);
            });
    }

    useEffect(() => {
        fetchStudents()
    }, [])


    const handleSubmit = (e) => {
        e.preventDefault();
        setLoading(true);
        if (validatephone(formData.phone)) {
            axios
                .post(`${baseUrl}add`, formData)
                .then((response) => {
                    fetchStudents()
                    setFormData({ name: '', phone: '' });
                    setLoading(false);
                })
                .catch((error) => {
                    console.error('API request failed:', error);
                    setLoading(false);
                });
        } else {
            setLoading(false);
            alert('Please enter a valid phone number.');
        }
    };

    const handleDelete = (index, id) => {
        setLoading(true);
        axios
            .delete(`${baseUrl}students/${id}`)
            .then(() => {
                fetchStudents()
                setLoading(false);
            })
            .catch((error) => {
                console.error('API request failed:', error);
                setLoading(false);
            });
    };


    const validatephone = (phone) => {
        // A simple validation for a 10-digit phone number
        return /^\d{10}$/.test(phone);
    };

    return (
        <>
            <ThemeProvider theme={darkTheme}>
                <div>
                    <Paper elevation={3} sx={{ padding: 2 }}>
                        <form onSubmit={handleSubmit}>
                            <FormControl fullWidth>
                                <TextField
                                    required
                                    name="name"
                                    label="Name"
                                    variant="outlined"
                                    value={formData.name}
                                    onChange={handleChange}
                                    sx={{ marginBottom: 2 }}
                                />
                            </FormControl>

                            <FormControl fullWidth>
                                <TextField
                                    required
                                    name="phone"
                                    label="Phone Number"
                                    variant="outlined"
                                    value={formData.phone}
                                    onChange={handleChange}
                                    sx={{ marginBottom: 2 }}
                                />
                            </FormControl>

                            <Button type="submit" variant="contained" color="primary" disabled={loading}>
                                {loading ? <CircularProgress size={24} color="inherit" /> : 'Submit'}
                            </Button>
                        </form>
                    </Paper>
                </div>

                <Paper elevation={3} sx={{ padding: 2, marginTop: 2 }}>
                    <Typography variant="h5" align="center" sx={{ marginBottom: 2 }}>
                        Students Data
                    </Typography>
                    <TableContainer>
                        <Table>
                            <TableBody>
                                {tableData.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={3} align="center">
                                            No Data Found
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    <TableRow>
                                        <TableCell>Name</TableCell>
                                        <TableCell>Phone Number</TableCell>
                                        <TableCell>Action</TableCell>
                                    </TableRow>
                                )}
                                {tableData.map((row, index) => (
                                    <TableRow key={index}>
                                        <TableCell>{row.name}</TableCell>
                                        <TableCell>{row.phone}</TableCell>
                                        <TableCell>
                                            <IconButton
                                                color="error"
                                                onClick={() => handleDelete(index, row.id)}
                                                disabled={loading}
                                            >
                                                {loading ? (
                                                    <CircularProgress size={20} color="inherit" />
                                                ) : (
                                                    <Delete />
                                                )}
                                            </IconButton>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Paper>
                <p>Author: Dipesh Kewalramani</p>
                <p>Date: 05.11.2023</p>
            </ThemeProvider>
        </>
    );
};

export default Ex1;
