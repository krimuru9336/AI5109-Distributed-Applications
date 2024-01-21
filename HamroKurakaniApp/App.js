/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React, { useContext, useEffect, useState } from 'react';
import { SafeAreaView, Text, useColorScheme } from 'react-native';

import { Colors } from 'react-native/Libraries/NewAppScreen';
import AuthContext from './context/AuthContext';

import AuthScreen from './screens/AuthScreen';
import ChatScreen from './screens/ChatScreen';

function App() {
  const isDarkMode = useColorScheme() === 'dark';
  const { accessToken } = useContext(AuthContext);
  const [username, setUsername] = useState('');

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      {true ? (
        <ChatScreen username={username} />
      ) : (
        <AuthScreen
          onSuccessfulLogin={(username) => {
            setUsername(username);
          }}
        />
      )}
    </SafeAreaView>
  );
}

export default App;
