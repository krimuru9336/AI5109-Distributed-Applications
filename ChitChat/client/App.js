import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createStackNavigator } from "@react-navigation/stack";
import SignUp from "./screens/signUp";
import HomeScreen from "./screens/home";
import Chat from "./screens/chat";
import { SocketProvider } from "./SocketContext";
import { Text, View, KeyboardAvoidingView } from "react-native";

const Stack = createStackNavigator();

const CustomHeaderTitle = () => (
  <View style={{ alignItems: "center" }}>
    <Text style={{ fontWeight: 700 }}>Dipesh Kewalramani (fdai8004)</Text>
    <Text style={{ fontWeight: 700 }}>1493481 (Matrikel-Nr)</Text>
  </View>
);

const screenOptions = {
  headerTitle: () => <CustomHeaderTitle />,
};

export default App = () => {
  return (
    <KeyboardAvoidingView
      style={{ flex: 1 }}
      behavior={Platform.OS === "ios" ? "padding" : "height"}
    >
      <NavigationContainer>
        <SocketProvider>
          <Stack.Navigator screenOptions={screenOptions}>
            <Stack.Screen name="Sign Up" component={SignUp} />
            {/* <Stack.Screen name="Module 1" component={Module1} /> */}
            <Stack.Screen name="Home" component={HomeScreen} />
            <Stack.Screen name="Chat" component={Chat} />
          </Stack.Navigator>
        </SocketProvider>
      </NavigationContainer>
    </KeyboardAvoidingView>
  );
};
