import React, { useEffect, useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import AppButton from '../components/AppButton';
import AppTextInput from '../components/AppTextInput';
import axios from 'axios';
import { baseUrl } from '../baseUrl';
import AppToast from '../components/AppToast';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { getUserFromLocalStorage } from '../helper';

const SignUp = ({ navigation }) => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        checkUser();
    }, []);

    const handleSignUp = () => {
        if (name && email) {
            axios.post(`${baseUrl}user/`, {
                name,
                email
            }).then(async (res) => {
                if (res?.data?.id) {
                    await AsyncStorage.setItem('user', JSON.stringify(res.data));
                    navigation.reset({
                        index: 0,
                        routes: [{ name: 'Home' }],
                    });
                }
            }).catch((error) => {
                console.log(error)
                setError(error?.response?.data?.detail || "Server Error")
            })
        } else {
            setError("Name & Email Required")
        }

    };

    const checkUser = async () => {
        try {
            const user = await getUserFromLocalStorage();
            console.log(user)
            if (user && JSON.parse(user)?.id) {
                navigation.reset({
                    index: 0,
                    routes: [{ name: 'Home' }],
                });
            }
        } catch (error) {
            console.error('Error checking user:', error);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.header}>Sign Up</Text>
            <AppTextInput
                onChangeText={text => setName(text)}
                value={name}
                placeholder="Name"
            />
            <AppTextInput
                onChangeText={text => setEmail(text)}
                value={email}
                placeholder="Email"
                keyboardType="email-address"
                emailAddress
            />
            <AppButton onPress={handleSignUp} text='Sign Up' />
            {error && <AppToast setError={setError} visible={true} text={error} />}
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 20,
    },
    header: {
        fontSize: 24,
        marginBottom: 20,
    }
});

export default SignUp;
