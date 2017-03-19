var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var AuthorSchema   = new Schema({
	firstname: String,
	lastname: String
	
});

module.exports = mongoose.model('Author', AuthorSchema);