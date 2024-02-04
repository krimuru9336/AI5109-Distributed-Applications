import React, { useCallback, useEffect, useState } from "react";
import { StyleSheet, View, Alert, TextInput } from "react-native";
import { GiftedChat } from "react-native-gifted-chat";
import { baseUrl, socketUrl } from "../baseUrl";
import axios from "axios";

const PersonalChat = ({ route }) => {
  const { user_id, receiver_id } = route.params;
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);

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

  const getChatHistory = () => {
    axios
      .get(`${baseUrl}/get-chat-history/${user_id}/${receiver_id}/`)
      .then((res) => {
        console.log(res.data);
        if (res.data?.length) {
          setMessages(res.data.map((message) => JSON.parse(message.text)));
        }
      })
      .catch((err) => {
        console.log(err);
      });
  };

  // Get Chat History
  useEffect(() => {
    if (user_id && receiver_id) {
      getChatHistory();
    }
  }, [user_id, receiver_id]);

  const onSend = (newMessage) => {
    if (newMessage?.[0]?.text?.trim() !== "" && socket) {
      try {
        socket.send(
          JSON.stringify({
            ...newMessage[0],
            receiver_id,
            type: "send",
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
                console.log(editedMessage,"CHECK")
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

    console.log("message", message, message.id, context);
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

  return (
    <View style={{ flex: 1, backgroundColor: "white", paddingBottom: 50 }}>
      <GiftedChat
        messages={messages}
        onSend={(newMessage) => {
          onSend(newMessage);
        }}
        user={{ _id: user_id.toString() }}
        onLongPress={onLongPress}
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
  textInput: {
    flex: 1,
    marginRight: 10,
    padding: 8,
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 5,
  },
});

export default PersonalChat;
