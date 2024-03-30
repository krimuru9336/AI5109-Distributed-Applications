import {useLayoutEffect} from 'react';
import {createMaterialTopTabNavigator} from '@react-navigation/material-top-tabs';

// import FriendsScreen from './Friends';
// import RequestScreen from './Requests';
// import ProfileScreen from './Profile';
// import {FontAwesomeIcon} from '@fortawesome/react-native-fontawesome';
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
      <Title text="UsChat" fontSize={40} />
      <ChatListScreen />
    </>
  );
}

export default HomeScreen;
