# Distributed Applications (WiSe 2023/24)

## Assignment week 4

### \*\*\*\* **for assignment week 5 please refer to: [This file](./week5.md)** \*\*\*\*

`Student Name: Iman Emadi`

### Short description:

A Nodejs app that makes an HTTP request to a third party API and stores the response in its MySQL database.
features:

- Database: `Azure Database for MySQL flexible servers`.
- Server OS: `Azure VM, Ubuntu server v22`
- app: `NodeJS server, powered by ExpressJS framework`.

### Installation

1. Install dependencies using `yarn`, `npm` or a similar package manager.
1. Update scripts in `package.json` if not using `yarn`.
1. Install MySQL v8 on your machine (optional).
1. Populate the [db/index](./src/db/index.ts) with a database connection info. (either local or remote database)
1. run `dev` script. the app should be running on localhost with the specified port. (default to http://localhost)

### Project structure

- `/actions` : Files containing functions that each, handle requests to a specific route, are placed in this directory.
- `/db` : contains the `database` related files. for example, the file that establishes the connection to the database.
- `server.ts` : entry point to the program. starts the nodeJS and contains ExpressJS routing specifications.
  change of the `port`, which server listens to or adding a new route handler, takes place here.

- `/scripts` : contains scripts that are not part of the main project, but act as helpers. either when compiling and building the output code for production or when starting the project on the production server.
  - `db.init.ts`: makes sure the required `DATABASE` and its `TABLES` exit in the target database.
  - `post.build.ts`: prepares the compiled code for the production. (e.g: creating appropriate `package.json` for production)
- `/types` : contains the typescript type definitions.

### Assignment week 4:

Assignment week 4 is coded after week 3, adding more functionality to the previous code.

#### Action Flow Explanation:

the `getData` function in [form.actions.ts](./src/actions/form.actions.ts), defines a route handler that handles the HTTP GET requests to `/getData`.
It expects two query parameters, that specify the target `longitude` and `latitude`. when provided, they pass these two parameters to the [HTTP Service](./src/services/http.srv.ts), which then makes an HTTP request to a third party API, and returns its response.

the response is first inserted into the Database, then also included in the server response to the client (in JSON format).
the API response is stored in a table named `api_results` in `mySQL` database.

Then this app is uploaded to an `Ubuntu` server Azure Cloud.
additionally, a `MySQL database` is also created on Azure Cloud. the credentials then are given to the `db/index` file.

after sending a sample request to our endpoint `/getData`, we use the `mysql` cli to connect to our online database and display our saved record.

        Screenshots of this process, are included in the Assignment week 4's PDF. (not published here due to containing sensitive/personal information)
