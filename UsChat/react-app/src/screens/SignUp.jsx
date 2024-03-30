import {
  Text,
  View,
  Button,
  Alert,
  Pressable,
  ToastAndroid,
  StyleSheet,
} from 'react-native';
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

  const handleRegister = async () => {
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
          backgroundColor: '#9893DA',
        }}>
        <View style={styles.cardContainer}>
          <Title text="UsChat" />

          <Text
            style={{
              color: '#9893DA',
              textAlign: 'center',
              padding: 15,
              fontSize: 20,
              fontWeight: '200',
            }}>
            Register User
          </Text>
          <Input title="Username" value={username} onChangeText={setUsername} />

          <Input
            title="Password"
            value={password}
            onChangeText={setPassword}
            secureTextEntry
          />

          <Input
            title="Confirm Password"
            value={confirmPassword}
            onChangeText={setConfirmPassword}
            secureTextEntry
          />

          <Pressable
            style={{
              borderRadius: 10,
              backgroundColor: 'white',
              display: 'flex',
              flexDirection: 'row',
              flex: 0,
              padding: 10,
              marginTop: 20,
              alignItems: 'center',
              justifyContent: 'center',
            }}
            title="Sign In"
            onPress={handleRegister}>
            <Text style={{fontSize: 20, color: '#9893DA'}}>Register</Text>
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
                style={{color: '#9893DA'}}
                onPress={() => navigation.navigate('SignIn')}>
                Login
              </Text>
            </Text>
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  cardContainer: {
    shadowColor: 'rgba(0, 0, 0, 0.8)',
    shadowOffset: {x: 10, y: 10},
    shadowOpacity: 1,
    alignSelf: 'stretch',
    borderRadius: 20,
    padding: 30,
    backgroundColor: '#BBBDF6',
    marginTop: 20,
  },
  cardContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginLeft: 20,
  },
});

export default SignUpScreen;
