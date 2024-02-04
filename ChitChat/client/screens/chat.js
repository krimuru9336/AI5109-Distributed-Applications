import React, { useState, useEffect, useRef } from 'react';
import {
    View,
    Text,
    TextInput,
    Button,
    FlatList,
    StyleSheet, Modal
} from 'react-native';
import { useSocket } from '../SocketContext';
import { baseUrl } from '../baseUrl';
import axios from 'axios';
import { TouchableOpacity } from 'react-native-gesture-handler';
import AppButton from '../components/AppButton';

const Chat = ({ route }) => {
    const { socket } = useSocket();
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const flatListRef = useRef(null);
    const [selectedMessage, setSelectedMessage] = useState(null);
    const [showContextMenu, setShowContextMenu] = useState(false);
    const [showEditDialog, setShowEditDialog] = useState(false);
    const editedMessage = useRef('')

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
                            if (message.messageType === 'delete' || message.messageType === 'update') {
                                getPastMessages();
                            } else {
                                setMessages((prevMessages) => [...prevMessages, message]);
                            }
                        }
                    } catch (error) {
                        console.error(error);
                    }
                }
            });
        }
    }, [socket]);

    useEffect(() => {
        getPastMessages();
    }, [])

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

    const handleEdit = () => {
        setShowEditDialog(true);
        setShowContextMenu(false);
    };

    const handleDelete = () => {
        socket.send(JSON.stringify({
            messageType: "delete",
            messageId: selectedMessage.id,
            recieverId: selectedMessage.recieverId
        }));
        getPastMessages();
        setShowContextMenu(false)
    };

    const handleEditDialogSave = () => {
        socket.send(JSON.stringify({
            messageType: "update",
            messageId: selectedMessage.id,
            recieverId: selectedMessage.recieverId,
            updatedText: editedMessage.current,
        }));
        getPastMessages();
        setShowEditDialog(false);
        setSelectedMessage(null)
        editedMessage.current = ''
    };


    const ContextMenu = ({ item }) => {

        return (
            <Modal
                visible={showContextMenu}
                transparent={true}
                animationType="slide"
            >
                <View style={styles.modalContainer}>
                    <View style={styles.contextMenu}>
                        <TouchableOpacity
                            style={styles.menuItem}
                            onPress={() => handleEdit()}
                        >
                            <Text style={styles.menuText}>Edit</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={styles.menuItem}
                            onPress={() => handleDelete()}
                        >
                            <Text style={styles.menuText}>Delete</Text>
                        </TouchableOpacity>
                        <AppButton onPress={() => setShowContextMenu(false)} text='Cancel' />
                    </View>
                </View>
            </Modal>
        );
    };

    const onMessageLongPress = (message, event) => {
        if (message.senderId === route?.params?.user?.id) {
            setSelectedMessage(message);
            setShowContextMenu(true);
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
            <View>
                {selectedMessage?.id === item.id && showContextMenu && (
                    <ContextMenu
                        style={styles.contextMenu}
                        item={item}
                    />
                )}
                <TouchableOpacity
                    onLongPress={(event) => isCurrentUser ? onMessageLongPress(item, event) : null}
                >
                    <View
                        style={[
                            styles.messageContainer,
                            isCurrentUser ? styles.currentUserMessage : styles.otherUserMessage,
                        ]}
                    >
                        <Text style={styles.messageText}>{item.text}</Text>
                        <Text style={styles.timestamp}>{timestamp}</Text>
                    </View>
                </TouchableOpacity>
            </View>)

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
            <Modal
                visible={showEditDialog}
                transparent={true}
                animationType="slide"
            >
                <View style={styles.modalContainer}>
                    <View style={styles.contextMenu}>
                        <TextInput
                            style={styles.editInput}
                            onChangeText={(e) => {
                                editedMessage.current = e
                            }}
                            defaultValue={selectedMessage?.text}
                            multiline
                        />
                        <AppButton text="Save" onPress={handleEditDialogSave} />
                        <Button title="Cancel" onPress={() => {
                            setShowEditDialog(false)
                            editedMessage.current = ''
                        }} />
                    </View>
                </View>
            </Modal>
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
    contextMenu: {
        position: 'absolute',
        backgroundColor: 'white',
        padding: 10,
        borderRadius: 5,
        elevation: 15,
        alignSelf: 'flex-end',
        zIndex: 10,
        right: 30,
        top: 75,
        gap: 10,
    },
    modalContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    contextMenu: {
        width: '80%',
        backgroundColor: 'white',
        borderRadius: 10,
        padding: 20,
        elevation: 5,
    },
    menuItem: {
        paddingVertical: 15,
        borderBottomColor: '#E0E0E0',
        alignItems: 'center',
    },
    cancelButton: {
        borderBottomWidth: 0,
        marginTop: 10,
        backgroundColor: '#FFEB3B',
    },
    menuText: {
        fontSize: 18,
    },
    editInput: {
        borderWidth: 1,
        marginBottom: 10,
        padding: 5,
        borderRadius: 5
    },
});

export default Chat;
