import * as SQLite from "expo-sqlite";
import { StatusBar } from "expo-status-bar";
import {
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  ScrollView,
} from "react-native";
import React, { useEffect, useState } from "react";
import {
  useTheme,
  Appbar,
  Banner,
  TextInput,
  Button,
  Snackbar,
} from "react-native-paper";

const db = SQLite.openDatabase("db.user"); // returns Database object

export default function Greeting() {
  const theme = useTheme();
  const styles = StyleSheet.create({
    container: {
      flex: 1,
      height: 50,
      backgroundColor: theme.colors.primary,
      alignItems: "center",
      justifyContent: "start",
    },
    row: {
      flexDirection: "row",
    },
  });

  const [data, setData] = useState([]);
  const [name, setName] = React.useState("");

  const [visible, setVisible] = React.useState(false);

  const onToggleSnackBar = () => setVisible(!visible);

  const onDismissSnackBar = () => setVisible(false);

  fetchData = () => {
    db.transaction((tx) => {
      tx.executeSql(
        "SELECT * FROM items ORDER BY id DESC LIMIT 1;",
        null,
        (txObj, { rows: { _array } }) => setData(_array),
        (txObj, error) => console.log("Error ", error)
      );
    });
    onToggleSnackBar();
  };
  newItem = () => {
    db.transaction((tx) => {
      tx.executeSql(
        "INSERT INTO items (name) values (?)",
        [name],
        (txObj, resultSet) => {
          setData(
            data.concat({
              id: resultSet.insertId,
              text: name,
            })
          ),
            setName(null);
        },
        (txObj, error) => console.log("Error", error)
      );
    });
  };

  increment = (id) => {
    db.transaction((tx) => {
      tx.executeSql(
        "UPDATE items SET count = count + 1 WHERE id = ?",
        [id],
        (txObj, resultSet) => {
          if (resultSet.rowsAffected > 0) {
            let newList = data.map((data) => {
              if (data.id === id) return { ...data, count: data.count + 1 };
              else return data;
            });
            setData(newList);
          }
        }
      );
    });
  };
  useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        "CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)"
      );
    });
  }, []);

  return (
    <>
      <Appbar.Header
        style={{
          backgroundColor: theme.colors.primary,
        }}
      >
        <Appbar.Content title="UsChat" />
      </Appbar.Header>
      <Banner
        style={{
          backgroundColor: theme.colors.secondary,
        }}
        visible={true}
      >
        <Text style={{ fontSize: 18 }}>Gagana Venkatesh - 1492695</Text>
      </Banner>

      <View style={styles.container}>
        <TextInput
          label="Name"
          value={name}
          mode="flat"
          placeholder="Enter your name"
          outlineColor={"black"}
          activeOutlineColor={"black"}
          style={{ width: "80%", margin: 10 }}
          onChangeText={(name) => setName(name)}
        />
        {/* <TouchableOpacity onPress={newItem}>
          <Text>Add New Item {data.length}</Text>
        </TouchableOpacity> */}
        <View style={styles.row}>
          <Button
            style={{ margin: 10, width: 100 }}
            buttonColor={theme.colors.secondary}
            textColor="black"
            onPress={newItem}
          >
            {"Save"}
          </Button>

          <Button
            style={{ margin: 10, width: 100 }}
            buttonColor={theme.colors.secondary}
            textColor="black"
            onPress={fetchData}
          >
            {"Show Toast"}
          </Button>
        </View>

        <Snackbar
          visible={visible}
          onDismiss={onDismissSnackBar}
          action={{
            label: "Close",
            textColor: theme.colors.primary,
            onPress: () => {
              onDismissSnackBar();
            },
          }}
        >
          <Text style={{ color: theme.colors.primary }}>
            {" "}
            Entered Name is : {data && data.length ? data[0].name : null}
          </Text>
        </Snackbar>
      </View>
    </>
  );
}
