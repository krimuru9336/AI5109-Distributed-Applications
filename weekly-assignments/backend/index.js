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
mongoose.connect(process.env.DB_URL).then(
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

app.listen(port, '0.0.0.0', () => {
	console.log(`App listening on port ${port}`)
})
