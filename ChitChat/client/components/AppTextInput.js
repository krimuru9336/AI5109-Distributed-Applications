import { TextInput } from 'react-native';
import { StyleSheet } from 'react-native';

export default AppTextInput = (props) => {
    return (
        <TextInput
            style={styles.input}
            {...props}
        />
    )
}

const styles = StyleSheet.create({
    input: {
        height: 40,
        width: '80%',
        borderWidth: 1,
        marginBottom: 20,
        padding: 10,
    },
});