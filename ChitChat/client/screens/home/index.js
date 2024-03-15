import React, { useEffect, useState, useRef } from "react";
import { View, Text, StyleSheet, Modal, Button, TextInput } from "react-native";
import axios from "axios";
import UserList from "./components/UserList";
import { baseUrl } from "../../baseUrl";
import AppToast from "../../components/AppToast";
import { getUserFromLocalStorage } from "../../helper";
import { useSocket } from "../../SocketContext";
import AppButton from "../../components/AppButton";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { AntDesign } from "@expo/vector-icons";
import MultiSelect from "react-native-multiple-select";

const HomeScreen = ({ navigation }) => {
  const [users, setUsers] = useState([]);
  const [groups, setGroups] = useState([]);
  const [error, setError] = useState("");
  const [newGroupModal, setNewGroupModal] = useState(false);
  const userRef = useRef("");
  const { socket, connectSocket } = useSocket();
  const [selectedUsers, setSelectedUsers] = useState([]);

  const groupName = useRef("");

  const setSocketConnection = async () => {
    const user = await getUserFromLocalStorage();
    if (user && JSON.parse(user)?.id) {
      userRef.current = JSON.parse(user);
      const userId = userRef.current.id;
      if (!socket) {
        connectSocket(userId);
      }
    }
  };

  useEffect(() => {
    setSocketConnection();
    getUsers();
    setTimeout(() => {
      getGroups();
    }, 300);
  }, []);

  const getUsers = () => {
    axios
      .get(`${baseUrl}user/`)
      .then((res) => {
        if (res.data) {
          setUsers(res.data.filter((user) => user.id != userRef.current.id));
        }
      })
      .catch((error) => {
        console.log(JSON.stringify(error));
        setError(error?.response?.data?.detail || "Server Error");
      });
  };

  const getGroups = () => {
    axios
      .get(`${baseUrl}group/`)
      .then((res) => {
        if (res.data) {
          setGroups(
            res.data.filter((group) =>
              group.members.split(",").includes(userRef.current.id.toString())
            )
          );
        }
      })
      .catch((error) => {
        console.log(JSON.stringify(error));
        setError(error?.response?.data?.detail || "Server Error");
      });
  };

  const handleClick = (reciever) => {
    if (socket) {
      navigation.navigate("Chat", {
        user: userRef.current,
        reciever: reciever,
        isGroup: reciever?.members?.length ? 1 : 0,
        members: reciever?.members?.length ? reciever?.members : "",
      });
    }
    setSocketConnection();
  };

  const logout = async () => {
    await AsyncStorage.removeItem("user");
    navigation.reset({
      index: 0,
      routes: [{ name: "Sign Up" }],
    });
  };

  const handleNewGroup = () => {
    if (selectedUsers?.length && groupName.current) {
      axios
        .post(`${baseUrl}group/`, {
          name: groupName.current,
          members: [...selectedUsers, userRef.current.id].join(","),
        })
        .then((res) => {
          setSelectedUsers([]);
          groupName.current = "";
          setNewGroupModal(false);
          getGroups();
        })
        .catch((error) => {
          console.log(error);
          setError(error?.response?.data?.detail || "Server Error");
        });
    } else {
      setError("Name & Users are Required");
    }
  };

  return (
    <View style={styles.container}>
      <AntDesign
        name="logout"
        size={24}
        color="red"
        style={styles.logoutButton}
        onPress={logout}
      />
      <AppButton
        onPress={() => {
          getUsers();
          setTimeout(() => {
            getGroups();
          }, 300);
        }}
        style={styles.refreshButton}
        text="Refresh"
      />
      <AppButton
        onPress={() => {
          setNewGroupModal(true);
        }}
        style={styles.groupButton}
        text="New Group"
      />
      <Text style={styles.header}>Chats</Text>
      <UserList
        user={userRef.current}
        items={[...users, ...groups]}
        handleClick={handleClick}
      />

      <Modal visible={newGroupModal} transparent={true} animationType="slide">
        <View style={styles.modalContainer}>
          <View style={styles.contextMenu}>
            <TextInput
              style={styles.editInput}
              onChangeText={(e) => {
                groupName.current = e;
              }}
              placeholder="Group Name"
            />
            <MultiSelect
              items={users}
              uniqueKey="id"
              onSelectedItemsChange={(selectedItems) =>
                setSelectedUsers(selectedItems)
              }
              selectedItems={selectedUsers}
              selectText="Select Users"
              searchInputPlaceholderText="Search Users..."
              searchInputStyle={{ color: "#CCC" }}
              hideSubmitButton
            />
            <AppButton text="Save" onPress={handleNewGroup} />
            <Button
              title="Cancel"
              onPress={() => {
                setNewGroupModal(false);
                setSelectedUsers([]);
                groupName.current = "";
              }}
            />
          </View>
        </View>
      </Modal>

      {error && <AppToast setError={setError} visible={true} text={error} />}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
  },
  header: {
    fontSize: 24,
    marginBottom: 20,
  },
  logoutButton: {
    position: "absolute",
    top: 20,
    right: 20,
    padding: 10,
    borderColor: "red",
    borderWidth: 1,
    borderRadius: 5,
  },
  refreshButton: {
    position: "absolute",
    top: 20,
    right: 80,
    padding: 10,
    backgroundColor: "#91C45A",
    borderColor: "#91C45A",
    borderWidth: 1,
    borderRadius: 5,
  },
  groupButton: {
    position: "absolute",
    top: 20,
    right: 180,
    padding: 10,
    backgroundColor: "#91C45A",
    borderColor: "#91C45A",
    borderWidth: 1,
    borderRadius: 5,
  },
  modalContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },
  contextMenu: {
    width: "80%",
    backgroundColor: "white",
    borderRadius: 10,
    padding: 20,
    elevation: 5,
  },
  editInput: {
    borderWidth: 1,
    marginBottom: 10,
    padding: 10,
    borderRadius: 5,
  },
});

export default HomeScreen;
