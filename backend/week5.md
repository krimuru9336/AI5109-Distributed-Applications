# Assignment Week 5

## By iman.emadi@informatik.hs-fulda.de

### Front-end repository: https://github.com/Iman-Emadi/week5

### `Explanation of Week 5 assignment: Back-end`

The project structure is exactly the same as in the Assignment 4.
As more explanation is required, here you can find a more detailed description of the Back-end project.

_server.ts_

```ts
const app = express();
http.createServer({}, app);
const server = app.listen(parseInt(process.env.PORT ?? "80"), () =>
  console.log(`Server started. listening on port 80`)
);
```

Here we create an Express.js server, which listens to a specific port on the server for incoming `HTTP Requests`.

then we define a function to handle the GET requests:

```ts
export const getData = async (req: Request, res: Express_Response) => {};
```

In the `server.ts`, we add this request handler to the routing

```ts
app.get("/getData", getData);
```

So the requests to the `/getData`, with GET method, will be given to this function.

Then we read the expected parameters and send them to our API endpoint, after a simple validation.

```ts
const { lat, long } = req.query as { lat: string; long: string }; //? reading query parameters of the request.
if (!lat || !long)
  return res
    .status(400)
    .end("Latitude and longitude parameters are mandatory!"); //? simple validation of the parameters

const HTTPServiceInstance = new HTTPService(); // Instantiation of the HTTP Class
const result = await HTTPServiceInstance.fetchData(lat, long); // making the request using our defined HTTP service's method.
```

turning the API call's result into JSON, and creating a javascript object of the key/value pairs we need.

```ts
const json_data = await result.json();
const db_set = { ...json_data }; // make a copy of the response json.
delete db_set["timezone_abbreviation"]; // delete this key as we don't need it.
```

Then we create a connection to the MySQL server, using `mysql` package in Node.js:

```ts
const dev = false;
const conn = sql.createConnection({
  host: dev ? "localhost" : "mymysqldatabase.mysql.database.azure.com",
  user: dev ? "root" : "iman",
  password: dev ? "root" : "******",
  database: dev ? "distributed_apps" : "distributed_apps",
  port: 3306,
  ssl: {
    rejectUnauthorized: false,
  },
});
```

- With the `dev` variable, we control the options for setting up the connection for development or production use.

_Back to the HTTP request handler_, we use the mysql connection to save the data in our database:

```ts
conn.query("INSERT INTO api_results SET ?", db_set, (e, r, f) => {
  //This function is called when the query result is ready.
});
```

## DB INIT

Running the server in production might raise errors, as the database and tables are not yet created in the MySQL server.
Therefor, we define a file called `db.init.ts` in our [scripts folder](./src/scripts/db.init.ts), which is run on the production by us, before we start the main server, so it creates the required tables for us.

## CORS requests

As we are going to deploy front-end and back-end on 2 different servers, sending a HTTP request from one to the other, requires server to allow CORS.

In this example, we have managed it with the simplest approach, which is _allowing all CORS requests_.

`server.ts`:

```ts
app.use((req: Request, res: Response, next: NextFunction) => {
  res.set({
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Credentials": "true",
    "Access-Control-Allow-Headers":
      "Content-Type,language, User-Agent, X-Requested-With, Origin, Accept-Language",
    "Access-Control-Allow-Methods": "POST, GET, OPTIONS",
  });

  if (req.method === "OPTIONS") return res.status(200).end();
  next();
});
```

For every request coming to the server, we are telling the client, that we allow requests from any `origin`, with specified `headers` and only for the specified HTTP request methods.
`since we are not using credentials in our example, we could simply remove the allow credentials header.`

## Accessing Data to list the records

To see what data is stored in our database, we can connect to our MySQL server on azure using terminal commands:

`mysql -h AzureMySQLHostName -u iman -p`

by executing this command, it asks for password, if the hostname is valid, by entering the valid password, we will be in the MySQL cli environment,
we can see the data using
`USE distributed_apps;`
then:
`SELECT * FROM api_results`;
