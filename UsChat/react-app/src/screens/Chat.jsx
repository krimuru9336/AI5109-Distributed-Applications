import React, {useState, useEffect, useRef, useLayoutEffect} from 'react';
import {
  Text,
  TouchableOpacity,
  Modal,
  TextInput,
  Alert,
  View,
  ToastAndroid,
} from 'react-native';
import {Avatar, Bubble, GiftedChat} from 'react-native-gifted-chat';
import useGlobalStore from '../core/global';
import {getMessagesByRoomId, editMessage, deleteMessage} from '../core/api';
import {Keyboard} from 'react-native';
import Title from '../common/Title';
import {Header, Icon} from 'react-native-elements';
import {FontAwesomeIcon} from '@fortawesome/react-native-fontawesome';
import {Avatar as Avatar2} from 'react-native-elements';
import Clipboard from '@react-native-clipboard/clipboard';
import {List, Divider, Portal, Dialog, Button} from 'react-native-paper';

const ChatScreen = ({route, navigation}) => {
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);
  const [editingMessage, setEditingMessage] = useState(null); // Track the message being edited
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editedMessageText, setEditedMessageText] = useState('');

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

    fetchPreviousMessages();
  }, [chatRoomId]);

  const fetchPreviousMessages = async () => {
    try {
      const response = await getMessagesByRoomId(chatRoomId);
      const data = await response.data;

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

  const onSend = (newMessages = []) => {
    // Send the new message to the WebSocket server
    socket.send(JSON.stringify(newMessages[0]));
    setMessages(prevMessages => GiftedChat.append(prevMessages, newMessages));
  };

  const onLongPress = (context, message) => {
    // Check if the message belongs to the current user
    const isCurrentUserMessage = message.user._id === user.user_id;

    // Options for the action sheet
    const options = isCurrentUserMessage
      ? ['Edit', 'Copy Text', 'Delete', 'Cancel']
      : ['Copy Text', 'Cancel'];

    const destructiveButtonIndex = isCurrentUserMessage ? 2 : 1;

    context.actionSheet().showActionSheetWithOptions(
      {
        options,
        cancelButtonIndex: options.length - 1,
        destructiveButtonIndex,
      },
      buttonIndex => {
        switch (buttonIndex) {
          case 0: // Edit
            // Implement your edit logic here
            if (isCurrentUserMessage) {
              handleEditMessage(message);
            }
            break;
          case 1: // Copy Text
            handleCopyText(message.text);
            break;
          case 2: // Delete
            // Implement your delete logic here
            if (isCurrentUserMessage) {
              handleDeleteMessage(message);
            }
            break;
          default:
            break;
        }
      },
    );
  };

  const handleCopyText = text => {
    // Implement logic to copy the text to the clipboard
    // You can use Clipboard API or any other method you prefer
    // For example, using Clipboard API from 'react-native'
    Clipboard.setString(text);
    // Optionally, you can show a notification or perform any other action
    // to indicate that the text has been copied.
    // For example, you can use ToastAndroid from 'react-native'
    ToastAndroid.show('Text copied to clipboard', ToastAndroid.SHORT);
  };

  const onSaveEdit = async () => {
    try {
      if (editingMessage) {
        await editMessage(editingMessage._id, editedMessageText);
        setEditModalVisible(false);
        fetchPreviousMessages();
      }
    } catch (e) {
      console.log('Error editing message', e);
    }
  };

  const onCancelEdit = () => {
    setEditModalVisible(false);
    setEditingMessage(null);
    setEditedMessageText('');
  };

  const handleEditMessage = async message => {
    setEditingMessage(message);
    setEditedMessageText(message.text);
    setEditModalVisible(true);
  };

  const handleDeleteMessage = async message => {
    try {
      const confirmDelete = await promptForDelete();
      if (confirmDelete) {
        // Call your API function to delete the message
        await deleteMessage(message._id);
        // Fetch updated messages and update the state
        fetchPreviousMessages();
      }
    } catch (e) {
      console.log('Exception while Deleting ', e);
    }
  };

  const promptForDelete = async () => {
    return new Promise(resolve => {
      Alert.alert(
        'Delete Message',
        'Are you sure you want to delete this message?',
        [
          {text: 'Cancel', onPress: () => resolve(false), style: 'cancel'},
          {text: 'Delete', onPress: () => resolve(true), style: 'destructive'},
        ],
      );
    });
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
          onLongPress={(context, message) => onLongPress(context, message)}
          onSend={onSend}
          renderAvatar={null}
          user={{_id: user.user_id, name: user.username}}
          ref={chatRef}
        />
      </View>

      <Portal>
        <Dialog visible={editModalVisible} onDismiss={onCancelEdit}>
          <Dialog.Title>Edit Message</Dialog.Title>
          <Dialog.Content>
            <TextInput
              value={editedMessageText}
              onChangeText={text => setEditedMessageText(text)}
              multiline
            />
          </Dialog.Content>
          <Dialog.Actions>
            <Button onPress={onCancelEdit}>Cancel</Button>
            <Button onPress={onSaveEdit}>Save</Button>
          </Dialog.Actions>
        </Dialog>
      </Portal>
    </>
  );
};

export default ChatScreen;
