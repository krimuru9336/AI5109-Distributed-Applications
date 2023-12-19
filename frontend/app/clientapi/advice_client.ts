import axios from "axios";

export const advice_client = axios.create({
	baseURL: "https://api.adviceslip.com",
	headers: {
		"Content-Type": "application/json",
	},
});
