import React, { useCallback, useEffect, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { GiftedChat } from 'react-native-gifted-chat';
import { baseUrl, socketUrl } from '../baseUrl';
import axios from 'axios';

const PersonalChat = ({ route }) => {
  const { user_id, receiver_id } = route.params;

  const [messages, setMessages] = useState([]);
  const [socket, setSocket] = useState(null);


  useEffect(() => {
    const newSocket = new WebSocket(`ws://${socketUrl}/ws/${user_id}`);
    setSocket(newSocket);

    // return () => {
    //   newSocket.disconnect();
    // };
  }, [user_id]);

  useEffect(() => {
    if(socket){
      socket.addEventListener('message',(event)=>{
        console.log(event.data, 'HERE')
        setMessages(previousMessages =>
          GiftedChat.append(previousMessages, [JSON.parse(event.data)]),
        )
      })
    }
  }, [socket]);
  useEffect(()=>{
    if(user_id && receiver_id){
      console.log("by")
      axios.get(`${baseUrl}/get-chat-history/${user_id}/${receiver_id}/`)
      .then(res=>{
        console.log(res.data)
        if(res.data?.length){
          setMessages(res.data.map(message => JSON.parse(message.text)))
        }
      }).catch(err=>{
        console.log(err)
      })
    }

  },[user_id,receiver_id])
  const onSend = (newMessage) => {
    if (newMessage?.[0]?.text?.trim() !== '' && socket) {
      try {
        socket.send(JSON.stringify({
          ...newMessage[0],
          receiver_id
        }));  
      }catch(error){
        console.log(error)
      }  
      setMessages(previousMessages =>
        GiftedChat.append(previousMessages, newMessage),
      )     
    }
  }

  return (
    <View style={{ flex: 1, backgroundColor:'white', paddingBottom:50 }}>
      <GiftedChat
        messages={messages}
        onSend={(newMessage) => {
          onSend(newMessage)
        }}
        user={{ _id: user_id.toString() }}
        
      />
    </View>
  );
};

const styles = StyleSheet.create({
  inputContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 10,
    borderTopWidth: 1,
    borderTopColor: '#ccc',
  },
  textInput: {
    flex: 1,
    marginRight: 10,
    padding: 8,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 5,
  },
});

export default PersonalChat;
