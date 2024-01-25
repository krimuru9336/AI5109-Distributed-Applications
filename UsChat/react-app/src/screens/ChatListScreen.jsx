// ChatListScreen.js
import React, {useEffect, useState, useLayoutEffect} from 'react';
import {View, Text, FlatList, StyleSheet, TouchableOpacity} from 'react-native';
import {Avatar} from 'react-native-elements';
import useGlobalStore from '../core/global';
import {getAllChats} from '../core/api';
import Title from '../common/Title';

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
    navigation.setOptions({
      headerShown: false,
    });
  }, []);

  return (
    <View style={styles.container}>
      <Title text={'UsChat'} fontSize={26} />
      <Text style={styles.header}>Chats</Text>
      <FlatList
        data={chats}
        keyExtractor={item => item.message_id.toString()}
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
