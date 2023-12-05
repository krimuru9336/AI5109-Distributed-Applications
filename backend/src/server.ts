import { parseJSONBody, replaceUndefinedBody } from './middlewares';
import { getData, getUser, handleForm, renderForm } from './actions/form.actions';
import http from 'http';
import express, { NextFunction, Request, Response } from 'express';
import { conn } from './db';

/**
 *! Distributed Applications
 ** Name: Iman Emadi
 ** Matriculation number: 1452312
 *? Date: 23/11/2023
 */

// ** ------------------Bootstrapping Node.js Server------------------------
const app = express();
app.use(parseJSONBody, replaceUndefinedBody);
app.use(express.urlencoded({ extended: true }));
app.use((req: Request, res: Response, next: NextFunction) => {
    res.set({
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Credentials": "true",
        "Access-Control-Allow-Headers": "Content-Type,language, User-Agent, X-Requested-With, Origin, Accept-Language",
        "Access-Control-Allow-Methods": "POST, GET, OPTIONS",
    });

    if (req.method === "OPTIONS") return res.status(200).end();
    next();
})

app.get("/", renderForm);
app.post('/user', handleForm);
app.get('/user', getUser);
app.get("/getData", getData); //? <-------- This is the route we are gonna use 

http.createServer({}, app);

// listening on port 80, on localhost
const server = app.listen(parseInt(process.env.PORT ?? "80"), () => console.log(`Server started. listening on port 80`));

// ** --------------------------------------------------------------------------


for (const signal of ["SIGINT", "SIGTERM"]) {
    // Use once() so that double signals exits the app
    process.once(signal, () => {
        console.log('DISPOSING OF MYSQL CONNECTION!');
        server.close();
        conn.end();
    })
}

