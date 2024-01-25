import {Text} from 'react-native';

function Title({text, fontSize = 80}) {
  return (
    <>
      <Text
        style={{
          color: '#9893DA',
          backgroundColor: '#BBBDF6',
          textAlign: 'center',
          fontSize: fontSize,
        }}>
        {text}
      </Text>
      <Text
        style={{
          color: 'white',
          backgroundColor: '#BBBDF6',
          textAlign: 'center',
          fontSize: 20,
          fontWeight: '600',
        }}>
        Gagana Venkatesh - 1492695
      </Text>
    </>
  );
}

export default Title;
