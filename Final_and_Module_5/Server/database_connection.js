const mysql = require('mysql');

let con = null;

async function initDatabase() {
    try {
        await initDatabaseConnection();
        await createChatDatabase();
        await createUserTable();
        await createMessageDatabase();
        console.log("Database initialization completed successfully.");
    } catch (error) {
        console.error("Database initialization failed:", error);
    }
}

async function initDatabaseConnection() {
    return new Promise((resolve, reject) => {
        try {
            con = mysql.createConnection({
                host: process.env.MYSQL_HOST,
                user: process.env.MYSQL_USER,
                password: process.env.MYSQL_PASSWORD
            });

            con.connect(function(err) {
                if (err) {
                    console.log("Error connecting to database");
                    reject(err);
                    return;
                }
                console.log("Connected to database");
                resolve();
            });   
        } catch(error) {
            console.log("Error connecting to database");
            reject(error);
        }
    });
};

async function createChatDatabase() {
    return new Promise((resolve, reject) => {
        con.query("CREATE DATABASE IF NOT EXISTS chatDB", function(err, result) {
            if (err) {
                console.log("Error creating database");
                reject(err);
                return;
            }
            console.log("Database created");
            resolve();
        });
    });
}

async function createUserTable() {
    return new Promise((resolve, reject) => {
        con.query("CREATE TABLE IF NOT EXISTS chatDB.users (uuid VARCHAR(64) PRIMARY KEY, userName VARCHAR(255))", function(err, result) {
            if (err) {
                console.log("Error creating table");
                reject(err);
                return;
            }
            console.log("Table created");
            resolve();
        });
    });
}

async function getAllUsers() {
    return new Promise((resolve, reject) => {
        con.query("SELECT * FROM chatDB.users", function(err, result) {
            if (err) {
                console.log("Error getting users");
                reject(err);
                return;
            }           
            resolve(JSON.parse(JSON.stringify(result)));
        });
    });
}

async function addUser(userName, uuid) {
    return new Promise((resolve, reject) => {
        con.query("INSERT INTO chatDB.users (uuid, userName) VALUES (?, ?)", [uuid, userName], function(err, result) {
            if (err) {
                console.log("Error adding user");
                reject(err);
                return;
            }
            resolve(result);
        });
    });
}

async function createMessageDatabase() {
    return new Promise((resolve, reject) => {
        con.query(`CREATE DATABASE IF NOT EXISTS messageDB`, function(err, result) {
            if (err) {
                console.log("Error creating message database");
                reject(err);
                return;
            }

            console.log("Message database created");
            resolve();
        });
    });
}

async function createMessageTableIfNotExist(tableName) {
    return new Promise((resolve, reject) => {
        con.query(`CREATE TABLE IF NOT EXISTS messageDB.${tableName} (id VARCHAR(64) PRIMARY KEY, 
            partnerName VARCHAR(255), incoming BOOLEAN, messageText TEXT, timestamp BIGINT, 
            timestampEdit BIGINT DEFAULT NULL, deleted BOOLEAN DEFAULT false, chatGroup VARCHAR(255) DEFAULT '0')`, function(err, result) {
            if (err) {
                console.log("Error creating message table");
                reject(err);
                return;
            }
            console.log("Message table created");
            resolve();
        });
    });
}

async function addMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        con.query(`INSERT INTO messageDB.${tableName} (id, partnerName, incoming, messageText, timestamp) VALUES (?, ?, ?, ?, ?)`, 
            [message.id, message.partnerName, message.incoming, message.messageText, message.timestamp], function(err, result) {
            if (err) {
                console.log("Error adding message");
                reject(err);
                return;
            }
            console.log("Message added");
            resolve(result);
        });
    });
}

async function addEditMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        con.query(`SELECT * FROM messageDB.${tableName} WHERE id = ?`, [message.id], function(err, rows) {
            if (err) {
                console.log("Error checking if message exists");
                reject(err);
                return;
            }

            if (rows && rows.length > 0) {
                con.query(`UPDATE messageDB.${tableName} SET messageText = ?, timestampEdit = ? WHERE id = ?`, 
                    [message.messageText, message.timestampEdit, message.id], function(err, result) {
                    if (err) {
                        console.log("Error updating message");
                        reject(err);
                        return;
                    }
                    console.log("Message updated");
                    resolve(result);
                });
            } else {
                con.query(`INSERT INTO messageDB.${tableName} (id, partnerName, incoming, messageText, timestamp, timestampEdit) VALUES (?, ?, ?, ?, ?, ?)`, 
                    [message.id, message.partnerName, message.incoming, message.messageText, message.timestamp, message.timestampEdit], function(err, result) {
                    if (err) {
                        console.log("Error adding message");
                        reject(err);
                        return;
                    }
                    console.log("Message added");
                    resolve(result);
                });
            }
        });
    });
}

async function addDeleteMessage(tableName, message) {
    return new Promise((resolve, reject) => {
        con.query(`SELECT * FROM messageDB.${tableName} WHERE id = ?`, [message.id], function(err, rows) {
            if (err) {
                console.log("Error checking if message exists");
                reject(err);
                return;
            }

            if (rows && rows.length > 0) {
                con.query(`UPDATE messageDB.${tableName} SET deleted = 1, messageText = ? WHERE id = ?`, [message.messageText, message.id], 
                function(err, result) {
                    if (err) {
                        console.log("Error deleting message");
                        reject(err);
                        return;
                    }
                    console.log("Message deleted");
                    resolve(result);
                });
            } else {
                con.query(`INSERT INTO messageDB.${tableName} (id, partnerName, incoming, messageText, timestamp, deleted) VALUES (?, ?, ?, ?, ?, ?)`, 
                    [message.id, message.partnerName, message.incoming, message.messageText, message.timestamp, 1], function(err, result) {
                    if (err) {
                        console.log("Error adding message");
                        reject(err);
                        return;
                    }
                    console.log("Message added");
                    resolve(result);
                });
            }
        });
    });
}

async function getMessages(tableName) {
    return new Promise((resolve, reject) => {
        try {
            con.query(`SELECT * FROM messageDB.${tableName}`, function(err, rows) {
                if (err) {
                    console.log("Error getting messages");
                    reject(err);
                    return;
                }

                const messages = rows.map(row => ({
                    id: row.id,
                    partnerName: row.partnerName,
                    incoming: row.incoming,
                    messageText: row.messageText,
                    timestamp: row.timestamp,
                    timestampEdit: row.timestampEdit,
                    deleted: row.deleted
                }));
                console.log(messages);

                console.log("Messages retrieved");
                resolve(messages);
            });
        } catch (error) {
            console.log("Error getting messages");
            reject(error);
        }
    });
}

async function clearTable(tableName) {
    return new Promise((resolve, reject) => {
        con.query(`DELETE FROM messageDB.${tableName}`, function(err, result) {
            if (err) {
                console.log("Error clearing table");
                reject(err);
                return;
            }
            console.log("Table cleared");
            resolve(result);
        });
    });
}

module.exports = {
    init: initDatabase,
    getAllUsers,
    addUser,
    createUserMessageTable: createMessageTableIfNotExist,
    addMessage,
    addEditMessage,
    addDeleteMessage,
    getMessages,
    clearTable
};