import { Ionicons } from '@expo/vector-icons';
import axios from "axios";
import { ResizeMode, Video } from 'expo-av';
import * as ImagePicker from 'expo-image-picker';
import React, { useEffect, useState } from "react";
import { Alert, StyleSheet, TouchableOpacity, View } from "react-native";
import { GiftedChat } from "react-native-gifted-chat";
import { baseUrl, socketUrl } from "../baseUrl";


function generateUniqueKey() {
  const timestamp = new Date().getTime(); // Get current timestamp
  const random = Math.random().toString(36).substring(2, 10); // Generate random string
  const uniqueKey = timestamp.toString(36) + random; // Combine timestamp and random string
  return uniqueKey;
}


const PersonalChat = ({ route }) => {
  const { user_id, receiver_id, isGroup, group_id,username } = route.params;
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);
  const [image, setImage] = useState(null);

  // Socket Connection
  useEffect(() => {
    const newSocket = new WebSocket(`ws://${socketUrl}/ws/${user_id}`);
    setSocket(newSocket);
  }, [user_id]);

  // Socket Events Listenting
  useEffect(() => {
    if (socket) {
      socket.addEventListener("message", (event) => {
        let data = JSON.parse(event.data) || {};
        if (data?.type == "refresh") {
          getChatHistory();
        } else {
          setMessages((previousMessages) =>
            GiftedChat.append(previousMessages, [data])
          );
        }
      });
    }
  }, [socket]);
  const pickImage = async () => {

    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.All,
      allowsEditing: true,
      aspect: [4, 3],
      quality: 1,
    });

    console.log();
    console.log(result.assets[0].fileName);
    uploadImage(result.assets[0].uri, result.assets[0].fileName, result.assets[0].type);
    if (!result.canceled) {

    }
  };
  const getChatHistory = () => {
    if (isGroup) {
      axios
        .get(`${baseUrl}/get-group-history/${group_id}/`)
        .then((res) => {
          if (res.data?.length) {
            setMessages(res.data.map((message) => JSON.parse(message.text)));
          }
        })
        .catch((err) => {
          console.log(err);
        });
    } else {
      axios
        .get(`${baseUrl}/get-chat-history/${user_id}/${receiver_id}/`)
        .then((res) => {
          if (res.data?.length) {
            setMessages(res.data.map((message) => JSON.parse(message.text)));
          }
        })
        .catch((err) => {
          console.log(err);
        });
    }
  };

  // Get Chat History
  useEffect(() => {
    if (user_id && receiver_id) {
      getChatHistory();
    }
  }, [user_id, receiver_id]);

  const onSend = (newMessage) => {
    if ((newMessage?.[0]?.text?.trim() !== "" || newMessage?.[0]?.image) && socket) {
      try {
        socket.send(
          JSON.stringify({
            ...newMessage[0],
            receiver_id,
            type: "send",
            group_id: group_id || null
          })
        );
      } catch (error) {
        console.log(error);
      }
      setMessages((previousMessages) =>
        GiftedChat.append(previousMessages, newMessage)
      );
    }
  };
  const onDelete = (message_id) => {
    axios
      .post(`${baseUrl}/delete-message/`, {
        message_id: message_id,
      })
      .then(() => {
        setMessages((previousMessages) =>
          previousMessages.filter((message) => message._id != message_id)
        );
        socket.send(
          JSON.stringify({
            receiver_id,
            type: "refresh",
            group_id,
            user_id
          })
        );
      })
      .catch((error) => {
        console.log(JSON.stringify(error));
        console.error(
          "Error while deleting message:",
          error.message,
          error.response
        );
      });
  };
  const onEditMessage = (message) => {
    if (message) {
      Alert.prompt(
        "Edit Message",
        "",
        [
          {
            text: "Cancel",
            style: "cancel",
          },
          {
            text: "Edit",

            onPress: (newText) => {
              if (newText != "") {
                const editedMessage = {
                  ...message,
                  text: newText,
                };
                axios
                  .put(`${baseUrl}/edit-message/`, {
                    message: editedMessage,
                  })
                  .then(() => {
                    setMessages((prevMessages) =>
                      prevMessages.map((msg) =>
                        msg._id === editedMessage._id ? editedMessage : msg
                      )
                    );
                    socket.send(
                      JSON.stringify({
                        receiver_id,
                        type: "refresh",
                        group_id,
                        user_id
                      })
                    );
                  })

                  .catch((error) => {
                    console.log(JSON.stringify(error));
                    console.error(
                      "Error while editing message:",
                      error.message,
                      error.response
                    );
                  });
              }
            },
          },
        ],
        "plain-text",
        message.text // Pre-fill the input with the existing message
      );
    }
  };

  const onLongPress = (context, message) => {
    if (message.user._id != user_id) {
      return null;
    }

    const options = ["Edit", "Delete", "Cancel"];
    const cancelButtonIndex = options.length - 1;

    context.actionSheet().showActionSheetWithOptions(
      {
        options,
        cancelButtonIndex,
      },
      (buttonIndex) => {
        switch (buttonIndex) {
          case 0:
            onEditMessage(message);
            break;
          case 1:
            onDelete(message._id);
            break;
          default:
            break;
        }
      }
    );
  };

  const uploadImage = async (imageUri, fileName, type) => {
    console.log(type)
    const formData = new FormData();
    const randomString = Math.random().toString(36).substring(7); // Generate random string
    const newName = `${fileName}_${randomString}`
    formData.append("file", {
      uri: imageUri,
      name: newName,
    });
    formData.append("name", fileName);
    formData.append("type", type);

    axios
      .post(`${baseUrl}/upload/`, formData)
      .then((res) => {
        console.log(res.data)

        const newMessage = {
          _id: generateUniqueKey(),
          createdAt: new Date(),
          user: {
            _id: user_id.toString(),
          },
        }
        if (type == 'image') {
          newMessage['image'] = `${baseUrl}/media/${res.data.filename}`
        } else if (type == 'video') {
          newMessage['video'] = `${baseUrl}/media/${res.data.filename}`
        }

        onSend([newMessage])
      })
      .catch((err) => {
        console.log(err);
      });



  };

  const renderActions = (props) => (
    <TouchableOpacity onPress={pickImage} >
      <View style={styles.button}>
        <Ionicons name="camera" size={29} color="#3399ff" />
      </View>

    </TouchableOpacity>
  );

  const renderVideo = (props) => {
    return (
      <View style={{ position: 'relative', height: 150, width: 250 }}>
        <Video
          style={styles.video}
          source={{
            uri: props.currentMessage.video,
          }}
          useNativeControls
          resizeMode={ResizeMode.CONTAIN}
          onError={(err) => {
            console.log(err)
          }}
        />
      </View>
    )
  }
console.log(user_id)

  return (
    <View style={{ flex: 1, backgroundColor: "white", paddingBottom: 50 }}>
      <GiftedChat
        messages={messages}
        onSend={(newMessage) => {
          onSend(newMessage);
        }}
        renderActions={renderActions}
        user={{ _id: user_id.toString(),name:username }}
        onLongPress={onLongPress}
        renderMessageVideo={renderVideo}
        renderUsernameOnMessage={true}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  inputContainer: {
    flexDirection: "row",
    alignItems: "center",
    padding: 10,
    borderTopWidth: 1,
    borderTopColor: "#ccc",
  },
  button: {
    flexDirection: "row",
    alignItems: "center",
    padding: 10,
    borderTopWidth: 1,
    borderTopColor: "#ccc",
  },
  textInput: {
    flex: 1,
    marginRight: 10,
    padding: 8,
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 5,
  }, actionButton: {
    marginRight: 10,
    marginBottom: 10,
  },
  video: {
    position: 'absolute',
    left: 0,
    top: 0,
    height: 150,
    width: 250,
    borderRadius: 20,
  }

});

export default PersonalChat;
