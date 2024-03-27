import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import RegisterScreen from './components/RegisterScreen';
import UsersScreen from './components/UserScreen';
import PersonalChatScreen from './components/PersonalChat';
import GroupScreen from './components/GroupScreen';
import { View,Text } from 'react-native';



const Stack = createStackNavigator();

const CustomHeaderTitle = () => (
  <View style={{alignItems:'center'}}>
    <Text style={{ fontSize: 14, fontWeight: 'bold' }}>Hauva vali</Text>
    <Text style={{ fontSize: 14, fontWeight: 'normal', marginLeft:10 }}>Matriculation Number:1493577</Text>
  </View>
);


const screenOptions = {
 headerTitle: ()=> <CustomHeaderTitle />,
 
};

  
const App = () => {
  return (
    <NavigationContainer>
       <Stack.Navigator initialRouteName="Register" screenOptions={screenOptions}> 
        <Stack.Screen name="Register" component={RegisterScreen}/>
        <Stack.Screen name="Users" component={UsersScreen}  />
        <Stack.Screen name="PersonalChat" component={PersonalChatScreen}  />
        <Stack.Screen name="Groups" component={GroupScreen} />
       </Stack.Navigator>
        
    </NavigationContainer>
  );
};

export default App;
