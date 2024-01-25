import {View, Text, TextInput} from 'react-native';

function Input({title, value, onChangeText, secureTextEntry = false}) {
  return (
    <View>
      <Text
        style={{
          color: '#70747a',
          marginVertical: 10,
          paddingLeft: 5,
        }}>
        {title}
      </Text>
      <TextInput
        style={{
          backgroundColor: 'white',
          borderWidth: 1,
          borderColor: 'transparent',
          borderRadius: 10,
          height: 52,
          paddingHorizontal: 16,
          fontSize: 16,
        }}
        secureTextEntry={secureTextEntry}
        onChangeText={onChangeText}
        value={value}
      />
    </View>
  );
}

export default Input;
