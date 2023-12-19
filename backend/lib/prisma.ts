// Rishabh Goswami
// matriculation number: 1455991
//28 October 2023
import { PrismaClient } from "@prisma/client";

declare global {
	var prisma: PrismaClient | undefined;
}

const prisma = global.prisma || new PrismaClient();

if (process.env.NODE_ENV === "development") global.prisma = prisma;

export default prisma;
