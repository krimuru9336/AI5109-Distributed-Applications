import prisma from "../lib/prisma";

async function main() {
	const response = await Promise.all([
		prisma.users.upsert({
			where: { id: 1 },
			update: {},
			create: {
				name: "Rishabh",
				mobile: "123456899@s.com",
				image: "https://i.pravatar.cc/300",
			},
		}),
	]);
	console.log(response);
}
main()
	.then(async () => {
		await prisma.$disconnect();
	})
	.catch(async (e) => {
		console.error(e);
		await prisma.$disconnect();
		process.exit(1);
	});
