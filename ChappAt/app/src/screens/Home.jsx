import {useLayoutEffect} from 'react';
import {createMaterialTopTabNavigator} from '@react-navigation/material-top-tabs';

import FriendsScreen from './Friends';
import RequestScreen from './Requests';
import ProfileScreen from './Profile';
import {FontAwesomeIcon} from '@fortawesome/react-native-fontawesome';
import Title from '../common/Title';
import ChatListScreen from './ChatListScreen';

const Tab = createMaterialTopTabNavigator();

function HomeScreen({navigation}) {
  useLayoutEffect(() => {
    navigation.setOptions({
      headerShown: false,
    });
  }, []);
  return (
    <>
      <Title text="chappat" fontSize={40} />
      <Tab.Navigator
        screenOptions={({route, navigation}) => ({
          tabBarIcon: ({focused, color, size}) => {
            const icons = {
              Requests: 'bell',
              ChatList: 'inbox',
              Profile: 'user',
            };
            const icon = icons[route.name];
            return (
              <FontAwesomeIcon
                style={{padding: 2}}
                icon={icon}
                size={24}
                color={color}
              />
            );
          },
          tabBarActiveTintColor: 'orange',
          tabBarInactiveTintColor: '#bababa',
          tabBarShowLabel: false,
          tabBarAndroidRipple: {borderless: false},
          tabBarIndicator: null,
          tabBarIndicatorStyle: null,
        })}>
        <Tab.Screen name="Requests" component={RequestScreen} />
        <Tab.Screen name="ChatList" component={ChatListScreen} />
        {/* <Tab.Screen name="Profile" component={ProfileScreen} /> */}
      </Tab.Navigator>
    </>
  );
}

export default HomeScreen;
