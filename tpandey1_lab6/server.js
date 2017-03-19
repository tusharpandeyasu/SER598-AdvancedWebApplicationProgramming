
var express    = require('express');
var bodyParser = require('body-parser');
var app        = express();


app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port     = process.env.PORT || 8080; 
var mongoose   = require('mongoose');
mongoose.connect('mongodb://localhost/lab6db'); 
var Author     = require('./app/models/author');
var Book     = require('./app/models/book');


var router = express.Router();


router.use(function(req, res, next) {
	console.log('Processing.');
	next();
});

// test route to make sure everything is working (accessed at GET http://localhost:8080/api)
router.get('/', function(req, res) {
	res.json({ message: 'hooray! welcome to our api!', test : 'test' });	
});


router.route('/authors')

	
	.post(function(req, res) {
		
		var author = new Author();		

		author.firstname = req.query.firstname;
		author.lastname = req.query.lastname;
		
		author.save(function(err) {
			if (err)
				res.send(err);

			res.json({ message: 'Author created!' });
		});

		
	})

	
	.get(function(req, res) {
		Author.find(function(err, authors) {
			if (err)
				res.send(err);

			res.json(authors);
		});
	});


router.route('/authors/:authorid')

	.get(function(req, res) {
		Author.findById(req.params.authorid, function(err, author) {
			if (err)
				res.send(err);
			res.json(author);
		});
	})

	.put(function(req, res) {
		Author.findById(req.params.authorid, function(err, author) {

			if (err)
				res.send(err);
			
			author.firstname = req.query.firstname;
			author.lastname = req.query.lastname;
			
			author.save(function(err) {
				if (err)
					res.send(err);

				res.json({ message: 'Author updated!' });
			});

		});
	})


	.delete(function(req, res) {
		Book.find({
		    authorids: { $in: [req.params.authorid] }
		  }, function(err, books) {
				if (err)
					res.send(err);
				console.log(books);
				if (books === null || books === undefined || books.length === 0){
					
					Author.remove({
						_id: req.params.authorid
					}, function(err, author) {
						if (err)
							res.send(err);

						
						res.json({ message: 'Successfully deleted' });
					});
				} else{
					res.json({ message: 'Author has atleast one book associated with it' });					
				}
				
			});
				
	});


router.route('/books')

	.post(function(req, res) {
		
		var book = new Book();		

		book.title = req.query.title;
		book.publisher = req.query.publisher;
		book.year = req.query.year;
		book.authorids = [req.query.authorid];
		
		
		book.save(function(err) {
			if (err)
				res.send(err);

			res.json({ message: 'Book created!' });
		});

		
	})

	.get(function(req, res) {
		Book.find(function(err, books) {
			if (err)
				res.send(err);

			res.json(books);
		});
	});



router.route('/books/:bookid')

	
	.get(function(req, res) {
		Book.findById(req.params.bookid, function(err, book) {
			if (err)
				res.send(err);
			res.json(book);
		});
	})

	.put(function(req, res) {
		Book.findById(req.params.bookid, function(err, book) {

			if (err)
				res.send(err);

//			author.name = req.body.name;
			
			
			book.title = req.query.title;
			book.publisher = req.query.publisher;
			book.year = req.query.year;
			book.authorids.push(req.query.authorid);
			
			book.save(function(err) {
				if (err)
					res.send(err);

				res.json({ message: 'Book updated!' });
			});

		});
	})


	.delete(function(req, res) {
		Book.remove({
			_id: req.params.bookid
		}, function(err, book) {
			if (err)
				res.send(err);

			res.json({ message: 'Successfully deleted' });
		});
	});



///for /title

router.route('/books/title/:title')

.get(function(req, res) {
	Book.find({
	    title: req.params.title
	  }, function(err, books) {
			if (err)
				res.send(err);
			res.json(books);	
			
		});

})


app.use('/api', router);


app.listen(port);
console.log('Connected to port ' + port);
