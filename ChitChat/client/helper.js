import AsyncStorage from '@react-native-async-storage/async-storage';

export const getUserFromLocalStorage = async () => {
    const user = await AsyncStorage.getItem('user')
    return user || null
}