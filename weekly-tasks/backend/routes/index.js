//Abu Sadat Md. Soyam
//Matriket Nr: 1365263 
//Date : 07-11-2023

const express = require('express')
const { studentRouter } = require('./student.route')

const router = express.Router()

router.use('/tasks', studentRouter)

module.exports = {
	router
}
