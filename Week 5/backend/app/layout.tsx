// Rishabh Goswami
// matriculation number: 1455991
//28 October 2023

// This is the main page of the application. It is the first page that is loaded when the application is started.
// This is the Home page you see on browser. I am importing a Table component and TableEdit (which allow user to add user).
// This is front end part of the application
// look inside pages/api/user.ts file to see the backend part of the application where I am using Prisma ORM to connect to Postgres database and create a new user
// Look inside prisma folder to see the schema.prisma file where I am defining the database schema
// Look inside table and tableEdit component to see the front end part of the application where i am displaying the data and allowing user to add new new data
import "./globals.css";
import { Inter } from "next/font/google";

export const metadata = {
	metadataBase: new URL("https://www.vercel.app"),
	title: "Distributed Applications Week 1",
	description:
		"Week 1 assignment for Distributed Applications course at Fulda Hochschule",
};

const inter = Inter({
	variable: "--font-inter",
	subsets: ["latin"],
	display: "swap",
});

export default function RootLayout({
	children,
}: {
	children: React.ReactNode;
}) {
	return (
		<html lang="en">
			<body className={inter.variable}>{children}</body>
		</html>
	);
}
