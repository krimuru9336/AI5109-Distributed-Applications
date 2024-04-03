// ChatListScreen.js
import React, {useEffect, useState, useLayoutEffect, useRef} from 'react';
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
import {getAllChats, getUsers, startChat} from '../core/api';
import {SearchBar} from 'react-native-elements';
import {FontAwesomeIcon} from '@fortawesome/react-native-fontawesome';
import {FloatingAction} from 'react-native-floating-action';
import {List, Divider, Portal, Dialog} from 'react-native-paper';
import MultiSelect from 'react-native-multiple-select';
import Input from '../common/Input';

function useDidUpdate(callback, deps) {
  const hasMount = useRef(false);

  useEffect(() => {
    if (hasMount.current) {
      callback();
    } else {
      hasMount.current = true;
    }
  }, deps);
}

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
  const user = useGlobalStore(state => state.user);

  useEffect(() => {
    // Fetch chat data from the API
    fetchChats();
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

  const actions = [
    {
      text: 'Start Group Chat',
      icon: <FontAwesomeIcon color="#FFF9E0" icon="user-group" />,
      name: 'start_gc',
      position: 1,
      color: 'orange',
    },
    {
      text: 'Start Chat',
      icon: <FontAwesomeIcon color="#FFF9E0" icon="message" />,
      name: 'start_chat',
      position: 2,
      color: 'orange',
    },
  ];
  useLayoutEffect(() => {
    // console.log(navigation);
  });

  return (
    <View style={styles.container}>
      <View
        style={{
          padding: 0,
          margin: 0,
          display: 'flex',
          flexDirection: 'row',
          alignContent: 'center',
          justifyContent: 'space-between',
        }}>
        <Text style={styles.header}>Chats</Text>
        <TouchableOpacity onPress={async () => await fetchChats()}>
          <FontAwesomeIcon size={20} icon="refresh" />
        </TouchableOpacity>
      </View>
      <SearchBar
        placeholder="Type Here..."
        onChangeText={updateSearch}
        value={searchTerm}
        containerStyle={{
          backgroundColor: 'transparent',
          borderTopColor: 'white',
          borderBottomColor: 'white',
        }}
        inputContainerStyle={{backgroundColor: '#FFF9E0'}}
        searchIcon={<FontAwesomeIcon icon="magnifying-glass" color="orange" />}
        clearIcon={<FontAwesomeIcon icon="xmark" color="orange" />}
        onClear={() => updateSearch('')}
      />
      <FlatList
        style={{paddingTop: 15}}
        data={searchedChats}
        keyExtractor={item =>
          item?.message_id?.toString() || item.username + Date.now()
        }
        renderItem={({item}) => (
          <TouchableOpacity onPress={() => navigateToChat(item)}>
            <View style={styles.chatItem}>
              {/* <Avatar rounded title="M" /> */}
              <View style={styles.chatText}>
                <Text style={{fontSize: 18, fontWeight: '600', color: 'black'}}>
                  {item.type == 1 ? item.username : item?.chat_room_name}
                </Text>
                {item?.message_type == 'text' ? (
                  <Text style={{color: 'black', width: 300}}>
                    {item.message_text}
                  </Text>
                ) : (
                  <Text style={{color: 'black', width: 200}}>
                    {item.message_type}
                  </Text>
                )}
                {/* Add more details as needed */}
              </View>
            </View>
          </TouchableOpacity>
        )}
      />
      <FloatingAction
        color="orange"
        tintColor="orange"
        actions={actions}
        onPressItem={name => {
          if (name == 'start_chat') {
            setOpenChatDialog(true);
          } else {
            setOpenGroupChatDialog(true);
          }
        }}
      />
      {/* Single Chat Dialog */}
      <Portal>
        <Dialog
          style={{backgroundColor: '#FFF9E0'}}
          visible={openChatDialog}
          onDismiss={handleNewChatClose}>
          <Dialog.Title style={{color: 'orange'}}>Search User</Dialog.Title>
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
              color="orange"
              onPress={e => handleStartChat(false)}>
              Start Chat
            </Button>
            <View style={{padding: 1}}></View>
            <Button
              title="Cancel"
              color="orange"
              style={{backgroundColor: 'orange'}}
              onPress={handleNewChatClose}>
              Cancel
            </Button>
          </Dialog.Actions>
        </Dialog>
      </Portal>

      <Portal>
        <Dialog
          style={{backgroundColor: '#FFF9E0'}}
          visible={openGroupChatDialog}
          onDismiss={handleNewChatClose}>
          <Dialog.Title style={{color: 'orange'}}>Search Users</Dialog.Title>
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
              color="orange"
              onPress={e => handleStartChat(true)}>
              Start Chat
            </Button>
            <View style={{padding: 1}}></View>
            <Button
              title="Cancel"
              color="orange"
              style={{backgroundColor: 'orange', width: 20}}
              onPress={handleNewChatClose}>
              Cancel
            </Button>
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
    backgroundColor: '#fff',
  },
  header: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
    color: 'black',
  },
  chatItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
    borderRadius: 10,
    borderWidth: 1,
    borderBottomWidth: 1,
    borderColor: '#aaa',
    backgroundColor: '#FFF9E0',
  },
  chatText: {
    width: 200,
    marginLeft: 5,
    padding: 10,
  },
});
export default ChatListScreen;
