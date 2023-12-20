import bodyParser from "body-parser";
import cors from "cors";
import dotenv from "dotenv";
import express from "express";
import { labDaRoutes } from "./routes/index.js";

// DB Connections
import mysql from 'mysql'

export var con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "6jubwe32",
  port: 3306
});

con.connect(function (err) {
  if (err) throw err;
  console.log("Connected!");
});

dotenv.config();

const PORT = process.env.PORT || 8080;
const app = express();


app.use(cors());
app.use(bodyParser.json());


app.use("/", labDaRoutes);


app.listen(PORT, () => console.log(`Server listening to port ${PORT}`));
