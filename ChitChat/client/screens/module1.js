import React from 'react';
import { StatusBar } from 'expo-status-bar';
import { StyleSheet, Text, View, Pressable } from 'react-native';
import { TextInput } from 'react-native';
import { RootSiblingParent } from 'react-native-root-siblings';
import Toast from 'react-native-root-toast';
import * as SQLite from 'expo-sqlite';
import AppButton from '../components/AppButton';
import AppTextInput from '../components/AppTextInput';

const db = SQLite.openDatabase('mydb.db');

db.transaction(tx => {
    tx.executeSql(
        'CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, value TEXT);',
        [],
        () => console.log('Table created successfully'),
        error => console.error('Error creating table: ', error)
    );
});

export default Module1 = () => {
    const [text, setText] = React.useState('');
    const [visible, setVisible] = React.useState(false);
    const [storeText, setStoredText] = React.useState('');

    const handleSubmit = () => {
        if (text) {
            db.transaction(tx => {
                tx.executeSql(
                    'INSERT INTO items (value) VALUES (?)',
                    [text],
                    (_, results) => {
                        console.log('Item added successfully');
                        setText('');
                    },
                    error => console.error('Error adding item: ', error)
                );
            });
        } else {
            setStoredText('Please type Something ;)');
            setVisible(true);
        }
    };

    const handleDisplay = () => {
        db.transaction(tx => {
            tx.executeSql(
                'SELECT * FROM items ORDER BY id DESC LIMIT 1',
                [],
                (_, results) => {
                    if (results.rows?.length > 0) {
                        const lastItem = results.rows.item(0);
                        setStoredText(lastItem?.value);
                        console.log(lastItem.value);
                        setVisible(true);
                    }
                },
                error => console.error('Error retrieving item: ', error)
            );
        });
    };

    setTimeout(function hideToast() {
        setVisible(false);
    }, 800);

    return (
        <RootSiblingParent>
            <View style={styles.container}>
                <Text style={styles.names}>
                    Dipesh Kewalramani
                </Text>
                <Text style={styles.names}>
                    fdai8004
                </Text>
                <Text style={styles.date}>{new Date().toLocaleDateString()}</Text>
                <AppTextInput
                    onChangeText={setText}
                    value={text}
                    placeholder="Type Anything...."
                />
                <View style={styles.buttonContainer}>
                    <AppButton
                        onPress={handleSubmit}
                        text="Submit"
                    />
                    <AppButton
                        onPress={handleDisplay}
                        text="Display"
                    />
                </View>
                <StatusBar style="auto" />
            </View>
            <Toast visible={visible}>{storeText}</Toast>
        </RootSiblingParent>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#fff',
        alignItems: 'center',
        justifyContent: 'center',
        paddingHorizontal: 20,
    },
    header: {
        fontSize: 24,
        fontWeight: 'bold',
        marginBottom: 5,
    },
    names: {
        fontSize: 18,
        marginBottom: 5,
    },
    date: {
        fontSize: 16,
        marginBottom: 15,
    },
    input: {
        height: 40,
        margin: 12,
        borderWidth: 1,
        padding: 10,
    },
    buttonContainer: {
        flexDirection: 'row',
        marginTop: 10,
        gap: 10,
    },
    buttonStyle: {
        alignItems: 'center',
        justifyContent: 'center',
        paddingVertical: 12,
        paddingHorizontal: 32,
        borderRadius: 4,
        elevation: 3,
        backgroundColor: '#142E3E',
    },
    text: {
        fontSize: 16,
        lineHeight: 21,
        fontWeight: 'bold',
        letterSpacing: 0.25,
        color: 'white',
    },
});
