import { useEffect, useState, useCallback, useContext, useRef } from 'react';
import io from "socket.io-client";
import axios from 'axios';
import { Image, View, Text, TouchableOpacity } from 'react-native';
import { GiftedChat } from 'react-native-gifted-chat'
import SocketContext from '../context/SocketContext';
import { Input, InputField, Button, ButtonText } from '@gluestack-ui/themed';
import AuthContext from '../context/AuthContext';
import { API_URL } from "@env";
import { launchImageLibrary } from 'react-native-image-picker';
import Video, { VideoRef } from 'react-native-video';

const FILE_TYPES = {
    text: "text",
    image: "image",
    video: "video"
}

const modes = {
    NEW_MESSAGE: 'new message',
    EDIT: 'edit',
}

function App({ route }) {
    const [newMessage, setNewMessage] = useState("");
    const [allMessages, setAllMessages] = useState([]);
    const [mode, setMode] = useState(modes.NEW_MESSAGE);
    const [editMessageId, setEditMessageId] = useState("");
    const [fetchChatHistory, setFetchChatHistory] = useState(true);
    const { socket } = useContext(SocketContext);
    const { accessToken } = useContext(AuthContext);
    const recipientId = route.params.receipientId;
    const recipientType = route.params.recipientType;
    const videoRef = useRef(null);

    useEffect(() => {
        if(!fetchChatHistory) return;
        (async () => {
            const params = {};
            if (recipientType == "user") {
                params["user_id"] = recipientId
            } else if (recipientType == "group") {
                params["group_id"] = recipientId
            }
            try {
                const response = await axios.get(`${API_URL}/chat_history`, {
                    params,
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });

                setAllMessages(response.data.chats);
            } catch (err) {
                console.log(err)
            }
        })()
        setFetchChatHistory(false);
    }, [fetchChatHistory])

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

    const sendMessage = useCallback((message, type) => {
        socket.emit("new_message", {
            recipient_id: recipientId,
            recipient_type: recipientType,
            content: message,
            message_type: type
        })
        setNewMessage("")
    }, [socket, recipientId, recipientType])

    const editMessage = useCallback(() => {
        socket.emit("edit", {
            message_id: editMessageId,
            content: newMessage
        })
        setNewMessage("")
        setFetchChatHistory(true);
    }, [socket, newMessage, editMessageId])

    const deleteMessage = useCallback((messageId) => {
        socket.emit("delete", {
            message_id: messageId,
        })
    }, [socket])

    const pickAndSendMediaFile = useCallback(async () => {
        const file = await launchImageLibrary({
            mediaType: 'image/video',
        })

        const fileType = file.assets[0].type.split("/")[0];

        const asset = file.assets[0];
        const fileObject = {
            uri: asset.uri,
            type: asset.type,
            name: asset.fileName
        }

        const formData = new FormData();
        formData.append("file", fileObject);
        formData.append("file_type", fileType);

        try {
            const response = await axios.post(`${API_URL}/upload_file`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${accessToken}`
                },
            });
            const imageUri = response.data;
            sendMessage(imageUri, fileType)
        } catch (err) {
            console.log(err);
        }
    }, [])

    return (
        <View className="ChatScreen" style={{ height: '100%', display: 'flex', justifyContent: 'flex-end', padding: 10 }}>
            <View style={{ display: 'flex', gap: 10 }}>
                {
                    allMessages.map((m, idx) => (
                        <View key={`${m.content}-${idx}`}>
                            {(m.content_type == FILE_TYPES.text) &&
                                <View style={{ backgroundColor: "pink", color: "black", display: 'flex', flexDirection: "row", justifyContent: "space-between" }}>
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
                                </View>
                            }
                            {
                                (m.content_type == FILE_TYPES.image) &&
                                <View>
                                    <Image
                                        style={{ width: 200, height: 200 }}
                                        source={{ uri: `${API_URL + m.content}` }}
                                    />
                                </View>
                            }
                            {
                                (m.content_type == FILE_TYPES.video) &&
                                <View>
                                    <TouchableOpacity>
                                        <View style={{ width: "100%", height: 200, position: 'relative' }}>
                                            <Video
                                                source={{ uri: "http://localhost:5000/static/video/20240328_230115_77cc98d9c659419b954891bd01b9263c.mp4" }}
                                                ref={videoRef}
                                                style={{
                                                    position: 'absolute',
                                                    top: 0,
                                                    left: 0,
                                                    bottom: 0,
                                                    right: 0,
                                                    height: "100%",
                                                    width: "100%"
                                                }}
                                                resizeMode="contain"
                                            />
                                        </View>
                                    </TouchableOpacity>
                                </View>
                            }
                        </View>
                    ))
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
                        <View style={{ display: 'flex', flexDirection: 'row' }}>
                            <Button
                                onPress={() => sendMessage(newMessage, FILE_TYPES.text)}
                                size="md"
                                variant="solid"
                                action="primary"
                                style={{ flex: 1 }}
                            >
                                <ButtonText>Send</ButtonText>
                            </Button>
                            <Button
                                onPress={pickAndSendMediaFile}
                                size="md"
                                variant="solid"
                                action="secondary"
                            >
                                <ButtonText>+</ButtonText>
                            </Button>
                        </View>
                        :
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