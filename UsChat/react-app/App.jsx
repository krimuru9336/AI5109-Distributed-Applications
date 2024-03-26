import React, {useState} from 'react';
import {SafeAreaView, StatusBar, Text} from 'react-native';

import './src/core/fontawesome';

import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

import SplashScreen from './src/screens/Splash';
import SignInScreen from './src/screens/SignIn';
import SignUpScreen from './src/screens/SignUp';
import MessagesScreen from './src/screens/Messages';
import SearchScreen from './src/screens/Search';
import useGlobalStore from './src/core/global';
import ChatScreen from './src/screens/Chat';
import ChatListScreen from './src/screens/ChatListScreen';
import {PaperProvider} from 'react-native-paper';
const Stack = createNativeStackNavigator();

function App() {
  const [initialized] = useState(true);
  // const [authenticated] = useState(false);
  const authenticated = useGlobalStore(state => state.authenticated);
  return (
    <PaperProvider>
      <NavigationContainer>
        <StatusBar barStyle={'dark-content'}></StatusBar>
        <Stack.Navigator>
          {!initialized ? (
            <>
              <Stack.Screen name="Splash" component={SplashScreen} />
            </>
          ) : !authenticated ? (
            <>
              <Stack.Screen name="SignIn" component={SignInScreen} />
              <Stack.Screen name="SignUp" component={SignUpScreen} />
            </>
          ) : (
            <>
              <Stack.Screen name="Home" component={ChatListScreen} />
              <Stack.Screen name="Search" component={SearchScreen} />
              <Stack.Screen name="Messages" component={MessagesScreen} />
              <Stack.Screen name="Chat" component={ChatScreen} />
            </>
          )}
        </Stack.Navigator>
      </NavigationContainer>
    </PaperProvider>
  );
}

export default App;
