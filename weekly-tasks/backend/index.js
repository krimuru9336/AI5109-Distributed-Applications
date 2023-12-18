//Abu Sadat Md. Soyam
//Matriket Nr: 1365263 
//Date : 07-11-2023

require('dotenv').config()
const express = require('express')
const cors = require('cors')
const  mongoose = require('mongoose')
const { router } = require('./routes')

// initialize express application
const app = express()

// port define
const port = process.env.PORT || 3000

// database connection - mongodb
mongoose.connect(process.env.DATABASE_URL).then(
	() => {
		console.log('DB connected!')
	},
	(err) => {
		console.log('Connection error', err)
	}
)

// setup the routes, cors for cross browser platform support
app.use(cors())
app.use(express.json())
app.use('/', router)

app.listen(port, () => {
	console.log(`App listening on port ${port}`)
})
