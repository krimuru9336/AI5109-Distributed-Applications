// import React, { useState, useEffect } from 'react';
// import { View } from 'react-native';
// import { GiftedChat } from 'react-native-gifted-chat';
// import { io } from 'socket.io-client';
// import { API_URL } from "@env"

// const socket = io(API_URL);  // Replace with your Flask server URL

// const ChatScreen = ({ username }) => {
//     const [messages, setMessages] = useState([]);
//     // const { username } = route.params;

//     useEffect(() => {
//         // Listen for incoming messages
//         socket.on('data', (newMessage) => {
//             setMessages((prevMessages) => GiftedChat.append(prevMessages, newMessage));
//         });

//         return () => {
//             // Clean up event listeners
//             socket.off('data');
//         };
//     }, []);

//     const onSend = (newMessages = []) => {
//         socket.emit('data', {
//             message: newMessages[0].text,
//             // recipient: 'recipient_username',  // Replace with the recipient's username
//         });

//         setMessages((prevMessages) => GiftedChat.append(prevMessages, newMessages));
//     };

//     return (
//         <View style={{ flex: 1 }}>
//             <GiftedChat
//                 messages={messages}
//                 onSend={(newMessages) => onSend(newMessages)}
//                 user={{ _id: 1, name: username }}
//             />
//         </View>
//     );
// };

// export default ChatScreen;





















import { useEffect, useState, useCallback, useContext } from 'react';
import io from "socket.io-client";
import axios from 'axios';
import { TextInput, View, Button, Text } from 'react-native';
import { GiftedChat } from 'react-native-gifted-chat'
import SocketContext from '../context/SocketContext';
import { Input, InputField } from '@gluestack-ui/themed';

function App({ route }) {
    const [newMessage, setNewMessage] = useState("");
    const [allMessages, setAllMessages] = useState([]);
    const [myUserId, setByUserId] = useState("samman");
    const { socket } = useContext(SocketContext);
    const recipientId = route.params.receipientId;

    useEffect(() => {
        if (!socket) return
        socket.on("data", (data) => {
            setAllMessages(prev => [...prev, data])
        })
    }, [socket])

    const send = useCallback(() => {
        socket.emit("data", {
            recipient_id: recipientId,
            message: newMessage
        })
        setNewMessage("")
    }, [socket, newMessage, myUserId, recipientId])

    return (
        <View className="ChatScreen" style={{ height: '100%', display: 'flex', justifyContent: 'flex-end', padding: 10 }}>
            <View style={{ display: 'flex', gap: 10 }}>
                {
                    allMessages.map((m, idx) =>
                        <View key={`${m.message}-${idx}`} style={{ backgroundColor: "pink", color: "black" }}>
                            <Text>MESSAGE: {m.message}</Text>
                            <Text>SENT BY: {m.sender}</Text>
                            <Text>TIMESTAMP: {m.timestamp}</Text>
                        </View>)
                }
            </View>
            <View style={{ display: 'flex', gap: 5 }}>
                <Input
                    variant="outline"
                    size="md"
                >
                    <InputField placeholder="Enter Message" onChangeText={(text) => { setNewMessage(text) }} />
                </Input>
                <Button onPress={send} title='send' />
            </View>
        </View>
    );
}

export default App;



/*
    Date: 21/01/2024
    Author: Samman Adhikari
    Matriculation Number: 1493340
**/