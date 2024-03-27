import React, { useEffect, useRef, useState } from 'react';
import { View, FlatList, StyleSheet, Text } from 'react-native';
import { Button } from 'react-native-paper';
import { useNavigation } from '@react-navigation/native';
import { baseUrl } from '../baseUrl';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

const UsersScreen = () => {
  const [users, setUsers] = useState([]);
  const navigation = useNavigation();
  const curUser = useRef(null)

  useEffect(() => {
    getUserIdFromStorage();
  }, []);
  const getUserIdFromStorage = async () => {
    try {
      var user = await AsyncStorage.getItem('userObject');
      user = JSON.parse(user)
      curUser.current = user
      fetchRegisteredUsers();
    } catch (error) {
      console.error('Error retrieving user_id from AsyncStorage:', error);
    }
  };
  const fetchRegisteredUsers =  () => {
    axios.get(`${baseUrl}/get-users/`).then(res=>{
      setUsers(res?.data?.length ? res.data.filter((user)=>user.id !== curUser?.current?.id) : []);
      console.log(curUser)
    }).catch((err) => {
        console.log(err);
      });
  };
 

  const navigateToPersonalChat = async (receiver_id) => {
    try {
      if (curUser?.current?.id) {
        navigation.navigate('PersonalChat', { user_id: curUser?.current?.id, receiver_id, isGroup: false,username: curUser?.current?.username});
      } else {
        console.warn('User_id not found in AsyncStorage');
      }
    } catch (error) {
      console.error('Error navigating to PersonalChat:', error);
    }
  };
  const handleLogout = async () => {
    try {
    await AsyncStorage.removeItem('userObject');
    navigation.navigate('Register');
    } catch (error) {
      console.error('Error logging out:', error);
    }
  };
  const navigateToGroupScreen = async () =>{
    try {
        navigation.navigate('Groups');
    }catch(error) {
      console.error('Error navigating to Group Chats:', error);
    }

  }

  return (
    <View style={styles.container}>
    <Text style={styles.header}>Registered Users</Text>
    <FlatList
      data={users}
      keyExtractor={(item) => item.username}
      renderItem={({ item }) => (
        <View style={styles.userItem}>
          <Text onPress={() => navigateToPersonalChat(item.id)}>{item.username}</Text>
        </View>
      )}
    />
    <Button onPress= {navigateToGroupScreen}>Group Chat</Button>
    <Button onPress={fetchRegisteredUsers}>Refresh Users</Button>
    <Button onPress={handleLogout}>Logout</Button>
  </View>
);
};



const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 16,
  },
  header: {
    fontSize: 20,
    fontWeight: 'bold',
    marginBottom: 16,
  },
  userItem: {
    fontSize: 20,
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
    paddingVertical: 20,
  },
});

export default UsersScreen;
