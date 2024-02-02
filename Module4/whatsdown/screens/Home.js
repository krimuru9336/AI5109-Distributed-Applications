import React, { useEffect } from "react";
import { View, TouchableOpacity, Text, Image, StyleSheet } from "react-native";
import { useNavigation } from "@react-navigation/native";
import { FontAwesome } from "@expo/vector-icons";
import colors from "../colors";
import { Entypo } from "@expo/vector-icons";

const Home = () => {
	const navigation = useNavigation();

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

			<View style={styles.container}>
				<TouchableOpacity
					onPress={() => navigation.navigate("Chat")}
					style={styles.chatButton}
				>
					<Entypo name="new-message" size={34} color={"white"} />
				</TouchableOpacity>
			</View>
		</>
	);
};

export default Home;

const styles = StyleSheet.create({
	container: {
		flex: 1,
		justifyContent: "flex-end",
		alignItems: "flex-end",
		backgroundColor: "#fff",
	},
	chatButton: {
		backgroundColor: "#A041EE",
		height: 90,
		width: 90,
		borderRadius: 35,
		alignItems: "center",
		justifyContent: "center",
		shadowColor: colors.primary,
		shadowOffset: {
			width: 0,
			height: 2,
		},
		shadowOpacity: 0.9,
		shadowRadius: 8,
		marginRight: 20,
		marginBottom: 50,
	},
});
