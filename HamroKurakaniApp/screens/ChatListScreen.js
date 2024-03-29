import React, { useContext, useEffect, useState } from 'react'
import axios from 'axios'
import { API_URL } from "@env"
import AuthContext from '../context/AuthContext'
import { VirtualizedList, Box, HStack, VStack, Text, Button, ButtonText, Heading } from '@gluestack-ui/themed';
import { TouchableOpacity } from 'react-native';
import CreateGroupModal from '../components/CreateGroupModal';

export default function ChatListScreen({ navigation }) {
    const { accessToken } = useContext(AuthContext);
    const [userChats, setUserChats] = useState([]);
    const [groupChats, setGroupChats] = useState([]);
    const [showCreateGroupModal, setShowCreateGroupModal] = useState(false)
    const [shouldLoadChats, setShouldLoadChats] = useState(true);

    useEffect(() => {
        (async () => {
            try {
                const res = await axios.get(`${API_URL}/chats`, {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    }
                });
                console.log(res.data)
                setUserChats(res.data.chats)
                setGroupChats(res.data.groups)
            } catch (err) {
                console.log(err)
            }
        })()
        setShouldLoadChats(false);
    }, [shouldLoadChats])

    function getUserChatItemCount(_data) {
        return userChats.length
    }
    function getUserChatItem(_data, index) {
        const a = userChats[index]
        return a;
    }

    function getGroupChatItemCount(_data) {
        return groupChats.length
    }
    function getGroupChatItem(_data, index) {
        const a = groupChats[index]
        return a;
    }

    return (
        <Box>
            <Box display='flex' alignItems='flex-end' paddingTop={10} paddingRight={10}>
                <Button
                    size="xs"
                    variant="solid"
                    action="primary"
                    width={100}
                    onPress={() => { setShowCreateGroupModal(true) }}
                >
                    <ButtonText>
                        Create Group
                    </ButtonText>
                </Button>
            </Box>
            <Box paddingVertical={10}>
                <Box>
                    <Heading color='#5F5F5F' textAlign='center' marginBottom={5}>Users</Heading>
                    {
                        (userChats.length > 0) ?
                            <VirtualizedList
                                getItemCount={getUserChatItemCount}
                                getItem={getUserChatItem}
                                renderItem={({ item }) => (
                                    <TouchableOpacity onPress={() => {
                                        navigation.navigate('ChatScreen', {
                                            receipientId: item.id,
                                            recipientType: "user"
                                        })
                                    }}>
                                        <Box
                                            borderWidth="$1"
                                            borderColor="$trueGray800"
                                            py="$2"
                                        >
                                            <HStack space="md" justifyContent="space-between">
                                                <VStack>
                                                    <Text
                                                        color="$coolGray800"
                                                        fontWeight="$bold"
                                                        sx={{
                                                            _dark: {
                                                                color: "$warmGray100",
                                                            },
                                                        }}
                                                    >
                                                        {item.username}
                                                    </Text>
                                                </VStack>
                                            </HStack>
                                        </Box>
                                    </TouchableOpacity>
                                )}
                            /> : <></>
                    }
                </Box>

                <Box>
                    <Heading color='#5F5F5F' textAlign='center' marginBottom={5}>Groups</Heading>
                    {
                        (groupChats.length > 0) ?
                            <VirtualizedList
                                getItemCount={getGroupChatItemCount}
                                getItem={getGroupChatItem}
                                renderItem={({ item }) => (
                                    <TouchableOpacity onPress={() => {
                                        navigation.navigate('ChatScreen', {
                                            receipientId: item.id,
                                            recipientType: "group"
                                        })
                                    }}>
                                        <Box
                                            borderWidth="$1"
                                            borderColor="$trueGray800"
                                            py="$2"
                                        >
                                            <HStack space="md" justifyContent="space-between">
                                                <VStack>
                                                    <Text
                                                        color="$coolGray800"
                                                        fontWeight="$bold"
                                                        sx={{
                                                            _dark: {
                                                                color: "$warmGray100",
                                                            },
                                                        }}
                                                    >
                                                        {item.name}
                                                    </Text>
                                                </VStack>
                                            </HStack>
                                        </Box>
                                    </TouchableOpacity>
                                )}
                            /> : <></>
                    }
                </Box>
            </Box>

            <CreateGroupModal showModal={showCreateGroupModal} setShowModal={setShowCreateGroupModal} successCallback={() => setShouldLoadChats(true)} />
        </Box>
    )
}
