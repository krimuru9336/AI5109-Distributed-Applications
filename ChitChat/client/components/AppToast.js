import { useState, useEffect } from 'react';
import Toast from 'react-native-root-toast';

export default AppToast = (props) => {

    const [visible, setVisible] = useState(props.visible)

    useEffect(() => {
        setVisible(props.visible);

        const timeout = setTimeout(() => {
            setVisible(false);
            props.setError("")
        }, 800);

        return () => clearTimeout(timeout);
    }, [props.visible]);

    return (
        <Toast visible={visible}>{props.text}</Toast>
    )
}