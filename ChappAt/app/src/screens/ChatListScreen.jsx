// ChatListScreen.js
import React, {useEffect, useState, useLayoutEffect} from 'react';
import {View, Text, FlatList, StyleSheet, TouchableOpacity} from 'react-native';
import {Avatar} from 'react-native-elements';
import useGlobalStore from '../core/global';
import {getAllChats} from '../core/api';

const ChatListScreen = ({navigation}) => {
  const [chats, setChats] = useState([]);
  const user = useGlobalStore(state => state.user);

  useEffect(() => {
    // Fetch chat data from the API
    const fetchChats = async () => {
      try {
        const response = await getAllChats(user.user_id);
        const data = await response.data;
        setChats(data);
      } catch (error) {
        console.error('Error fetching chats:', error);
      }
    };

    fetchChats();
  }, []);

  const navigateToChat = chat => {
    console.log(navigation);
    // Navigate to 'Chat' screen, pass chat details as route params
    navigation.navigate('Chat', {chat});
  };
  useLayoutEffect(() => {
    console.log(navigation);
  });

  return (
    <View style={styles.container}>
      <Text style={styles.header}>Chat List</Text>
      <FlatList
        data={chats}
        keyExtractor={item => item.message_id.toString()}
        renderItem={({item}) => (
          <TouchableOpacity onPress={() => navigateToChat(item)}>
            <View style={styles.chatItem}>
              {/* <Avatar rounded title="M" /> */}
              <View style={styles.chatText}>
                <Text style={{fontSize: 18, fontWeight: '600'}}>
                  {item.type == 1 ? item?.username : item?.chat_room_name}
                </Text>
                {item.message_type == 'text' ? (
                  <Text>{item.message_text}</Text>
                ) : (
                  <Text>{item.message_type}</Text>
                )}
                {/* Add more details as needed */}
              </View>
            </View>
          </TouchableOpacity>
        )}
      />
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
    marginBottom: 16,
  },
  chatItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
    borderBottomWidth: 1,
    borderBottomColor: '#ccc',
  },
  chatText: {
    marginLeft: 5,
    padding: 10,
  },
});
export default ChatListScreen;
