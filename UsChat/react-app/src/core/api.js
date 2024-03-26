// api.js

import axios from 'axios';

const baseURL = 'http://10.0.2.2:8000';

const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const registerUser = async (username, password) => {
  try {
    return await api.post('/users/register', {
      username,
      password,
    });
  } catch (error) {
    console.error('Error during registration:', error);
    return null;
  }
};

export const loginUser = async (username, password) => {
  try {
    console.log(username, password);
    return api({
      method: 'GET',
      url: `/auth/login?username=${username}&password=${password}`,
    });
  } catch (error) {
    console.error('Error during login:', error);
    return null;
  }
};

export const getAllChats = async user_id => {
  try {
    return api({
      method: 'GET',
      url: `/chats/get-all-chats/${user_id}`,
    });
  } catch (error) {
    console.error('Error during login:', error);
    return null;
  }
};

export const getMessagesByRoomId = async chat_room_id => {
  try {
    return api({
      method: 'GET',
      url: `/chats/get-messages/${chat_room_id}`,
    });
  } catch (error) {
    console.error('Error during login:', error);
    return null;
  }
};

export const editMessage = async (message_id, new_text) => {
  try {
    return api({
      method: 'PUT',
      url: `/chats/edit-message/${message_id}`,
      data: {new_text}, // Include the updated message text in the request body
    });
  } catch (error) {
    console.error('Error during edit message:', error);
    return null;
  }
};

export const deleteMessage = async message_id => {
  try {
    console.log(message_id);
    return api.delete(`/chats/delete-message/${message_id}`);
  } catch (error) {
    console.error('Error during delete:', error);
    return null;
  }
};
