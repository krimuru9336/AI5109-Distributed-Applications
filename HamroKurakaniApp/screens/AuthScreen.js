import React, { useEffect, useState } from 'react'
import {
    Box,
    FormControl,
    FormControlLabel,
    FormControlLabelText,
    Input,
    InputField,
    Button,
    ButtonText
} from '@gluestack-ui/themed'
import { API_URL } from "@env"
import axios from 'axios';

export default function AuthScreen({ onSuccessfulLogin }) {
    const [selectedTab, setSelectedTab] = useState("login");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = () => {
        (async () => {
            if (selectedTab == "login") {
                try {
                    // // const res = await axios.post(`http://localhost:5000/login`);
                    // const res = await axios("http://localhost:8080");
                    // console.log("RES:: ", res)

                    const response = await axios.get('http://127.0.0.1:5000/');
                    // const json = await response.json();
                    console.log('JSON::: ', response.data)

                } catch (err) {
                    console.log("this is here")
                    console.log(err)
                }
            } else if (selectedTab == "register") {

            }
        })()
    }

    // const handleSubmit = () => {
    //     onSuccessfulLogin(username)
    // }

    return (
        <Box h="$32" w="$72">
            <Box>
                <Button
                    size="md"
                    variant="link"
                    action="primary"
                    isDisabled={false}
                    isFocusVisible={false}
                    onPress={() => { setSelectedTab("login") }}
                >
                    <ButtonText>Login</ButtonText>
                </Button>
                <Button
                    size="md"
                    variant="link"
                    action="primary"
                    isDisabled={false}
                    isFocusVisible={false}
                    onPress={() => { setSelectedTab("register") }}
                >
                    <ButtonText>Register</ButtonText>
                </Button>
            </Box>
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
                            onChange={(e) => setPassword(e.target.value)}
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
        </Box>
    )
}