import { StyleSheet, Text, Pressable } from 'react-native';

export default AppButton = (props) => {
    return (
        <Pressable
            style={({ pressed }) => [
                styles.buttonStyle,
                pressed && { backgroundColor: '#0D1E2B' },
            ]}
            {...props}
        >
            <Text style={styles.text}>{props.text}</Text>
        </Pressable>
    )
}

const styles = StyleSheet.create({
    buttonStyle: {
        alignItems: 'center',
        justifyContent: 'center',
        paddingVertical: 12,
        paddingHorizontal: 32,
        borderRadius: 4,
        elevation: 3,
        backgroundColor: '#142E3E',
    },
    text: {
        fontSize: 16,
        lineHeight: 21,
        fontWeight: 'bold',
        letterSpacing: 0.25,
        color: 'white',
    },
});