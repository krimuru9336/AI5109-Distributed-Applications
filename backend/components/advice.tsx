"use client";
import { advice_client } from "@/app/clientapi/advice_client";
import { AxiosResponse } from "axios";
import React from "react";

const Advice = () => {
	const [advice, setAdvice] = React.useState("");

	React.useEffect(() => {
		advice_client
			.get("/advice")
			.then((response: AxiosResponse) => {
				setAdvice(response.data.slip.advice);
			})
			.catch((error) => {
				error.message;
			});
	}, [advice]);
	return (
		<div className="bg-gray-200 p-4 rounded-xl shadow-lg my-10">
			<p className="text-lg font-medium">
				{"Daily Advice: " + advice ?? "API error"}
			</p>
		</div>
	);
};

export default Advice;
