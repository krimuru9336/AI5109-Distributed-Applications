import {Text, View, Button, Alert, Pressable, ToastAndroid} from 'react-native';
import {useEffect, useState, useLayoutEffect} from 'react';
import {SafeAreaView} from 'react-native-safe-area-context';

import Title from '../common/Title';
import Input from '../common/Input';
import {registerUser} from '../core/api';
import useGlobalStore from '../core/global';
function SignUpScreen({navigation}) {
  const [username, setUsername] = useState();
  const [password, setPassword] = useState();
  const [confirmPassword, setConfirmPassword] = useState();
  const login = useGlobalStore(state => state.login);
  handleUsername = e => {
    e.preventDefault();
    setUsername(e.target.value);
  };

  handlePassword = e => {
    e.preventDefault();
    setPassword(e.target.value);
  };

  const handleSignUp = async () => {
    try {
      if (password != confirmPassword) {
        ToastAndroid.showWithGravityAndOffset(
          "Passwords doesn't match",
          ToastAndroid.LONG,
          ToastAndroid.BOTTOM,
          0,
          90,
        );
      } else {
        const response = await registerUser(username, password);
        console.log(response);
        if (response && response.data && response.data.user) {
          // Handle successful login
          login(response.data.user);
          Alert.alert('Login successful!');
          // Navigate to the next screen or perform other actions
        } else {
          // Handle login failure
          Alert.alert('Login failed', 'Invalid username or password');
        }
      }
    } catch (error) {
      if (
        error &&
        error.response &&
        error.response.data &&
        error.response.data.detail
      ) {
        Alert.alert('Error', error.response.data.detail);
      } else {
        Alert.alert('Error', 'Internal Server Error During Registration');
      } // Handle other errors
    }
  };
  useLayoutEffect(() => {
    console.log(navigation);
    navigation.setOptions({
      headerShown: false,
    });
  }, []);

  return (
    <SafeAreaView style={{flex: 1}}>
      <View
        style={{
          flex: 1,
          justifyContent: 'center',
          paddingHorizontal: 20,
          backgroundColor: '#FFF9E0',
        }}>
        <Title text="Chappat" />

        <Text
          style={{
            color: 'orange',
            textAlign: 'center',
            padding: 15,
            fontSize: 20,
            fontWeight: '200',
          }}>
          Register
        </Text>

        <Input title="Username" value={username} onChangeText={setUsername} />

        <Input
          title="Password"
          value={password}
          onChangeText={setPassword}
          secureTextEntry={true}
        />
        <Input
          title="Confirm Password"
          value={confirmPassword}
          onChangeText={setConfirmPassword}
          secureTextEntry={true}
        />

        <Pressable
          style={{
            borderRadius: 20,
            backgroundColor: 'orange',
            display: 'flex',
            flexDirection: 'row',
            flex: 0,
            padding: 10,
            marginTop: 25,
            alignItems: 'center',
            justifyContent: 'center',
          }}
          title="Register"
          onPress={handleSignUp}>
          <Text style={{fontSize: 20}}>Register</Text>
        </Pressable>
        <View
          style={{
            padding: 10,
            paddingTop: 20,
            textAlign: 'center',
          }}>
          <Text
            style={{
              fontSize: 16,
              color: 'black',
              textAlign: 'center',
            }}>
            Already a user?{' '}
            <Text
              style={{color: 'orange'}}
              onPress={() => navigation.navigate('SignIn')}>
              Login
            </Text>
          </Text>
        </View>
      </View>
    </SafeAreaView>
  );
}

export default SignUpScreen;
