import React, { useContext, useEffect, useState } from 'react'
import axios from 'axios'
import { API_URL } from "@env"
import AuthContext from '../context/AuthContext'
import { VirtualizedList, Box, HStack, VStack, Text } from '@gluestack-ui/themed';
import { TouchableOpacity } from 'react-native';

export default function ChatListScreen({ navigation }) {
    const { accessToken } = useContext(AuthContext);
    const [chats, setChats] = useState([]);

    useEffect(() => {
        (async () => {
            const res = await axios.get(`${API_URL}/chats`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`
                }
            });
            setChats(res.data.chats)
        })()
    }, [])

    function getItemCount(_data) {
        return chats.length
    }
    function getItem(_data, index) {
        const a = chats[index]
        return a;
    }

    return (
        <Box>
            {
                (chats.length > 0) ?
                    <VirtualizedList
                        getItemCount={getItemCount}
                        getItem={getItem}
                        renderItem={({ item }) => (
                            <TouchableOpacity onPress={() => {
                                navigation.navigate('ChatScreen', {
                                    receipientId: item.id
                                })
                            }}>
                                <Box
                                    borderBottomWidth="$1"
                                    borderColor="$trueGray800"
                                    sx={{
                                        _dark: {
                                            borderColor: "$trueGray100",
                                        },
                                        "@base": {
                                            pl: 0,
                                            pr: 0,
                                        },
                                        "@sm": {
                                            pl: "$4",
                                            pr: "$5",
                                        },
                                    }}
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
    )
}
