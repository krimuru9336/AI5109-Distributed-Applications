const express = require('express')

const { createStudentInfo, getAllStudentsInfo, deleteStudentInfo } = require('../controllers/student.controller')
const { Validator } = require('../middlewares/validator.middleware')
const { studentSchema } = require('../schemas/student.schema')

const router = express.Router()

// post route - to create student info
router.post('/', Validator(studentSchema), createStudentInfo)

// get route - to get students info
router.get('/', getAllStudentsInfo)

// delete route - to delete student info
router.delete('/:id', deleteStudentInfo)

module.exports = {
	studentRouter: router
}
