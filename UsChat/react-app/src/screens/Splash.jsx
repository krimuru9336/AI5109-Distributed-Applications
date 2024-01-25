import {useEffect, useLayoutEffect, useState} from 'react';
import {Animated, SafeAreaView, StatusBar, View} from 'react-native';
import Title from '../common/Title';

function SplashScreen({navigation}) {
  const [fadeAnim] = useState(new Animated.Value(0));
  const [zoomIn] = useState(new Animated.Value(0));

  useLayoutEffect(() => {
    navigation.setOptions({
      headerShown: false,
    });
  }, []);

  useEffect(() => {
    Animated.timing(fadeAnim, {
      toValue: 1,
      duration: 2000,

      useNativeDriver: true,
    }).start();
    Animated.timing(zoomIn, {
      toValue: 1,
      duration: 2000,
      useNativeDriver: true,
    }).start();
  }, []);

  return (
    <SafeAreaView
      style={{
        alignItems: 'center',
        flex: 1,
        justifyContent: 'center',
        backgroundColor: '#FFF9E0',
      }}>
      <StatusBar barStyle="light-content"></StatusBar>
      <View>
        <Animated.View
          style={{
            transform: [{scale: zoomIn}],
            opacity: fadeAnim,
          }}>
          <Title text="chappat" />
        </Animated.View>
      </View>
    </SafeAreaView>
  );
}

export default SplashScreen;
