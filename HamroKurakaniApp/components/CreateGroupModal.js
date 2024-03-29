import { AddIcon, Badge, BadgeText, Box, Button, ButtonText, CloseIcon, FormControl, FormControlHelper, FormControlLabel, FormControlLabelText, HStack, Heading, Icon, Input, InputField, Modal, ModalBackdrop, ModalBody, ModalCloseButton, ModalContent, ModalFooter, ModalHeader, VStack } from "@gluestack-ui/themed"
import axios from "axios";
import { API_URL } from "@env"
import { useContext, useState } from "react"
import { Alert, TouchableOpacity } from "react-native";
import AuthContext from "../context/AuthContext";

export default ({ showModal, setShowModal, successCallback }) => {
    const { accessToken } = useContext(AuthContext);
    const [groupName, setGroupName] = useState("");
    const [newMember, setNewMember] = useState("");
    const [allMembers, setAllMembers] = useState([]);

    const addNewMember = () => {
        setAllMembers(prev => [...prev, newMember]);
    }

    const removeFromMembers = (memberToRemove) => {
        setAllMembers(prev => prev.filter(member => member !== memberToRemove))
    }

    const hanldeSubmit = async () => {
        try {
            const response = await axios.post(`${API_URL}/groups`, { group_name: groupName, member_names: allMembers }, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                },
            });
            setShowModal(false);
            successCallback();
        } catch (err) {
            console.log(err)
        }
    }

    return (
        <Modal
            isOpen={showModal}
            onClose={() => {
                setShowModal(false)
            }}
        >
            <ModalBackdrop />
            <ModalContent>
                <ModalHeader>
                    <Heading size="lg">Create Group </Heading>
                    <ModalCloseButton>
                        <Icon as={CloseIcon} />
                    </ModalCloseButton>
                </ModalHeader>
                <ModalBody>
                    <VStack space="md">
                        <FormControl maxWidth="$80">
                            <FormControlLabel>
                                <FormControlLabelText>Group Name</FormControlLabelText>
                            </FormControlLabel>
                            <Input>
                                <InputField value={groupName} onChangeText={setGroupName} />
                            </Input>
                        </FormControl>
                        <FormControl maxWidth="$80">
                            <FormControlLabel>
                                <FormControlLabelText>Add Members</FormControlLabelText>
                            </FormControlLabel>
                            <HStack>
                                <Input flex={1}>
                                    <InputField value={newMember} onChangeText={(member) => { setNewMember(member) }} />
                                </Input>
                                <Button onPress={() => { addNewMember() }}>
                                    <Icon as={AddIcon} />
                                </Button>
                            </HStack>
                        </FormControl>
                        <VStack gap={5}>
                            {
                                allMembers.map((member, idx) => (
                                    <TouchableOpacity onPress={() => removeFromMembers(member)} key={`${member}-${idx}`}>
                                        <Badge size="md" variant="solid" borderRadius="$md" action="info">
                                            <BadgeText>{member}</BadgeText>
                                        </Badge>
                                    </TouchableOpacity>
                                ))
                            }
                        </VStack>
                    </VStack>
                </ModalBody>
                <ModalFooter>
                    <Button onPress={hanldeSubmit}>
                        <ButtonText>
                            Submit
                        </ButtonText>
                    </Button>
                </ModalFooter>
            </ModalContent>
        </Modal>
    )
}