// Rishabh Goswami
// matriculation number: 1455991
//28 October 2023

import ms from "ms";

export const timeAgo = (timestamp: Date, timeOnly?: boolean): string => {
	if (!timestamp) return "never";
	return `${ms(Date.now() - new Date(timestamp).getTime())}${
		timeOnly ? "" : " ago"
	}`;
};
