import { NextFunction, Request, Response } from "express";


export const parseJSONBody = (req: Request, res: Response, next: NextFunction) => {
    const header = req.header("content-type");

    if (!header || header.toLowerCase() !== "application/json") {
        // console.warn('Request is not app/json');
        return next();
    }

    let buffer_array: Buffer[] = [];
    req
        .on("error", (err) => {
            console.error(err);
            return res
                .status(503)
                .json({ success: 0, messages: ["SERVER PARSING ERROR"] });
        })
        .on("data", (data) => {
            buffer_array.push(data);
        })
        .on("end", () => {
            try {
                const totalBuffer = Buffer.concat(buffer_array);
                if (totalBuffer.byteLength > 8e6)
                    return res
                        .status(413) // payload too large
                        .json({ success: 0, messages: ["EXCEEDED_MAX_BODY_SIZE"] });

                const stringifiedBuffer = totalBuffer.toString();
                req.body = stringifiedBuffer.length ? JSON.parse(stringifiedBuffer) : {};
            } catch (error) {
                console.error(error);
            }
            next();
        });
};
/**
 * prevents req.body from entering controllers as `undefined` to avoid unexpected errors
 */
export const replaceUndefinedBody = (req: Request, res: Response, next: NextFunction) => {
    if (typeof req.body === "undefined") req.body = {};
    next();
};

