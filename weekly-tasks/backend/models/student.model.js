//Abu Sadat Md. Soyam
//Matriket Nr: 1365263 
//Date : 07-11-2023

const mongoose = require('mongoose')

// mongoose model
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
