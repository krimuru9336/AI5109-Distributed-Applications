/**
 * @format
 */

import React from 'react';
import { AppRegistry } from 'react-native';
import App from './App';
import { name as appName } from './app.json';

import { AuthContextProvider } from './context/AuthContext';
import { SocketContextProvider } from './context/SocketContext';
import { GluestackUIProvider } from '@gluestack-ui/themed';
import { config } from '@gluestack-ui/config'; // Optional if you want to use default theme

AppRegistry.registerComponent(appName, () =>
    AppWithProviders
);

export default function AppWithProviders() {
    return (
        <GluestackUIProvider config={config}>
            <AuthContextProvider>
                <SocketContextProvider>
                    <App />
                </SocketContextProvider>
            </AuthContextProvider>
        </GluestackUIProvider>
    )
}

