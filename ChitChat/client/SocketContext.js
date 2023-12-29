import React, { createContext, useContext, useState } from 'react';
import { socketUrl } from './baseUrl';

const SocketContext = createContext();

export const useSocket = () => {
    return useContext(SocketContext);
};

export const SocketProvider = ({ children }) => {
    const [socket, setSocket] = useState(null);

    const connectSocket = (userId) => {
        const newSocket = new WebSocket(`ws://${socketUrl}ws/${userId}`);

        newSocket.addEventListener('open', () => {
            console.log('Connected')
        });

        newSocket.addEventListener('close', () => {
            console.log('disconnected')
            setSocket(null)
        });

        newSocket.addEventListener('error', (event) => {
            console.error('WebSocket error:', event);
            setSocket(null)
        });

        setSocket(newSocket);

        return newSocket;
    };

    return (
        <SocketContext.Provider value={{ socket, connectSocket }}>
            {children}
        </SocketContext.Provider>
    );
};
