import {Text, View, Button, Alert, Pressable} from 'react-native';
import {useEffect, useState, useLayoutEffect} from 'react';
import {SafeAreaView} from 'react-native-safe-area-context';

import Title from '../common/Title';
import Input from '../common/Input';
import {loginUser} from '../core/api';
import useGlobalStore from '../core/global';
function SignInScreen({navigation}) {
  const [username, setUsername] = useState('amar');
  const [password, setPassword] = useState('amar');
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

        <Input title="Username" value={username} onChangeText={setUsername} />

        <Input
          title="Password"
          value={password}
          onChangeText={setPassword}
          secureTextEntry
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
      </View>
    </SafeAreaView>
  );
}

export default SignInScreen;
