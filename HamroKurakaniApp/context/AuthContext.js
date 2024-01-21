import { createContext, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';

export default AuthContext = createContext({
  accessToken: '',
  setAccessToken: () => { }
});

const accessTokenKey = "accessToken";

export const AuthContextProvider = ({ children }) => {
  const [accessToken, setAccessToken] = useState("");

  useEffect(() => {
    (async () => {
      const accessToken = await AsyncStorage.getItem(accessTokenKey);
      if (accessToken) setAccessToken(accessToken);
    })()
  }, [])

  useEffect(() => {
    AsyncStorage.setItem(accessTokenKey, accessToken);
  }, [accessToken])

  return (<AuthContext.Provider value={{ accessToken, setAccessToken }}>
    {children}
  </AuthContext.Provider>)
};
