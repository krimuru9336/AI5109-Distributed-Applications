import React, {
	useState,
	useEffect,
	useLayoutEffect,
	useCallback,
} from "react";
import { TouchableOpacity, Text, View, TextInput, Button } from "react-native";
import { GiftedChat } from "react-native-gifted-chat";
import {
	collection,
	addDoc,
	orderBy,
	query,
	onSnapshot,
	deleteDoc,
	doc,
} from "firebase/firestore";
import { signOut } from "firebase/auth";
import { auth, database } from "../config/firebase";
import { useNavigation } from "@react-navigation/native";
import { AntDesign } from "@expo/vector-icons";
import colors from "../colors";
import Modal from "react-native-modal";

export default function Chat() {
	const [messages, setMessages] = useState([]);
	const navigation = useNavigation();
	const [editingMessage, setEditingMessage] = useState(null);
	const [editingMessageReference, setEditingMessageReference] = useState(null);
	const [editModalVisible, setEditModalVisible] = useState(false);

	const onSignOut = () => {
		signOut(auth).catch((error) => console.log("Error logging out: ", error));
	};

	useLayoutEffect(() => {
		const collectionRef = collection(database, "chats");
		const q = query(collectionRef, orderBy("createdAt", "desc"));

		const unsubscribe = onSnapshot(q, (querySnapshot) => {
			console.log("querySnapshot unsusbscribe");
			setMessages(
				querySnapshot.docs.map((doc) => ({
					_id: doc.data()._id,
					createdAt: doc.data().createdAt.toDate(),
					text: doc.data().text,
					user: doc.data().user,
				}))
			);
		});
		return unsubscribe;
	}, []);

	const onLongPress = (context, currentMessage) => {
		let options = ["Edit", "Delete", "Cancel"];
		setEditingMessageReference(currentMessage);

		const cancelButtonIndex = options.length - 1;

		if (currentMessage.user._id !== auth?.currentUser?.email) {
			options = ["Copy", "Cancel"];
		}

		context.actionSheet().showActionSheetWithOptions(
			{
				options,
				cancelButtonIndex,
			},
			(buttonIndex) => {
				switch (buttonIndex) {
					case 0:
						if (currentMessage.user._id === auth?.currentUser?.email) {
							setEditingMessage(currentMessage.text);
							setEditModalVisible(true);
						} else {
							// Copy
							console.log("Copy");
						}
						break;
					case 1:
						onDelete(currentMessage);
						break;
					default:
						// Cancel
						break;
				}
			}
		);
	};

	const onDelete = async (messageToDelete) => {
		// Implement your delete logic here
		setMessages((prevMessages) =>
			prevMessages.filter((message) => message._id !== messageToDelete._id)
		);
		console.log("messageToDelete");
		await deleteDoc(doc(database, "chats", messageToDelete._id));
	};

	const onSend = useCallback((messages = []) => {
		setMessages((previousMessages) =>
			GiftedChat.append(previousMessages, messages)
		);

		const { _id, createdAt, text, user } = messages[0];
		addDoc(collection(database, "chats"), {
			_id,
			createdAt,
			text,
			user,
		});
	}, []);

	const handleEdit = async () => {
		console.log(JSON.stringify(editingMessageReference));
		if (editingMessageReference) {
			setMessages((prevMessages) =>
				prevMessages.map((message) => {
					if (message._id === editingMessageReference._id) {
						return { ...editingMessageReference, text: editingMessage };
					} else {
						return message;
					}
				})
			);

			// setMessages([
			// 	...messages,
			// 	{ ...editingMessageReference, text: editingMessage.text },
			// ]);

			setEditModalVisible(false);
		}
	};

	return (
		<>
			<Text
				style={{
					textAlign: "center",

					fontSize: 20,
					fontWeight: "bold",
					marginTop: 10,
				}}
			>
				Rishabh Goswami
			</Text>

			<Text
				style={{
					textAlign: "center",

					fontSize: 15,
					marginVertical: 10,
				}}
			>
				{"1455991 (fdai7680) Distributed Application 2024"}
			</Text>

			<Modal
				animationType="slide"
				transparent={true}
				visible={editModalVisible}
				onRequestClose={() => setEditModalVisible(false)}
			>
				<View
					style={{ flex: 1, justifyContent: "center", alignItems: "center" }}
				>
					<View
						style={{
							backgroundColor: "white",
							padding: 20,
							borderRadius: 10,
							width: "80%",
						}}
					>
						<TextInput
							multiline
							placeholder="Edit your message"
							value={editingMessage?.text}
							onChangeText={(text) => setEditingMessage(text)}
							style={{
								marginBottom: 10,
								borderBottomWidth: 1,
								borderBottomColor: "#ccc",
							}}
						/>
						<Button title="Save" onPress={handleEdit} />
					</View>
				</View>
			</Modal>

			<GiftedChat
				messages={messages}
				showAvatarForEveryMessage={false}
				showUserAvatar={true}
				onSend={(messages) => onSend(messages)}
				onLongPress={(context, currentMessage) =>
					onLongPress(context, currentMessage)
				}
				messagesContainerStyle={{
					backgroundColor: "#f8f8f8",
				}}
				textInputStyle={{
					backgroundColor: "#ffff",
					borderRadius: 20,
				}}
				user={{
					_id: auth?.currentUser?.email,
					avatar: auth?.currentUser?.email.includes("rishabh")
						? "https://media.licdn.com/dms/image/D4E03AQFR8eE0vZBZDA/profile-displayphoto-shrink_400_400/0/1697491393643?e=1710979200&v=beta&t=sSggHP758e4YGda4t0cXdOTfuwsHaZPQFxCWhUO3hNQ"
						: "https://i.pravatar.cc/300",
				}}
			/>
		</>
	);
}
