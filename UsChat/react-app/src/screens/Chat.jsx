import React, {useState, useEffect, useRef, useLayoutEffect} from 'react';
import {
  Text,
  TouchableOpacity,
  Modal,
  TextInput,
  Alert,
  View,
  ToastAndroid,
  Image,
  ActivityIndicator,
} from 'react-native';
import {
  Avatar,
  Bubble,
  GiftedChat,
  Composer,
  LeftAction,
  InputToolbar,
  SendButton,
  Send,
} from 'react-native-gifted-chat';
import useGlobalStore from '../core/global';
import {
  getMessagesByRoomId,
  editMessage,
  deleteMessage,
  sendMessageWithMedia,
  uploadMedia,
  baseURL,
} from '../core/api';
import {Keyboard} from 'react-native';
import Title from '../common/Title';
import {Header, Icon} from 'react-native-elements';
import {Avatar as Avatar2} from 'react-native-elements';
import Clipboard from '@react-native-clipboard/clipboard';
import {List, Divider, Portal, Dialog, Button} from 'react-native-paper';
import {FontAwesomeIcon} from '@fortawesome/react-native-fontawesome';
import {launchImageLibrary} from 'react-native-image-picker';

const ChatScreen = ({route, navigation}) => {
  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);
  const [editingMessage, setEditingMessage] = useState(null); // Track the message being edited
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [editedMessageText, setEditedMessageText] = useState('');
  const [uploading, setUploading] = useState(false);
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
      `ws://18cb-2405-201-1004-1aeb-9869-b419-899e-4b66.ngrok-free.app/websocket/ws/${chatRoomId}`,
    ); // replace with your actual URL

    newSocket.onmessage = async event => {
      const newMessage = JSON.parse(event.data);
      console.log('New Message ', newMessage);
      if (newMessage?.isUpdate) {
        await fetchPreviousMessages();
      } else if (newMessage.user._id != user.user_id) {
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
      console.error('Error fetching previous messages:');
      console.log(error.response.data);
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
        await fetchPreviousMessages();
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
        await fetchPreviousMessages();
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

  const handleImageUpload = async () => {
    try {
      const options = {
        title: 'Select Image',
        mediaType: 'photo',
        maxWidth: 1000,
        maxHeight: 1000,
      };

      launchImageLibrary(options, async result => {
        console.log(result);
        if (result?.assets?.length && result?.assets?.[0] && !result.canceled) {
          let mediaItem = result.assets[0];

          uploadAndSendMediaMessage(mediaItem, true);
        }
      });
    } catch (e) {
      console.log(e);
    }
  };

  const uploadAndSendMediaMessage = async (mediaItem, isImage) => {
    setUploading(true);
    try {
      // Upload image to server
      console.log(mediaItem);
      let media = mediaItem;
      if (!isImage) {
        let name = mediaItem.uri.split('/').slice(-1)[0];
        media = {
          uri: mediaItem.linkUri,
          fileName: name,
          type: mediaItem.mime,
        };
      }
      const uploadResponse = await uploadMedia(media);
      console.log(uploadResponse.data);
      if (
        uploadResponse &&
        uploadResponse.data &&
        uploadResponse.data.filename &&
        uploadResponse.data.message_type
      ) {
        let filename = uploadResponse.data.filename;
        let response = await sendMessageWithMedia(
          chatRoomId,
          user.user_id,
          filename,
          uploadResponse.data.message_type,
        );

        if (response) {
          await fetchPreviousMessages();
        }
      }
    } catch (error) {
      if (error && error.response && error.response.data) {
        console.log(error.response.data);
      }
      console.log(error.response);
      console.error('Error uploading image:', error.request);
    } finally {
      setUploading(false);
    }
  };

  const scrollToBottomComponent = () => {
    return <FontAwesomeIcon icon="angle-double-down" size={22} color="#333" />;
  };

  const renderComposer = props => (
    <Composer {...props} textInputProps={{onImageChange}} />
  );

  const renderSend = props => {
    return (
      <Send {...props}>
        <View style={{marginRight: 10, marginBottom: 10}}>
          <FontAwesomeIcon icon="paper-plane" size={25} color="#BBBDF6" />
        </View>
      </Send>
    );
  };

  const onImageChange = ({nativeEvent}) => {
    try {
      uploadAndSendMediaMessage(nativeEvent, false);
    } catch (e) {
      console.log('error in gif upload', gif);
    }
  };

  return (
    <>
      <View style={{flex: 1, backgroundColor: 'white'}}>
        <Title text="UsChat" fontSize={26} />
        {/*  */}

        <View
          style={{
            color: 'white',
            backgroundColor: '#9893DA',
            paddingLeft: 10,
            paddingBottom: 10,
            paddingTop: 10,
            textTransform: 'capitalize',
            display: 'flex',
            flexDirection: 'row',
          }}>
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <FontAwesomeIcon
              icon="chevron-left"
              size={30}
              style={{color: 'white'}}
            />
          </TouchableOpacity>
          <Avatar2
            rounded
            size={30}
            title={
              chat.type == 1
                ? chat?.username?.charAt(0).toUpperCase()
                : chat?.chat_room_name?.charAt(0).toUpperCase()
            }
            source={{
              uri: 'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Ffree-icon%2Fprofile_9706583&psig=AOvVaw1njRIqcJgzRVPDhfGwwEcd&ust=1705938836582000&source=images&cd=vfe&opi=89978449&ved=0CBMQjRxqFwoTCMi578Xr7oMDFQAAAAAdAAAAABAI',
            }}
          />
          <Text style={{fontSize: 20}}>
            {' '}
            {chat.type == 2 ? chat.chat_room_name : chat.username}
          </Text>
        </View>

        <GiftedChat
          // showUserAvatar
          messages={messages}
          renderSend={renderSend}
          alwaysShowSend
          onLongPress={(context, message) => onLongPress(context, message)}
          onSend={onSend}
          user={{_id: user.user_id, name: user.username}}
          ref={chatRef}
          renderComposer={renderComposer}
          renderActions={() => (
            <TouchableOpacity style={{padding: 10}} onPress={handleImageUpload}>
              <FontAwesomeIcon icon="photo-film" size={28} color="grey" />
            </TouchableOpacity>
          )}
          renderMessageImage={props => (
            <Image
              source={{uri: props.currentMessage.image}}
              style={{width: 200, height: 200, borderRadius: 8}}
            />
          )}
          textInputStyle={{color: 'black'}}
          scrollToBottom
          scrollToBottomComponent={scrollToBottomComponent}
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
      {uploading && (
        <View
          style={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            justifyContent: 'center',
            alignItems: 'center',
          }}>
          <ActivityIndicator size="large" color="#fff" />
        </View>
      )}
    </>
  );
};

export default ChatScreen;
