import React, { useState, useEffect } from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, View, TextInput, Text, Button ,Pressable} from 'react-native';
import * as SQLite from 'expo-sqlite';
import Toast from 'react-native-root-toast';
import { RootSiblingParent } from 'react-native-root-siblings';

const db = SQLite.openDatabase('mydatabase.db');

export default function App() {
  const [text, setText] = useState('');

  useEffect(() => {
    db.transaction((tx) => {
      tx.executeSql(
        'CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);'
      );
    });
  }, []);

  const handleInsert = () => {
    db.transaction(
      (tx) => {
        tx.executeSql('INSERT INTO items (name) VALUES (?);', [text]);
      },
      null,
      () => {
        setText('');
      }
    );
  };

  const handleRetrieve = () => {
    db.transaction((tx) => {
      tx.executeSql(
        'SELECT * FROM items order by id desc limit 1;',
        [],
        (_, { rows }) => {
          Toast.show(`Hello, ${rows._array[0].name}`, {
            duration: Toast.durations.SHORT,
          });
        }
      );
    });
  };

  return (
    <RootSiblingParent>
      <View style={styles.container}>
        <View style={styles.infoContainer}>
          <Text>Name: Hauva Vali</Text>
          <Text>Date: 13/12/23</Text>
          <Text>FDAI Number: fdai8007</Text>
        </View>

        <Text style={styles.label}>Enter your name:</Text>
        <View style={styles.inputContainer}>
          <TextInput
            style={styles.input}
            placeholder="Enter your first name here"
            value={text}
            onChangeText={setText}
          />
        </View>

        <View style={styles.buttonContainer}>
          <Pressable style={styles.btn} onPress={handleInsert}>
            <Text style={styles.buttonText}>Insert</Text>
          </Pressable>
          <Pressable style={styles.btn} onPress={handleRetrieve}>
            <Text  style={styles.buttonText} >Retreive</Text>
          </Pressable>
    
        </View>

        <StatusBar style="auto" />
      </View>
    </RootSiblingParent>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
    padding: 20,
  },
  infoContainer: {
    paddingTop:50,
    alignItems: 'center',
    marginBottom: 170,
  },
  label: {
    marginBottom: 5,
    fontSize: 18,
    fontWeight: 'bold',
  },
  inputContainer: {
    marginVertical: 10,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 2,
    elevation: 2,
    borderColor: '#4CAF50', // Green color
    borderWidth: 1,
    borderRadius: 5,
  },
  input: {
    height: 40,
    paddingLeft: 10,
    fontSize: 16,
  },
  buttonContainer: {
    flexDirection: 'row',
    gap:120,
    marginTop: 20,
    alignItems: 'center',
    
    
  },
  btn:{
    alignItems: 'center',
    paddingVertical: 12,
    paddingHorizontal: 32,
    borderRadius: 5,
    elevation: 7,
    backgroundColor: 'green'
  },
  buttonText:{
    fontSize: 16,
    lineHeight: 21,
    fontWeight: 'bold',
    letterSpacing: 0.25,
    color: 'white',

  }
});
