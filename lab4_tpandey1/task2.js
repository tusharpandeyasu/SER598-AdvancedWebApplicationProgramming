var express = require('express');
var jade = require('jade');
var bodyParser = require('body-parser');

var app = express();
app.set('views', './views');
app.set('view engine', 'jade');
app.engine('jade', jade.__express);
app.use(bodyParser.urlencoded({
	extended : false
}));
app.use(bodyParser.json());
app.use(function (req, res, next) {
	res.header('Cache-Control', 'no-cache');
	next();
});

app.locals.failure = undefined;
app.locals.success = undefined;
app.locals.successMsg = {
	desc : 'Action successful!',
	count : ''
};
app.locals.failureMsg = {
	desc : 'Action failed, please try again'
};

app.listen(8081);

var codersArray = [];

app.get('/', function(req, res) {
	app.locals.success = undefined;
	app.locals.failure = undefined;
	res.render('webform');
});

app.post('/post_coder', function(req, res) {
	
	if (req.body.firstname === '' &&
			req.body.lastname === '' &&
			req.body.contact === '' &&
			req.body.languages === undefined &&
			req.body.days === undefined) {
		app.locals.failure = true;
		app.locals.success = false;
		app.locals.successMsg.count = 'Total number of entries in array: ' + codersArray.length;
	} else {
	codersArray.push(req.body);
	app.locals.success = true;
	app.locals.failure = false;
	app.locals.successMsg.count = 'Total number of entries in array: ' + codersArray.length;
}
	res.render('webform');
});

app.get('/coders', function(req, res) {
	app.locals.myCodersArray = codersArray;
	if (req.headers['user-agent'].indexOf('Chrome') > -1) {
		app.locals.mybgcolor = 'pink';
	} else {
		app.locals.mybgcolor = 'white';
	}
	// Logic Begin
	var filteredCodersArray = [];
	var filteredResult = false;
	if (JSON.stringify(req.query) !== JSON.stringify({}))
		filteredResult = true;
	var index = 0, value = '';
	codersArray.forEach(function(item, index, array) {
		var searchHit = 1;
		for (field in req.query) {
			
			if (item[field]!== undefined && item[field].constructor === Array) {
				// handling multi valued params
				var multiValueHit = 0;
				var multiValueArray = req.query[field].split(" ");
				for (index = 0; index < item[field].length; index++) {
					value = (item[field])[index];
					if (multiValueArray.indexOf(value) > -1) {
						multiValueHit = 1;
					}
				}
				if (multiValueHit == 0) {
					searchHit = 0;
					break;
				}
			} else {
				// handling single valued params
				if (item[field]!== undefined && item[field].indexOf(req.query[field]) <= -1) {
					searchHit = 0;
					break;
				}
			}
		}
		if (searchHit == 1) {
			filteredCodersArray.push(item);
		}

	});
	
	if (filteredResult == true) {
		app.locals.myCodersArray = filteredCodersArray;
	}

	// Logic End
	res.render('results');
});

app.get('/get_coder/firstname/:name', function (req, res) {
	var myFirstname = req.params.name;
	var myCoder = undefined;
	var filteredCodersArray = [];
	codersArray.forEach(function(item, index, array) {
		if (item.firstname.indexOf(myFirstname) > -1) {
			filteredCodersArray.push(item);
		}
	});
	app.locals.myCodersArray = filteredCodersArray;
	res.render('results');
});


app.get('/get_coder/lastname/:name', function (req, res) {
	var myLastname = req.params.name;
	var myCoder = undefined;
	var filteredCodersArray = [];
	codersArray.forEach(function(item, index, array) {
		if (item.lastname.indexOf(myLastname) > -1) {
			filteredCodersArray.push(item);
		}
	});
	app.locals.myCodersArray = filteredCodersArray;
	res.render('results');
});


app.use(function(err, req, res, next) {
	console.log("Error!");
	console.log(err);
	app.locals.success = false;
	app.locals.failure = true;
	res.render('webform');
});