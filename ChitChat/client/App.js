import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import HomeScreen from './screens/module1';
import { createStackNavigator } from '@react-navigation/stack';
import SignUp from './screens/signUp';

const Stack = createStackNavigator();

export default App = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="Sign Up" component={SignUp} />
        <Stack.Screen name="Module 1" component={HomeScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

