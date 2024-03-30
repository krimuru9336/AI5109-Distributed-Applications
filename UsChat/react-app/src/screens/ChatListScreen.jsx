// ChatListScreen.js
import React, {useEffect, useState, useLayoutEffect} from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  Button,
  ToastAndroid,
} from 'react-native';
import {Avatar} from 'react-native-elements';
import useGlobalStore from '../core/global';
import Title from '../common/Title';
import {getAllChats, getUsers, startChat} from '../core/api';
import {SearchBar} from 'react-native-elements';
import {FontAwesomeIcon} from '@fortawesome/react-native-fontawesome';
import {FloatingAction} from 'react-native-floating-action';
import {List, Divider, Portal, Dialog} from 'react-native-paper';
import MultiSelect from 'react-native-multiple-select';
import Input from '../common/Input';

const ChatListScreen = ({navigation}) => {
  const [chats, setChats] = useState([]);
  const [searchedChats, setSearchedChats] = useState([]);
  const [searchTerm, setSearchTerm] = useState();
  const [openChatDialog, setOpenChatDialog] = useState(false);
  const [openGroupChatDialog, setOpenGroupChatDialog] = useState(false);
  const [allUsers, setAllUsers] = useState([]);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [groupName, setGroupName] = useState();
  const [allChatUserIds, setAllChatUserIds] = useState();
  const [openSelectChatDialog, setOpenSelectChatDialog] = useState(false);
  const user = useGlobalStore(state => state.user);

  useEffect(() => {
    // Fetch chat data from the API
    // setInterval(() => {
    fetchChats();
    // }, 1000);
    getAllUsers();
  }, []);

  const handleNewChatClose = e => {
    e?.preventDefault();
    setOpenChatDialog(false);
    setOpenGroupChatDialog(false);
    setSelectedUsers([]);
    setGroupName(null);
  };
  const handleStartChat = async isGroup => {
    try {
      if (selectedUsers && selectedUsers.length == 0)
        ToastAndroid.show('Please select a user', ToastAndroid.LONG);
      if (isGroup && !groupName) ToastAndroid.show('Please enter a group name');
      else {
        let user_ids = [...selectedUsers, user.user_id];

        let name = groupName;
        let response = await startChat(name, isGroup, user_ids);
        if (response && response.data && response.data.chat) {
          await fetchChats();
          let chat = response.data.chat;
          chat['chat_room_name'] = chat['name'];

          if (chat) {
            navigation.navigate('Chat', {chat});
            setOpenChatDialog(false);
            setOpenGroupChatDialog(false);
          } else {
            console.log(chats.length);
          }
        }
      }
    } catch (e) {
      if (e?.response?.data) {
        console.log(e.response.data);
      }
      console.log(e);
    }
  };
  const fetchChats = async () => {
    try {
      const response = await getAllChats(user.user_id);
      const data = await response.data;
      setChats(data);
      setSearchedChats(data);
      let allChats_user_ids = new Set();
      for (let chat of data) {
        if (chat.user_id && !allChats_user_ids.has(chat.user_id)) {
          allChats_user_ids.add(chat.user_id);
        }
      }
      setAllChatUserIds(allChats_user_ids);
    } catch (error) {
      console.log(error?.response?.data);
      console.error('Error fetching chats:', error);
    }
  };
  const getAllUsers = async () => {
    try {
      const response = await getUsers(user.user_id);
      const data = await response.data;
      setAllUsers(data);
    } catch (error) {
      if (error?.response?.data) {
        console.log(error.response.data);
      }
      console.error('Error fetching chats:', error);
    }
  };

  const updateSearch = search => {
    setSearchTerm(search);

    if (search.trim() == '') {
      setSearchedChats(chats);
    } else {
      let tempSearchedChats = chats.filter(chat =>
        chat.username.toLowerCase().includes(search.toLowerCase()),
      );
      setSearchedChats(tempSearchedChats);
    }
  };

  const navigateToChat = chat => {
    // Navigate to 'Chat' screen, pass chat details as route params
    navigation.navigate('Chat', {chat});
  };

  useLayoutEffect(() => {
    navigation.setOptions({
      headerShown: false,
    });
  }, []);

  return (
    <View style={styles.container}>
      <Title text={'UsChat'} fontSize={26} />
      <View
        style={{
          padding: 0,
          margin: 0,
          marginTop: 20,
          display: 'flex',
          flexDirection: 'row',
          alignContent: 'center',
          justifyContent: 'space-between',
        }}>
        <TouchableOpacity
          onPress={async () => {
            await fetchChats();
            await getAllUsers();
          }}>
          <Text style={styles.header}>Chats</Text>
        </TouchableOpacity>

        <TouchableOpacity onPress={() => setOpenSelectChatDialog(true)}>
          <FontAwesomeIcon size={20} icon="add" />
        </TouchableOpacity>
      </View>
      <FlatList
        style={{paddingTop: 15}}
        data={chats}
        keyExtractor={item =>
          item?.message_id?.toString() || item.username + Date.now()
        }
        renderItem={({item}) => (
          <TouchableOpacity onPress={() => navigateToChat(item)}>
            <View style={styles.chatItem}>
              <View style={{margin: 5, marginLeft: 10}}>
                <Avatar
                  rounded
                  title={
                    item.type == 1
                      ? item?.username.charAt(0).toUpperCase()
                      : item?.chat_room_name.charAt(0).toUpperCase()
                  }
                  source={{
                    uri: 'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Ffree-icon%2Fprofile_9706583&psig=AOvVaw1njRIqcJgzRVPDhfGwwEcd&ust=1705938836582000&source=images&cd=vfe&opi=89978449&ved=0CBMQjRxqFwoTCMi578Xr7oMDFQAAAAAdAAAAABAI',
                  }}
                />
              </View>
              <View style={styles.chatText}>
                <Text
                  style={{
                    fontSize: 18,
                    fontWeight: '600',
                    color: 'black',
                    width: 200,
                  }}>
                  {item.type == 1 ? item?.username : item?.chat_room_name}
                </Text>
                {item?.message_type == 'text' ? (
                  <Text style={{color: 'black'}}>{item.message_text}</Text>
                ) : (
                  <Text style={{color: 'black'}}>{item.message_type}</Text>
                )}
                {/* Add more details as needed */}
              </View>
            </View>
          </TouchableOpacity>
        )}
      />

      <Portal>
        <Dialog
          dismissable
          style={{backgroundColor: '#fff'}}
          visible={openSelectChatDialog}
          onDismiss={() => setOpenSelectChatDialog(false)}>
          <Dialog.Title style={{color: '#BBBDF6'}}>
            Select Chat Type
          </Dialog.Title>
          <Dialog.Content>
            <Button
              title="Start Chat"
              color="#BBBDF6"
              onPress={e => {
                setOpenChatDialog(true);
                setOpenSelectChatDialog(false);
              }}>
              Start Chat
            </Button>
            <View style={{padding: 5}} />
            <Button
              title="Start Group Chat"
              color="#BBBDF6"
              onPress={e => {
                setOpenGroupChatDialog(true);
                setOpenSelectChatDialog(false);
              }}>
              Start Chat
            </Button>
          </Dialog.Content>
        </Dialog>
      </Portal>
      {/* Single Chat Dialog */}
      <Portal>
        <Dialog
          style={{backgroundColor: '#fff'}}
          visible={openChatDialog}
          onDismiss={handleNewChatClose}>
          <Dialog.Title style={{color: '#BBBDF6'}}>Search User</Dialog.Title>
          <Dialog.Content>
            <Text>Select User you want to start chat with</Text>
            <MultiSelect
              selectedItems={selectedUsers}
              items={allUsers.filter(
                user => allChatUserIds && !allChatUserIds.has(user.user_id),
              )}
              single={true}
              uniqueKey="user_id"
              removeSelected={true}
              selectText="Search User"
              displayKey="username"
              hideSubmitButton
              noItemsText="No user found"
              searchInputPlaceholderText="Search user"
              onSelectedItemsChange={users => {
                console.log(users);
                setSelectedUsers(users);
              }}
            />
          </Dialog.Content>
          <Dialog.Actions>
            <Button
              title="Start Chat"
              color="#BBBDF6"
              onPress={e => handleStartChat(false)}>
              Start Chat
            </Button>
            <TouchableOpacity
              title="Cancel"
              style={{width: 70, marginLeft: 20}}
              onPress={handleNewChatClose}>
              <Text style={{color: '#BBBDF6', fontSize: 18}}>Cancel</Text>
            </TouchableOpacity>
          </Dialog.Actions>
        </Dialog>
      </Portal>

      <Portal>
        <Dialog
          style={{backgroundColor: '#fff'}}
          visible={openGroupChatDialog}
          onDismiss={handleNewChatClose}>
          <Dialog.Title style={{color: '#BBBDF6'}}>Search Users</Dialog.Title>
          <Dialog.Content>
            <Text>Select Users you want to start group chat with</Text>
            <Input
              title={'Group Name'}
              value={groupName}
              onChangeText={setGroupName}
            />
            <View style={{padding: 5}} />
            <MultiSelect
              selectedItems={selectedUsers}
              items={allUsers}
              single={false}
              uniqueKey="user_id"
              removeSelected={true}
              selectText="Search User"
              displayKey="username"
              hideSubmitButton
              noItemsText="No user found"
              searchInputPlaceholderText="Search user"
              onSelectedItemsChange={users => {
                setSelectedUsers(users);
              }}
            />
          </Dialog.Content>
          <Dialog.Actions>
            <Button
              title="Create Group"
              color="#BBBDF6"
              onPress={e => handleStartChat(true)}>
              Start Chat
            </Button>
            <View style={{padding: 1}}></View>
            <TouchableOpacity
              title="Cancel"
              style={{width: 70, marginLeft: 20}}
              onPress={handleNewChatClose}>
              <Text style={{color: '#BBBDF6', fontSize: 18}}>Cancel</Text>
            </TouchableOpacity>
          </Dialog.Actions>
        </Dialog>
      </Portal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
    backgroundColor: '#BBBDF6',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 16,
    borderTopColor: '#9893DA',
    marginVertical: 10,
    borderTopWidth: 1,
  },
  chatItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
    backgroundColor: '#fff',
    borderRadius: 10,
  },
  chatText: {
    marginLeft: 0,
    padding: 10,
  },
});
export default ChatListScreen;
