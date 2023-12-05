import { HTTPService } from './../services/http.srv';
import { NAME_REGEX, PHONE_REGEX } from './../constants';
import { readCSSFiles, readJSFiles } from '../ejs/readers.ejs';
import { renderLayout, renderView } from './../ejs/engines.ejs';
import { Request, Response as Express_Response } from "express";
import { conn } from '../db';

export const renderForm = (req: Request, res: Express_Response) => {

    const view_str = renderView('form.ejs', {});
    const styles = readCSSFiles(['form.css']);
    const scripts = readJSFiles(['form.js']);
    const response_str = renderLayout('layout.ejs',
        { body1: view_str },
        { doc_title: 'Entry Form', page_scripts: scripts, page_styles: styles }
    )

    res.status(200).end(response_str);
}

export const handleForm = async (req: Request, res: Express_Response) => {
    /**
     *! Distributed Applications
     ** Name: Iman Emadi
     ** Matriculation number: 1452312
     *? Date: 23/11/2023
     */
    const { name, phoneNumber } = req.body;

    if (!name || !NAME_REGEX.test(name))
        return res.status(400).json({ ok: 0, message: 'Invalid value for Name.' });

    if (!phoneNumber || !PHONE_REGEX.test(phoneNumber))
        return res.status(400).json({ ok: 0, message: 'Invalid value for phoneNumber.' });


    conn.query(`SELECT * FROM users WHERE phoneNumber=${phoneNumber}`, (err, results, fields) => {
        if (results.length > 0)
            return res.status(200).json({ ok: 0, message: 'User already Exists.' });

        const uid = Date.now().toString(16);

        conn.query(`INSERT INTO users (phoneNumber, name,uid) VALUES (${phoneNumber}, ${name},${uid});`, (e, r, f) => {
            if (e) return res.status(500).json({ ok: 0, message: 'SERVER ERROR!' });
            return res.status(201).json({ ok: 1, message: 'Success.!', payload: { uid } });
        })

    });
}

/**
 *! Distributed Applications
 ** Name: Iman Emadi
 ** Matriculation number: 1452312
 *? Date: 23/11/2023
 */

export const getUser = async (req: Request, res: Express_Response) => {
    const { uid } = req.query;

    if (typeof uid === 'string') {
        conn.query(`SELECT * FROM users WHERE uid=${uid}`, (e, r, f) => {
            if (!e) {
                const user = r[0];
                const view_str = renderView('user.ejs', user);
                const styles = readCSSFiles(['user.css']);
                const scripts = readJSFiles(['user.js']);
                const response_str = renderLayout('layout.ejs',
                    { body1: view_str },
                    { doc_title: 'User Info', page_scripts: scripts, page_styles: styles }
                )
                return res.status(200).end(response_str);
            }
            return res.status(500).json({ ok: 0, message: 'SERVER ERROR!' });
        });
    }

    const view_str = renderView('invalid-user.ejs', {});
    const styles = readCSSFiles(['user.css']);
    const response_str = renderLayout('layout.ejs',
        { body1: view_str },
        { doc_title: 'Invalid User', page_scripts: '', page_styles: styles }
    )
    return res.status(400).end(response_str);

}

/**
 *! Distributed Applications
 ** Name: Iman Emadi
 ** Matriculation number: 1452312
 *? Date: 23/11/2023
 */
export const getData = async (req: Request, res: Express_Response) => { //! defining the request handler, named `getData`
    const { lat, long } = req.query as { lat: string, long: string }; //? reading query parameters of the request.
    if (!lat || !long) return res.status(400).end("Latitude and longitude parameters are mandatory!"); //? simple validation of the parameters

    const HTTPServiceInstance = new HTTPService(); // Instantiation of the HTTP Class
    const result = await HTTPServiceInstance.fetchData(lat, long); // making the request using our defined HTTP service's method.

    // Iterating over the Response headers and putting them into a printable object.
    let headers: Record<any, any> = {};
    for (const key of result.headers.keys()) //? looping header's keys
        headers[key] = result.headers.get(key); //? setting headers key-values

    const json_data = await result.json();
    const db_set = { ...json_data };
    delete db_set['timezone_abbreviation'];

    conn.query("INSERT INTO api_results SET ?", db_set, (e, r, f) => { //** Inserting the API Response into the Database.
        if (e) return res.status(500).json({ ok: 0, message: 'SERVER ERROR!' });
        else
            res.status(200).json({ //** Send the response to the user
                message: 'DATA SAVED TO THE DB',
                status: result.status, //? status Code of the API
                statusText: result.statusText,//? status Text of the API response
                headers, // the headers object we created above.
                api_response: json_data //? The Response's body content, converted into JSON formatted before sending to the client.
            });
    })
}









