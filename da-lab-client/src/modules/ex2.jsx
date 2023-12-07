import React, { useState, useEffect } from 'react';
import { CircularProgress, Button } from '@mui/material';
import axios from 'axios';


const Ex2 = () => {

    const [res, setRes] = useState('')
    const [loading, setLoading] = useState(false)
    const apiEndpoint = 'https://techy-api.vercel.app/api/json'



    const fetchApi = () => {
        setLoading(true);
        axios.get(apiEndpoint)
            .then((response) => {
                setRes(response.data);
                setLoading(false);
            })
            .catch((error) => {
                console.error('API request failed:', error);
                setRes('API request failed')
                setLoading(false);
            });
    }

    useEffect(() => {
        fetchApi()
    }, [])

    return (
        <>
            Api Endpoint - {apiEndpoint}
            <br />
            Response: {loading ? <CircularProgress size={24} color="inherit" /> : JSON.stringify(res)}
            <br />
            <Button style={{ marginTop: 10 }} onClick={fetchApi}>Fetch Again</Button>
            <p>Author: Dipesh Kewalramani</p>
            <p>Date: 17.11.2023</p>
        </>
    );
};

export default Ex2;
