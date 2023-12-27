import React, { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import AppButton from '../components/AppButton';
import AppTextInput from '../components/AppTextInput';

const SignUp = ({ navigation }) => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');

    const handleSignUp = () => {
        // Add logic to handle the sign-up process, e.g., send data to the server
        // For now, let's just log the user data
        console.log('Name:', name);
        console.log('Email:', email);

        // Navigate to the home screen or any other screen after sign-up
        navigation.navigate('Module 1')
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
            />
            <AppButton onPress={handleSignUp} text='Sign Up' />
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
