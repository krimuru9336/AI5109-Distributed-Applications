import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";
import { getFirestore } from "firebase/firestore";
import Constants from "expo-constants";
// Firebase config
const firebaseConfig = {
	apiKey: "AIzaSyBF5OM223Ccmf6y5xsT-1zscTe7ImZbj9o",
	authDomain: "distributed-systems-ac0be.firebaseapp.com",
	projectId: "distributed-systems-ac0be",
	storageBucket: "distributed-systems-ac0be.appspot.com",
	messagingSenderId: "114036931597",
	appId: "1:114036931597:web:144d661ca089640bc3e944",
	measurementId: "G-7SSXPDLTNT",
	databaseURL: "https://distributed-systems-ac0be-default-rtdb.firebaseio.com/",
};
// initialize firebase
initializeApp(firebaseConfig);
export const auth = getAuth();
export const database = getFirestore();
