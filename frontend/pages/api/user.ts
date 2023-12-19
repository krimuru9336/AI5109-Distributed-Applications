// Rishabh Goswami
// matriculation number: 1455991
//28 October 2023

// POST /api/user
import type { NextApiRequest, NextApiResponse } from "next";
import prisma from "@/lib/prisma";
import { advice_client } from "@/app/clientapi/advice_client";
import { AxiosResponse } from "axios";

export default async function handle(
	req: NextApiRequest,
	res: NextApiResponse
) {
	await advice_client.get("/advice").then(async (response: AxiosResponse) => {
		console.log("Header: ", response.headers);
		console.log("Status: ", response.status);
		console.log("JSON Data: ", response.data);
	});

	const result = await prisma.users.create({
		data: {
			name: req.body.name,
			mobile: req.body.mobile,
			image: "https://i.pravatar.cc/300",
		},
	});
	return res.status(201).json({ result });
}
