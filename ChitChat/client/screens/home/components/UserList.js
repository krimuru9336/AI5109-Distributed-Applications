import React from "react";
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import { Avatar } from "react-native-paper";

const UserList = ({ items, handleClick }) => {
  return (
    <FlatList
      data={items}
      renderItem={({ item }) => (
        <TouchableOpacity onPress={() => handleClick(item)}>
          <View style={styles.userContainer}>
            <Avatar.Text size={48} label={item.name[0].toUpperCase()} />
            <View style={styles.userInfo}>
              <Text style={styles.userName}>{item.name}</Text>
              <Text style={styles.userEmail}>{item.email}</Text>
            </View>
          </View>
        </TouchableOpacity>
      )}
    />
  );
};

const styles = StyleSheet.create({
  userContainer: {
    flexDirection: "row",
    alignItems: "center",
    padding: 16,
    borderBottomWidth: 1,
    borderBottomColor: "#ccc",
  },
  userInfo: {
    marginLeft: 16,
  },
  userName: {
    fontSize: 18,
    fontWeight: "bold",
  },
  userEmail: {
    fontSize: 16,
    color: "#555",
  },
});

export default UserList;
