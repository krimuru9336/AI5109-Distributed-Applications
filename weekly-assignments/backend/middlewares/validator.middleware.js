// validator middleware to validate student info while creating
function Validator(schema) {
	return function(req, res, next) {
		const validationResult = schema.validate(req.body)

		if(validationResult.error) {
			return res.status(400).json(
				validationResult.error.details[0]
			)
		}
		next()
	}
}

module.exports = {
	Validator
}
