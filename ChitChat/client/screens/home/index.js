import React, { useEffect, useState, useRef } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import axios from 'axios';
import UserList from './components/UserList';
import { baseUrl } from "../../baseUrl";
import AppToast from '../../components/AppToast';
import { getUserFromLocalStorage } from '../../helper';
import { useSocket } from '../../SocketContext';

const HomeScreen = ({ navigation }) => {
    const [users, setUsers] = useState([]);
    const [error, setError] = useState('');
    const userRef = useRef('')
    const { socket, connectSocket } = useSocket();

    const setSocketConnection = async () => {
        const user = await getUserFromLocalStorage();
        if (user && JSON.parse(user)?.id) {
            userRef.current = JSON.parse(user)
            const userId = userRef.current.id;
            if (!socket) {
                connectSocket(userId);
            }
        }
    };

    useEffect(() => {

        setSocketConnection();
        getUsers();
    }, []);

    const getUsers = () => {

        axios.get(`${baseUrl}user/`)
            .then((res) => {
                if (res.data) {
                    setUsers(res.data)
                }
            }).catch((error) => {
                console.log(error)
                setError(error?.response?.data?.detail || "Server Error")
            })
    };

    const handleClick = (reciever) => {
        if (socket) {
            navigation.navigate('Chat', {
                user: userRef.current,
                reciever: reciever
            });
        }
        setSocketConnection();
    }

    return (
        <View style={styles.container}>
            <Text style={styles.header}>Users</Text>
            <UserList user={userRef.current} users={users} handleClick={handleClick} />
            {error && <AppToast setError={setError} visible={true} text={error} />}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
    },
    header: {
        fontSize: 24,
        marginBottom: 20,
    },
});

export default HomeScreen;
