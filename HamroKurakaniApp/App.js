/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useContext, useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import io from "socket.io-client";
import { API_URL } from "@env";

import ChatListScreen from './screens/ChatListScreen';
import ChatScreen from './screens/ChatScreen';
import SocketContext from './context/SocketContext';
import AuthContext from './context/AuthContext';
import AuthScreen from './screens/AuthScreen';
import { Box, Button, ButtonText, Heading } from '@gluestack-ui/themed';

const Stack = createNativeStackNavigator();

function App() {
  const { setSocket } = useContext(SocketContext);
  const { accessToken, setAccessToken } = useContext(AuthContext);

  useEffect(() => {
    if (accessToken) setSocket(io(`${API_URL}?access_token=${accessToken}`))
  }, [accessToken])

  const logout = () => {
    setAccessToken("")
  }

  return (
    accessToken ? (
      <NavigationContainer>
        <Stack.Navigator initialRouteName='ChatListScreen'
          screenOptions={{
            headerTitle: () =>
              <Box display='flex' flexDirection='row' justifyContent='space-between' alignItems='center' w="90%">
                <Heading size="sm">Hamro KuraKani</Heading>
                <Button
                  size="md"
                  variant="solid"
                  action="negative"
                >
                  <ButtonText onPress={logout}>Logout</ButtonText>
                </Button>
              </Box>
          }}
        >
          <Stack.Screen name="ChatListScreen" component={ChatListScreen} />
          <Stack.Screen name="ChatScreen" component={ChatScreen} />
        </Stack.Navigator>
      </NavigationContainer >
    ) : (
      <AuthScreen />
    )
  );
}

export default App;
