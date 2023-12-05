import { conn } from "../db";

/**
 *! Distributed Applications
 ** Name: Iman Emadi
 ** Matriculation number: 1452312
 *? Date: 23/11/2023
 */
(async () => {

    conn.query("CREATE DATABASE IF NOT EXISTS distributed_Apps;");

    const create_user = new Promise((res, rej) => {
        conn.query(`CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            uid VARCHAR(50) NOT NULL,
            phoneNumber VARCHAR(20) NOT NULL,
            name VARCHAR(255) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );`, (err, results, fields) => {
            if (err) rej(err);
            else res(results);
        })
    });

    const create_api_results = new Promise((rs, rj) => {
        conn.query(`CREATE TABLE IF NOT EXISTS api_results (
            elevation FLOAT,
            longitude FLOAT,
            latitude FLOAT,
            generationtime_ms DOUBLE,
            utc_offset_seconds INT,
            timezone VARCHAR(10)
        );
        `, (err, r, f) => {
            if (err) rj(err);
            else rs(r);
        })
    })

    await Promise.all([create_user, create_api_results]);

    process.exit();
})()




