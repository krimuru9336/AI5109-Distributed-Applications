import {SafeAreaView} from 'react-native-safe-area-context';
import {Text} from 'react-native';
import ChatScreen from './Chat';
import ChatListScreen from './ChatListScreen';

function FriendsScreen() {
  return <ChatListScreen />;
}

export default FriendsScreen;
