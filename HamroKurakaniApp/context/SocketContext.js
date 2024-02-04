import { createContext, useState } from 'react';

export default SocketContext = createContext({
    socket: undefined,
    setSocket: () => { }
});

export const SocketContextProvider = ({ children }) => {
    const [socket, setSocket] = useState();

    return (
        <SocketContext.Provider value={{ socket, setSocket }}>
            {children}
        </SocketContext.Provider>
    )
};
