import React, { useState, useEffect, useRef } from 'react';
import {
    View,
    Text,
    TextInput,
    Button,
    FlatList,
    StyleSheet,
} from 'react-native';
import { useSocket } from '../SocketContext';
import { baseUrl } from '../baseUrl';
import axios from 'axios';

const Chat = ({ route }) => {
    const { socket } = useSocket();
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const flatListRef = useRef(null);

    const getPastMessages = () => {
        if (route?.params?.user?.id) {
            axios.get(`${baseUrl}past_messages/`, {
                params: {
                    sender_id: route?.params?.user?.id,
                    reciever_id: route?.params?.reciever?.id
                }
            })
                .then(res => {
                    if (res?.data?.length) {
                        setMessages(res.data)
                    } else {
                        setMessages([])
                    }
                })
                .catch(error => {
                    console.log(error)
                })
        }
    }

    useEffect(() => {
        if (socket) {
            socket.addEventListener('message', (event) => {
                if (event?.data) {
                    try {
                        const message = JSON.parse(event.data);
                        if (typeof message === 'object' && !message.error) {
                            setMessages((prevMessages) => [...prevMessages, message]);
                        }
                    } catch (error) {
                        console.error(error);
                    }
                }
            });
        }
        getPastMessages();
    }, []);

    useEffect(() => {
        // Scroll to the bottom when messages change
        flatListRef.current.scrollToEnd({ animated: true });
    }, [messages]);

    const sendMessage = () => {
        if (newMessage.trim() === '') {
            return;
        }

        const message = {
            text: newMessage,
            senderId: route?.params?.user?.id,
            timestamp: new Date().toISOString(),
            recieverId: route?.params?.reciever?.id,
        };

        if (socket) {
            socket.send(JSON.stringify(message));
            setNewMessage('');
        }
    };

    const renderMessage = ({ item }) => {
        const isCurrentUser = item.senderId === route?.params?.user?.id;
        const messageDate = new Date(item.timestamp);

        const timestamp = `${messageDate.toLocaleString('default', {
            day: 'numeric',
            month: 'short',
            hour: 'numeric',
            minute: 'numeric',
        })}`;

        return (
            <View
                style={[
                    styles.messageContainer,
                    isCurrentUser ? styles.currentUserMessage : styles.otherUserMessage,
                ]}
            >
                <Text style={styles.messageText}>{item.text}</Text>
                <Text style={styles.timestamp}>{timestamp}</Text>
            </View>
        );
    };

    return (
        <View style={styles.container}>
            <FlatList
                ref={flatListRef}
                data={messages}
                keyExtractor={(item, index) => index.toString()}
                renderItem={renderMessage}
            />
            <View style={styles.inputContainer}>
                <TextInput
                    style={styles.textInput}
                    placeholder="Type your message..."
                    value={newMessage}
                    onChangeText={(text) => setNewMessage(text)}
                />
                <Button title="Send" onPress={sendMessage} />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'flex-end',
    },
    messageContainer: {
        maxWidth: '80%',
        padding: 10,
        margin: 5,
        borderRadius: 10,
    },
    currentUserMessage: {
        alignSelf: 'flex-end',
        backgroundColor: '#DCF8C6',
    },
    otherUserMessage: {
        alignSelf: 'flex-start',
        backgroundColor: '#FFFFFF',
    },
    messageText: {
        fontSize: 16,
    },
    inputContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingHorizontal: 10,
        paddingVertical: 5,
        backgroundColor: '#E0E0E0',
        paddingBottom: 40
    },
    textInput: {
        flex: 1,
        marginRight: 10,
        paddingHorizontal: 10,
        paddingVertical: 8,
        backgroundColor: '#FFFFFF',
        borderRadius: 5,
    },
    timestamp: {
        fontSize: 12,
        color: '#555',
        textAlign: 'right',
        marginTop: 2,
    },
});

export default Chat;
