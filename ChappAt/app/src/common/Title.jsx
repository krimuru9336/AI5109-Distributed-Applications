import {Text} from 'react-native';

function Title({text, fontSize = 80}) {
  return (
    <>
      <Text
        style={{
          color: 'orange',
          backgroundColor: '#FFF9E0',
          textAlign: 'center',
          fontSize: fontSize,
          fontFamily: 'Nabla-Regular',
        }}>
        {text}
      </Text>
      <Text
        style={{
          color: 'orange',
          backgroundColor: '#FFF9E0',
          textAlign: 'center',
          fontSize: 18,
        }}>
        Amar Sharma - 1492710
      </Text>
    </>
  );
}

export default Title;
