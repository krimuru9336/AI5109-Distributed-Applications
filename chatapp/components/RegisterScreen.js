import React, { useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { TextInput, Button } from 'react-native-paper';
import { baseUrl } from '../baseUrl';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { useNavigation } from '@react-navigation/native'; // Import useNavigation from @react-navigation/native
import  { useEffect } from 'react';

const RegisterScreen = ({ navigation }) => {

  const checkUserObject = async () => {
    const storedUserObject = await AsyncStorage.getItem('userObject');
    console.log(storedUserObject)

    if (storedUserObject) {
    
     console.log(storedUserObject)
      navigation.reset({
        index:0,
        routes:[{name:'Users'}]});
    } else {
      
      navigation.navigate('Register');
    }
  };

  const removeUserObject = async () => {
    await AsyncStorage.removeItem('userObject')
  }

  useEffect(() => { 
    // removeUserObject()
     checkUserObject();
  }, []);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleRegister =  () => {  
      axios.post(`${baseUrl}/register/`,{
        
        username: username,
        password: password
      
      }).then((res)=>{          
        AsyncStorage.setItem('userObject', JSON.stringify(res.data));
        navigation.navigate('Users');

      }).catch((error)=> {
        console.log(JSON.stringify(error))
        console.error('Error during registration:', error.message,error.response);
      })
  
  };

  return (
    <View style={styles.container}>
      <TextInput
        label="Username"
        value={username}
        onChangeText={(text) => setUsername(text)}
        style={styles.input}
      />
      <TextInput
        label="Password"
        value={password}
        onChangeText={(text) => setPassword(text)}
        secureTextEntry
        style={styles.input}
      />
      <Button mode="contained" onPress={handleRegister} style={styles.button}>
        Register
      </Button>
  
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    padding: 16,
  },
  input: {
    marginBottom: 16,
  },
  button: {
    marginTop: 8,
  },
  loginButton: {
    marginTop: 16,
  },
});

export default RegisterScreen;
