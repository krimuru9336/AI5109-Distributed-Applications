import React, {useState, useEffect, useRef, useLayoutEffect} from 'react';
import {Text, TouchableOpacity, View} from 'react-native';
import {Avatar, Bubble, GiftedChat} from 'react-native-gifted-chat';
import useGlobalStore from '../core/global';
import {getMessagesByRoomId} from '../core/api';
import {Keyboard} from 'react-native';
import Title from '../common/Title';
import {Header, Icon} from 'react-native-elements';
import {FontAwesomeIcon} from '@fortawesome/react-native-fontawesome';
import {Avatar as Avatar2} from 'react-native-elements';
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

  const renderBubble = props => {
    console.log(props.currentMessage);
    let username = props.currentMessage.user.name;
    let color = getColor(username);

    return (
      <Bubble
        {...props}
        textStyle={{
          right: {
            color: 'white',
          },
        }}
        timeTextStyle={{
          right: {color: 'white'},
          left: {color: 'black'},
        }}
        wrapperStyle={{
          left: {
            backgroundColor: color,
          },
          right: {
            backgroundColor: '#9893DA',
          },
        }}
      />
    );
  };

  const renderAvatar = props => {
    console.log(props.currentMessage);
    let username = props.currentMessage.user.name;
    let color = getColor(username);

    return (
      <Avatar
        {...props}
        textStyle={{
          color: 'black',
        }}
        imageStyle={{
          left: {
            backgroundColor: color,
          },
          right: {
            backgroundColor: '#9893DA',
          },
        }}
      />
    );
  };

  const getColor = username => {
    let sumChars = 0;
    for (let i = 0; i < username.length; i++) {
      sumChars += username.charCodeAt(i);
    }

    const colors = [
      '#F1F7B5', // pastel yellow
      '#A8D1D1', // pastel green
      '#FD8A8A', // pastel red
      '#FFCBCB', // pastel pink
      '#DFEBEB', // pastel grey
    ];
    console.log(colors[sumChars % colors.length]);
    return colors[sumChars % colors.length];
  };

  return (
    <>
      <View style={{flex: 1, backgroundColor: 'white'}}>
        <Title text="UsChat" fontSize={26} />
        {/*  */}

        <Text
          style={{
            color: 'white',
            backgroundColor: '#9893DA',
            paddingLeft: 10,
            paddingBottom: 10,
            paddingTop: 10,
            fontSize: 20,
            textTransform: 'capitalize',
          }}>
          <FontAwesomeIcon icon="chevron-left" style={{color: 'white'}} />{' '}
          <Avatar2
            rounded
            size={30}
            title={
              chat.type == 1
                ? chat?.username.charAt(0).toUpperCase()
                : chat?.chat_room_name.charAt(0).toUpperCase()
            }
            source={{
              uri: 'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Ffree-icon%2Fprofile_9706583&psig=AOvVaw1njRIqcJgzRVPDhfGwwEcd&ust=1705938836582000&source=images&cd=vfe&opi=89978449&ved=0CBMQjRxqFwoTCMi578Xr7oMDFQAAAAAdAAAAABAI',
            }}
          />{' '}
          {chat.username}
        </Text>

        <GiftedChat
          // showUserAvatar
          messages={messages}
          // renderBubble={renderBubble}
          onSend={onSend}
          renderAvatar={null}
          user={{_id: user.user_id}}
          ref={chatRef}
        />
      </View>
    </>
  );
};

export default ChatScreen;
