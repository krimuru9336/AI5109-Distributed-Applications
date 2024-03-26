import { useEffect, useState, useCallback, useContext } from 'react';
import io from "socket.io-client";
import axios from 'axios';
import { TextInput, View, Text } from 'react-native';
import { GiftedChat } from 'react-native-gifted-chat'
import SocketContext from '../context/SocketContext';
import { Input, InputField, Button, ButtonText } from '@gluestack-ui/themed';
import AuthContext from '../context/AuthContext';
import { API_URL } from "@env";

const modes = {
    NEW_MESSAGE: 'new message',
    EDIT: 'edit',
}

function App({ route }) {
    const [newMessage, setNewMessage] = useState("");
    const [allMessages, setAllMessages] = useState([]);
    const [mode, setMode] = useState(modes.NEW_MESSAGE);
    const [editMessageId, setEditMessageId] = useState("");
    const { socket } = useContext(SocketContext);
    const { accessToken } = useContext(AuthContext);
    const recipientId = route.params.receipientId;

    useEffect(() => {
        (async () => {
            try {
                const response = await axios.get(`${API_URL}/chat_history`, {
                    params: {
                        user_id: recipientId
                    },
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });

                setAllMessages(response.data.chats);
            } catch (err) {
                console.log(err)
            }
        })()
    }, [])

    useEffect(() => {
        if (!socket) return

        socket.on("new_message", (newMessage) => {
            setAllMessages(prev => [...prev, newMessage])
        })

        socket.on("edit", (editedMessage) => {
            setAllMessages(prev => prev.map((message) => {
                if (message.id == editedMessage.id) {
                    return editedMessage;
                }
                return message;
            }))
        })

        socket.on("delete", (deletedMessage) => {
            setAllMessages(prev => prev.filter((message) =>
                message.id !== deletedMessage.id
            ))
        })
    }, [socket])

    const sendMessage = useCallback(() => {
        socket.emit("new_message", {
            recipient_id: recipientId,
            content: newMessage
        })
        setNewMessage("")
    }, [socket, newMessage, recipientId])

    const editMessage = useCallback(() => {
        socket.emit("edit", {
            message_id: editMessageId,
            content: newMessage
        })
        setNewMessage("")
    }, [socket, newMessage, editMessageId])

    const deleteMessage = useCallback((messageId) => {
        socket.emit("delete", {
            message_id: messageId,
        })
    }, [socket])

    return (
        <View className="ChatScreen" style={{ height: '100%', display: 'flex', justifyContent: 'flex-end', padding: 10 }}>
            <View style={{ display: 'flex', gap: 10 }}>
                {
                    allMessages.map((m, idx) =>
                        <View key={`${m.message}-${idx}`} style={{ backgroundColor: "pink", color: "black", display: 'flex', flexDirection: "row", justifyContent: "space-between" }}>
                            <View>
                                <Text>MESSAGE: {m.content}</Text>
                                <Text>SENT BY: {m.sender_username}</Text>
                                <Text>TIMESTAMP: {m.sent_at}</Text>
                            </View>
                            <View>
                                <Button
                                    size="xs"
                                    variant="solid"
                                    action="secondary"
                                    onPress={() => {
                                        setMode(modes.EDIT);
                                        setEditMessageId(m.id);
                                    }}
                                >
                                    <ButtonText>Edit</ButtonText>
                                </Button>
                                <Button
                                    size="xs"
                                    variant="solid"
                                    action="negative"
                                    onPress={() => deleteMessage(m.id)}
                                >
                                    <ButtonText>Delete</ButtonText>
                                </Button>
                            </View>
                        </View>)
                }
            </View>
            <View style={{ display: 'flex', gap: 5, marginTop: 10 }}>
                <Input
                    variant="outline"
                    size="md"
                >
                    <InputField placeholder="Enter Message" onChangeText={(text) => { setNewMessage(text) }} />
                </Input>
                {
                    mode == modes.NEW_MESSAGE ?
                        <Button
                            onPress={sendMessage}
                            size="md"
                            variant="solid"
                            action="primary"
                        >
                            <ButtonText>Send</ButtonText>
                        </Button> :
                        <View style={{ display: 'flex', flexDirection: "row" }}>
                            <Button
                                onPress={editMessage}
                                size="md"
                                variant="outline"
                                action="primary"
                                style={{ flex: 1 }}
                            >
                                <ButtonText>Edit</ButtonText>
                            </Button>
                            <Button
                                size="md"
                                variant="solid"
                                action="negative"
                                onPress={() => { setMode(modes.NEW_MESSAGE) }}
                            >
                                <Text>X</Text>
                            </Button>
                        </View>
                }
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