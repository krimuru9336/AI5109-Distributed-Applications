import React, {useState, useEffect, useRef, useLayoutEffect} from 'react';
import {Text, TouchableOpacity} from 'react-native';
import {GiftedChat} from 'react-native-gifted-chat';
import useGlobalStore from '../core/global';
import {getMessagesByRoomId} from '../core/api';
import {Keyboard} from 'react-native';
import Title from '../common/Title';
import {Header, Icon} from 'react-native-elements';

const ChatScreen = ({route, navigation}) => {
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);
  const user = useGlobalStore(state => state.user);
  const {chat} = route.params;
  const chatRoomId = chat.chat_room_id;
  const chatRef = useRef(null);

  useLayoutEffect(() => {
    navigation.setOptions({
      headerShown: false,
    });
  }, []);

  useEffect(() => {
    // Connect to the WebSocket server
    console.log(chat);
    const newSocket = new WebSocket(
      'ws://10.0.2.2:8000/websocket/ws/' + chatRoomId,
    ); // replace with your actual URL

    newSocket.onmessage = event => {
      const newMessage = JSON.parse(event.data);
      if (newMessage.user._id != user.user_id) {
        setMessages(prevMessages =>
          GiftedChat.append(prevMessages, newMessage),
        );
      } else {
        console.log('Response Received', newMessage);
      }
    };

    setSocket(newSocket);

    return () => {
      // Close the WebSocket connection when the component is unmounted
      newSocket.close();
    };
  }, []);

  useEffect(() => {
    // Fetch previous messages when component mounts
    const fetchPreviousMessages = async () => {
      try {
        const response = await getMessagesByRoomId(chatRoomId);
        const data = await response.data;

        console.log(data);
        // Set the retrieved messages
        setMessages(data);

        // Scroll to the latest message
        if (chatRef.current) {
          chatRef.current.scrollToBottom();
        }
      } catch (error) {
        console.error('Error fetching previous messages:', error);
      }
    };

    fetchPreviousMessages();
  }, [chatRoomId]);

  const onSend = (newMessages = []) => {
    // Send the new message to the WebSocket server
    console.log(newMessages);
    socket.send(JSON.stringify(newMessages[0]));
    setMessages(prevMessages => GiftedChat.append(prevMessages, newMessages));
  };

  return (
    <>
      <Title text="chappat" fontSize={40} />
      {/*  */}

      <Text
        style={{
          color: 'white',
          backgroundColor: 'orange',
          paddingLeft: 10,
          paddingBottom: 10,
          paddingTop: 10,
          fontSize: 20,
          textTransform: 'capitalize',
        }}>
        {chat.username}
      </Text>

      <GiftedChat
        showUserAvatar
        messages={messages}
        onSend={onSend}
        user={{_id: user.user_id}}
        ref={chatRef}
      />
    </>
  );
};

export default ChatScreen;
