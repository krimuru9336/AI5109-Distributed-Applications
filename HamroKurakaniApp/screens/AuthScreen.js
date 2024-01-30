import React, { useEffect, useState, useContext } from 'react'
import {
    Box,
    FormControl,
    FormControlLabel,
    FormControlLabelText,
    Input,
    InputField,
    Button,
    ButtonText,
    VStack,
    Toast,
    ToastTitle,
    ToastDescription,
    useToast
} from '@gluestack-ui/themed'
import { API_URL } from "@env"
import axios from 'axios'
import AuthContext from '../context/AuthContext'

export default function AuthScreen({ onSuccessfulLogin }) {
    const [selectedTab, setSelectedTab] = useState("login");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const { setAccessToken } = useContext(AuthContext);
    const toast = useToast();

    const handleSubmit = () => {
        (async () => {
            if (selectedTab == "login") {
                try {
                    const response = await axios.post(`${API_URL}/login`, { username, password });
                    setAccessToken(response.data.accessToken)
                } catch (err) {
                    console.log(err)
                }
            } else if (selectedTab == "register") {
                const response = await axios.post(`${API_URL}/register`, {
                    username,
                    password
                });
                toast.show({
                    placement: "bottom",
                    render: ({ id }) => {
                        const toastId = `toast-${id}`;
                        return (
                            <Toast nativeID={toastId} action="success" variant="solid">
                                <VStack space="xs">
                                    <ToastTitle>Register Successful</ToastTitle>
                                    <ToastDescription>
                                        You have registered successfully. Please login.
                                    </ToastDescription>
                                </VStack>
                            </Toast>
                        )
                    }
                })
            }
        })()
    }

    return (
        <Box h="$32" w="$72">
            <Box>
                <FormControl size="md" isDisabled={false} isInvalid={false} isReadOnly={false} isRequired={false} >
                    <FormControlLabel mb='$1'>
                        <FormControlLabelText>Username</FormControlLabelText>
                    </FormControlLabel>
                    <Input>
                        <InputField
                            type="text"
                            placeholder="username"
                            value={username}
                            onChangeText={(val) => setUsername(val)}
                        />
                    </Input>
                </FormControl>
                <FormControl size="md" isDisabled={false} isInvalid={false} isReadOnly={false} isRequired={false} >
                    <FormControlLabel mb='$1'>
                        <FormControlLabelText>Password</FormControlLabelText>
                    </FormControlLabel>
                    <Input>
                        <InputField
                            type="password"
                            placeholder="password"
                            value={password}
                            onChangeText={(val) => setPassword(val)}
                        />
                    </Input>
                </FormControl>
                <FormControl size="md" isDisabled={false} isInvalid={false} isReadOnly={false} isRequired={false} >
                    <Button
                        size="md"
                        variant="solid"
                        action="primary"
                        isDisabled={false}
                        isFocusVisible={false}
                        onPress={handleSubmit}
                    >
                        <ButtonText>{selectedTab}</ButtonText>
                    </Button>
                </FormControl>
            </Box>
            <Box>
                {
                    selectedTab == "login" ?
                        <Button
                            size="md"
                            variant="link"
                            action="primary"
                            isDisabled={false}
                            isFocusVisible={false}
                            onPress={() => { setSelectedTab("register") }}
                        >
                            <ButtonText>Register Instead</ButtonText>
                        </Button> :
                        <Button
                            size="md"
                            variant="link"
                            action="primary"
                            isDisabled={false}
                            isFocusVisible={false}
                            onPress={() => { setSelectedTab("login") }}
                        >
                            <ButtonText>Login Instead</ButtonText>
                        </Button>
                }
            </Box>
        </Box>
    )
}