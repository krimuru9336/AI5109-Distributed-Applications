const mongoose = require('mongoose')

// mongoose model for Student
const StudentSchema = new mongoose.Schema({
	name: {
		type: String,
		required: true,
	},
	phone: {
		type: String,
		required: true,
	}
})

const Student = mongoose.model('Student', StudentSchema)

module.exports = { Student }
