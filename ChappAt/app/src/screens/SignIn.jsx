import {Text, View, Button, Alert, Pressable} from 'react-native';
import {useEffect, useState, useLayoutEffect} from 'react';
import {SafeAreaView} from 'react-native-safe-area-context';

import Title from '../common/Title';
import Input from '../common/Input';
import {loginUser} from '../core/api';
import useGlobalStore from '../core/global';
function SignInScreen({navigation}) {
  const [username, setUsername] = useState();
  const [password, setPassword] = useState();
  const login = useGlobalStore(state => state.login);
  handleUsername = e => {
    e.preventDefault();
    setUsername(e.target.value);
  };

  handlePassword = e => {
    e.preventDefault();
    setPassword(e.target.value);
  };

  const handleSignIn = async () => {
    try {
      const response = await loginUser(username, password);
      console.log('Response Received ', response.data);
      if (response && response.data && response.data.user) {
        // Handle successful login
        login(response.data.user);
        Alert.alert('Login successful!');
        // Navigate to the next screen or perform other actions
      } else {
        // Handle login failure
        Alert.alert('Login failed', 'Invalid username or password');
      }
    } catch (error) {
      console.error('Error during login:', error);
      // Handle other errors
      Alert.alert('Error', 'An unexpected error occurred');
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
          Login
        </Text>

        <Input title="Username" value={username} onChangeText={setUsername} />

        <Input
          title="Password"
          value={password}
          onChangeText={setPassword}
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
            marginTop: 10,
            alignItems: 'center',
            justifyContent: 'center',
          }}
          title="Sign In"
          onPress={handleSignIn}>
          <Text style={{fontSize: 20}}>Login</Text>
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
            Not a user?{' '}
            <Text
              style={{color: 'orange'}}
              onPress={() => navigation.navigate('SignUp')}>
              Register Here
            </Text>
          </Text>
        </View>
      </View>
    </SafeAreaView>
  );
}

export default SignInScreen;
