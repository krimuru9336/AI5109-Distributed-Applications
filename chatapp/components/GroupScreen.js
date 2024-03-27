import React, { useEffect, useRef, useState } from 'react';
import { View, Modal, FlatList, StyleSheet, Text, TextInput } from 'react-native';
import { Button } from 'react-native-paper';
import { useNavigation } from '@react-navigation/native';
import { baseUrl } from '../baseUrl';
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import MultiSelect from 'react-native-multiple-select';



const GroupScreen = () => {
    const [groups, setGroups] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [users, setUsers] = useState([]); // For storing existing users
    const [groupName, setGroupName] = useState('');
    const [modalVisible, setModalVisible] = useState(false);
    const curUser = useRef(null);
    const navigation = useNavigation();


    useEffect(() => {
        getUserIdFromStorage();

    }, []);

    const fetchGroups = () => {
        axios.get(`${baseUrl}/get-groups/`).then(res => {
            setGroups(res.data.filter((group) => group.user_ids.split(',').includes(String(curUser.current.id))));
            console.log(res.data, 'groups')
        }).catch((err) => {
            console.log(err);
        });
    };
    const fetchUsers = async () => {
        axios.get(`${baseUrl}/get-users/`).then(res => {
            setUsers(res?.data?.length ? res.data.filter((user) => user.id !== curUser?.current?.id) : []);
            console.log(curUser)

        }).catch((err) => {
            console.log(err);
        });
    };
    const getUserIdFromStorage = async () => {
        try {
            var user = await AsyncStorage.getItem('userObject');
            user = JSON.parse(user)
            curUser.current = user
            fetchUsers();
            fetchGroups();

        } catch (error) {
            console.error('Error retrieving user_id from AsyncStorage:', error);
        }
    };

    const handleCreateGroups = () => {
        if (groupName && selectedUsers.length) {
            axios.post(`${baseUrl}/create-group/`, {
                name: groupName.toString(),
                user_ids: [...selectedUsers, curUser.current.id].join(',').toString()

            }).then(res => {
                console.log("here", res)
                fetchGroups()
            }).catch((err) => {
                console.log(err);
            });
            // Handle group creation here using groupName and selectedUsers
            console.log('Group Name:', groupName.toString());
            console.log('Selected Users:', selectedUsers.join(','));
            // Close the modal
            handleModalClose()
        } else {
            console.log('DATA MISSING')
        }

    };

    const handleModalClose = () => {
        setModalVisible(false)
        setGroupName('')
        setSelectedUsers([])
    }

    const navigateToPersonalChat = async (item) => {
        try {

            if (curUser?.current?.id) {
                navigation.navigate('PersonalChat', { user_id: curUser?.current?.id, receiver_id: item.user_ids.split(','), isGroup: true, group_id: item.id, username: curUser?.current?.username });
            } else {
                console.warn('User_id not found in AsyncStorage');
            }
        } catch (error) {
            console.error('Error navigating to PersonalChat:', error);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.header}>Groups</Text>
            <FlatList
                data={groups}
                keyExtractor={(item) => item.name}
                renderItem={({ item }) => (
                    <View style={styles.groupItem}>
                        <Text onPress={() => navigateToPersonalChat(item)}>{item.name}</Text>
                    </View>
                )}
            />
            <Button onPress={() => setModalVisible(true)}>Create Group</Button>
            <Modal
                animationType="slide"
                transparent={true}
                visible={modalVisible}
                onRequestClose={() => {
                    handleModalClose()
                }}
            >
                <View style={styles.modalContainer}>
                    <View style={styles.modalContent}>
                        <Text style={styles.modalHeader}>Create Group</Text>
                        <TextInput
                            required
                            label="Group Name"
                            placeholder="Group Name"
                            placeholderTextColor="#333333"
                            style={styles.input}
                            value={groupName}
                            onChangeText={text => setGroupName(text)}
                        />

                        <View>
                            <MultiSelect
                                items={users.map(user => ({ name: user.username, value: user.id, id: user.id.toString() }))}
                                uniqueKey="id"
                                style={pickerSelectStyles.inputIOS}
                                onSelectedItemsChange={(value) => setSelectedUsers(value)}
                                selectedItems={selectedUsers}
                                hideSubmitButton
                                tagBorderColor='#7733ff'
                                tagRemoveIconColor='#7733ff'
                                tagTextColor='#333333'
                                textColor='#333333'
                                selectText="   Select Group Members"
                                //selectTextColor='#333333'

                                selectedItemTextColor='#7733ff'
                                selectedItemIconColor='#7733ff'
                            />

                        </View>

                        <View style={styles.buttonContainer}> 
                            <Button onPress={() => { handleModalClose() }}>Cancel</Button>
                            <Button onPress={handleCreateGroups}>Create</Button>
                        </View>


                    </View>
                </View>
            </Modal>
        </View>
    )
}

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
    modalContainer: {
        flex: 1,
        justifyContent: 'center',
        margin: 20
    },
    modalContent: {
        backgroundColor: '#fff',
        padding: 20,
        borderRadius: 10,
        elevation: 5,
    },
    modalHeader: {
        fontSize: 20,
        fontWeight: 'bold',
        marginBottom: 10,
    },
    input: {
        borderWidth: 1,
        borderColor: '#ccc', borderRadius: 5,
        padding: 10,
        marginBottom: 20,
    }, groupItem: {
        fontSize: 20,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
        paddingVertical: 20,
    },
    buttonContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        marginTop: 10,
    }
});

const pickerSelectStyles = StyleSheet.create({
    inputIOS: {


        fontSize: 16,
        paddingHorizontal: 10,
        paddingVertical: 8,
        borderWidth: 1,
        borderColor: '#ccc',
        borderRadius: 5,
        marginBottom: 10, // to ensure the text is never behind the icon
    },
    inputAndroid: {
        fontSize: 16,
        paddingHorizontal: 10,
        paddingVertical: 8,
        borderWidth: 0.5,
        borderColor: 'purple',
        borderRadius: 8,
        color: 'black',
        height: 50,
        width: 50,
        paddingRight: 10, // to ensure the text is never behind the icon
    },
});
export default GroupScreen;
