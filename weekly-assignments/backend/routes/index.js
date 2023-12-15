const express = require('express')
const { studentRouter } = require('./student.route')

const router = express.Router()

router.use('/students', studentRouter)

module.exports = {
	router
}
