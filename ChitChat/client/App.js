import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import Module1 from './screens/module1';
import { createStackNavigator } from '@react-navigation/stack';
import SignUp from './screens/signUp';
import HomeScreen from './screens/home';
import Chat from './screens/chat';
import { SocketProvider } from './SocketContext';

const Stack = createStackNavigator();

export default App = () => {
  return (
    <NavigationContainer>
      <SocketProvider>
        <Stack.Navigator>
          <Stack.Screen name="Sign Up" component={SignUp} />
          {/* <Stack.Screen name="Module 1" component={Module1} /> */}
          <Stack.Screen name="Home" component={HomeScreen} />
          <Stack.Screen name="Chat" component={Chat} />
        </Stack.Navigator>
      </SocketProvider>
    </NavigationContainer>
  );
};

