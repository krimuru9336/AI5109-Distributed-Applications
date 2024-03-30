// api.js

import axios from 'axios';

export const baseURL =
  'https://18cb-2405-201-1004-1aeb-9869-b419-899e-4b66.ngrok-free.app';

const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const registerUser = async (username, password) => {
  try {
    return api.post('/users/register', {
      username,
      password,
    });
  } catch (error) {
    console.error('Error during registration:', error);
    return error;
  }
};

export const loginUser = async (username, password) => {
  try {
    return api({
      method: 'POST',
      url: `/auth/login`,
      data: {username, password},
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

// Function to send an image file to the server
export const uploadMedia = async imageFile => {
  try {
    const formData = new FormData();
    formData.append('file', {
      uri: imageFile.uri,
      type: imageFile.type,
      name: imageFile.fileName,
    });
    formData.append('name', imageFile.fileName);
    console.log(formData);
    return axios.post(`${baseURL}/chats/upload_media`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  } catch (error) {
    console.error('Error sending message with image:', error);
    return null;
  }
};

// Function to send an image file to the server
export const sendMessageWithMedia = async (
  chat_room_id,
  sender_id,
  filename,
  message_type,
) => {
  try {
    console.log(
      typeof chat_room_id,
      typeof sender_id,
      typeof filename,
      typeof message_type,
    );
    return api({
      method: 'POST',
      url: `/chats/insert-media-message`,
      data: {chat_room_id, sender_id, filename, message_type}, // Include the updated message text in the request body
    });
  } catch (error) {
    console.error('Error sending message with image:', error);
    return null;
  }
};

export const getUsers = async user_id => {
  try {
    return api({
      method: 'GET',
      url: `/users/get-all/${user_id}`,
    });
  } catch (error) {
    console.error('Error during login:', error);
    return null;
  }
};

export const startChat = async (name, is_group, user_ids) => {
  try {
    let data = {
      name,
      is_group,
      user_ids,
    };

    return api({
      method: 'POST',
      url: `/chats/start-chat`,
      data: data,
    });
  } catch (error) {
    console.error('Error during login:', error);
    return null;
  }
};
