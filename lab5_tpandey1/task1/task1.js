var express = require('express');
var jade = require('jade');
var bodyParser = require('body-parser');
var cookieParser = require('cookie-parser');
var session = require('express-session');

var app = express();
app.set('views', './views');
app.set('view engine', 'jade');
app.engine('jade', jade.__express);
app.use(bodyParser.urlencoded({
	extended : false
}));
app.use(bodyParser.json());
app.use(cookieParser());
app.use(function (req, res, next) {
	res.header('Cache-Control', 'no-cache');
	next();
});

app.use(session({secret: 'RESULTSEXPRESSKEY',
	resave: false,
    saveUninitialized: false
    }));

app.locals.failure = undefined;
app.locals.success = undefined;
app.locals.successMsg = {
	desc : 'Action successful!',
	count : ''
};
app.locals.failureMsg = {
	desc : 'Action failed, please try again'
};

app.locals.firstname = undefined;
app.locals.lastname = undefined;
app.locals.languages = undefined;
app.locals.days = undefined;
app.locals.contact = undefined;
app.locals.checkedLangsC = undefined;
app.locals.checkedLangsRuby = undefined;
app.locals.checkedLangsAda = undefined;
app.locals.checkedLangsJava = undefined;
app.locals.checkedLangsScala = undefined;
app.locals.checkedLangsPython = undefined;

app.locals.checkedDaysMon = undefined;
app.locals.checkedDaysTue = undefined;
app.locals.checkedDaysWed = undefined;
app.locals.checkedDaysThu = undefined;
app.locals.checkedDaysFri = undefined;
app.locals.checkedDaysSat = undefined;
app.locals.checkedDaysSun = undefined;

app.listen(8081);

var codersArray = [];

app.get('/', function(req, res) {	 
	console.log(req.cookies);
	if (!req.cookies.firstname && !req.session.visited){	  
		app.locals.firstname = undefined;
		app.locals.lastname = undefined;
		  
		app.locals.days = undefined;
		app.locals.contact = undefined;
		app.locals.checkedLangsC = undefined;
		app.locals.checkedLangsRuby = undefined;
		app.locals.checkedLangsAda = undefined;
		app.locals.checkedLangsJava = undefined;
		app.locals.checkedLangsScala = undefined;
		app.locals.checkedLangsPython = undefined;

		app.locals.checkedDaysMon = undefined;
		app.locals.checkedDaysTue = undefined;
		app.locals.checkedDaysWed = undefined;
		app.locals.checkedDaysThu = undefined;
		app.locals.checkedDaysFri = undefined;
		app.locals.checkedDaysSat = undefined;
		app.locals.checkedDaysSun = undefined;
		  
		res.render('webform1');
	  	
	  		//send error
	  } else if (!req.cookies.firstname && req.session.visited) { 
		
		  var myFirstname = req.session.myfirstname;
		  var srchQuery= {};
			
		  for (var i=0; i< codersArray.length; i++) {
			  if (codersArray[i].firstname === myFirstname) {
				  srchQuery.languages = codersArray[i].languages;
				  srchQuery.days = codersArray[i].days;
				  srchQuery.contact = codersArray[i].contact;
				  break;
				}
		  }
			
		  app.locals.myCodersArray = codersArray;
		  // Logic Begin
		  var filteredCodersArray = [];
		  var filteredResult = false;
		  if (JSON.stringify(srchQuery) !== JSON.stringify({}))
			  filteredResult = true;
		  var index = 0, value = '';
			
		  codersArray.forEach(function(item, index, array) {
			  var searchHit = 1;
			  for (field in srchQuery) {
				  if (myFirstname===item.firstname){
					  return;
				  }
				  if (item[field]!== undefined && item[field].constructor === Array) {
					  // handling multi valued params
					  var multiValueHit = 0;
					  var multiValueArray = srchQuery[field];
	
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
					  if (item[field]!== undefined && item[field].indexOf(srchQuery[field]) <= -1) {
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
			  //Filtering top 3 results only
			  if (filteredCodersArray.length > 3) {
				  filteredCodersArray = filteredCodersArray.splice(0, 3);
			  }
			  app.locals.myCodersArray = filteredCodersArray;
			  app.locals.firstname = myFirstname;

			  for (var i=0; i< codersArray.length; i++) {
				  if (codersArray[i].firstname === myFirstname) {
					  app.locals.lastname = codersArray[i].lastname;
				  }
			  }				
		  }
		  // Logic End
			
		  res.render('resultstop3');	
	  }
});

app.post('/post_coder1', function(req, res) {	
	if (req.body.firstname === '' || req.body.lastname === '') {
		app.locals.firstname =  undefined;
		app.locals.lastname =  undefined;
		app.locals.failure = true;
		app.locals.success = false;
		app.locals.failureMsg = 'Please enter the values for the below attribute(s)';
				
		res.render('webform1');
		//send error
	} else {
		app.locals.failure = false;
		app.locals.success = false;
		app.locals.failureMsg = undefined;
		
		req.session.visited = true;
		req.session.myfirstname = req.body.firstname;
		res.cookie('firstname', req.body.firstname, 
	            { maxAge: 60*60*1000, 
	              httpOnly: true, 
	              path:'/'});
		codersArray.push(req.body);
		
		var loopVar = 0;
		var myFirstname = req.body.firstname;
	
		for (var i=0; i< codersArray.length; i++) {
			if (codersArray[i].firstname === myFirstname) {
				codersArray[i].days = req.body.days;
				loopVar = i;
				break;
			}
		}
	
		var multiLangsArray = codersArray[loopVar].languages;
		console.log(multiLangsArray);
		if (multiLangsArray !== undefined  && multiLangsArray !== null){
			app.locals.checkedLangsC = multiLangsArray.indexOf("C") > -1 ? "1" : undefined;
			app.locals.checkedLangsRuby = multiLangsArray.indexOf("Ruby") > -1 ? "1" : undefined;
			app.locals.checkedLangsAda = multiLangsArray.indexOf("Ada") > -1 ? "1" : undefined;
			app.locals.checkedLangsJava = multiLangsArray.indexOf("Java") > -1 ? "1" : undefined;
			app.locals.checkedLangsScala = multiLangsArray.indexOf("Scala") > -1 ? "1" : undefined;
			app.locals.checkedLangsPython = multiLangsArray.indexOf("Python") > -1 ? "1" : undefined;	
		}
		
		res.render('webform2');	
	}
});


app.post('/post_coder2', function(req, res) {	
	if ((req.body.languages === '' || req.body.languages === undefined) && req.body.action !== 'Previous') {
		app.locals.checkedLangsC = undefined;
		app.locals.checkedLangsRuby = undefined;
		app.locals.checkedLangsAda = undefined;
		app.locals.checkedLangsJava = undefined;
		app.locals.checkedLangsScala = undefined;
		app.locals.checkedLangsPython = undefined;
		app.locals.failure = true;
		app.locals.success = false;
		app.locals.failureMsg = 'Please enter the values for the below attribute(s)';
			
		res.render('webform2');
		//send error
	} else {	
		app.locals.failure = false;
		app.locals.failureMsg = undefined;	
		
		var myFirstname = req.cookies.firstname;
		var inputValue = req.body.action;
		var loopVar = 0;
	
		for (var i=0; i< codersArray.length; i++) {
			if (codersArray[i].firstname === myFirstname) {
				codersArray[i].languages = req.body.languages;
				loopVar = i;
				break;
			}
		}
	
		if (inputValue == 'Previous') {
			app.locals.firstname = codersArray[loopVar].firstname;
			app.locals.lastname = codersArray[loopVar].lastname;
			res.render('webform1');
		}
	
		if (inputValue == 'Next') {			
			var multiDaysArray = codersArray[loopVar].days;
			if (multiDaysArray !== undefined && multiDaysArray !== null){
				app.locals.checkedDaysMon = multiDaysArray.indexOf("Monday") > -1 ? "1" : undefined;
				app.locals.checkedDaysTue = multiDaysArray.indexOf("Tuesday") > -1 ? "1" : undefined;
				app.locals.checkedDaysWed = multiDaysArray.indexOf("Wednesday") > -1 ? "1" : undefined;
				app.locals.checkedDaysThu = multiDaysArray.indexOf("Thursday") > -1 ? "1" : undefined;
				app.locals.checkedDaysFri = multiDaysArray.indexOf("Friday") > -1 ? "1" : undefined;
				app.locals.checkedDaysSat = multiDaysArray.indexOf("Saturday") > -1 ? "1" : undefined;
				app.locals.checkedDaysSun = multiDaysArray.indexOf("Sunday") > -1 ? "1" : undefined;	
			}				
		
		res.render('webform3');
		}
	}
});


app.post('/post_coder3', function(req, res) {	
	if ((req.body.days === '' || req.body.days === undefined) && req.body.action !== 'Previous') {
		app.locals.days =  undefined;
		app.locals.checkedDaysMon = undefined;
		app.locals.checkedDaysTue = undefined;
		app.locals.checkedDaysWed = undefined;
		app.locals.checkedDaysThu = undefined;
		app.locals.checkedDaysFri = undefined;
		app.locals.checkedDaysSat = undefined;
		app.locals.checkedDaysSun = undefined;
		app.locals.failure = true;
		app.locals.success = false;
		app.locals.failureMsg = 'Please enter the values for the below attribute(s)';
			
		res.render('webform3');
		//send error	
	} else {
		app.locals.failure = false;
		app.locals.failureMsg = undefined;	
	
		var myFirstname = req.cookies.firstname;
		inputValue = req.body.action;
		var loopVar = 0;
	
		for (var i=0; i< codersArray.length; i++) {
			if (codersArray[i].firstname === myFirstname) {
				codersArray[i].days = req.body.days;
				loopVar = i;
				break;
			}
		}
	
		if (inputValue == 'Previous') {
			var multiLangsArray = codersArray[loopVar].languages;
			if (multiLangsArray !== undefined && multiLangsArray !== null){
				app.locals.checkedLangsC = multiLangsArray.indexOf("C") > -1 ? "1" : undefined;
				app.locals.checkedLangsRuby = multiLangsArray.indexOf("Ruby") > -1 ? "1" : undefined;
				app.locals.checkedLangsAda = multiLangsArray.indexOf("Ada") > -1 ? "1" : undefined;
				app.locals.checkedLangsJava = multiLangsArray.indexOf("Java") > -1 ? "1" : undefined;
				app.locals.checkedLangsScala = multiLangsArray.indexOf("Scala") > -1 ? "1" : undefined;
				app.locals.checkedLangsPython = multiLangsArray.indexOf("Python") > -1 ? "1" : undefined;
			}
			res.render('webform2');
		}
	
		if (inputValue == 'Next') {
			res.render('webform4');
		}
	}
});


app.post('/post_coder4', function(req, res) {	
	if ((req.body.contact === '') && req.body.action !== 'Previous') {
		app.locals.contact =  undefined;
		app.locals.failure = true;
		app.locals.success = false;
		app.locals.failureMsg = 'Please enter the values for the below attribute(s)';
			
		res.render('webform4');
		//send error
	} else {
		app.locals.failure = false;
		app.locals.failureMsg = undefined;	
		
		var myFirstname = req.cookies.firstname;
		inputValue = req.body.action;
		
		for (var i=0; i< codersArray.length; i++) {
			if (codersArray[i].firstname === myFirstname) {
				codersArray[i].contact = req.body.contact;
				app.locals.firstname = codersArray[i].firstname;
				app.locals.lastname = codersArray[i].lastname;
				app.locals.languages = codersArray[i].languages;
				app.locals.days = codersArray[i].days;
				app.locals.contact = codersArray[i].contact;
				loopVar = i;
				break;
			}
		}
	
		if (inputValue == "Previous") {
			var multiDaysArray = codersArray[loopVar].days;
			if (multiDaysArray !== undefined && multiDaysArray !== null){
				app.locals.checkedDaysMon = multiDaysArray.indexOf("Monday") > -1 ? "1" : undefined;
				app.locals.checkedDaysTue = multiDaysArray.indexOf("Tuesday") > -1 ? "1" : undefined;
				app.locals.checkedDaysWed = multiDaysArray.indexOf("Wednesday") > -1 ? "1" : undefined;
				app.locals.checkedDaysThu = multiDaysArray.indexOf("Thursday") > -1 ? "1" : undefined;
				app.locals.checkedDaysFri = multiDaysArray.indexOf("Friday") > -1 ? "1" : undefined;
				app.locals.checkedDaysSat = multiDaysArray.indexOf("Saturday") > -1 ? "1" : undefined;
				app.locals.checkedDaysSun = multiDaysArray.indexOf("Sunday") > -1 ? "1" : undefined;
			}		
			res.render('webform3');
		}
		
		if (inputValue == "Next") {
			res.render('webform5');
		}
	}
});


app.post('/post_coder5', function(req, res) {	
	var myFirstname = req.cookies.firstname;
	inputValue = req.body.action;
	
	if (inputValue == "Previous") {
		res.render('webform4');
	}
	
	if (inputValue == "Submit") {
		app.locals.firstname = undefined;
		app.locals.lastname = undefined;		
		
		app.locals.checkedLangsC = undefined;
		app.locals.checkedLangsRuby = undefined;
		app.locals.checkedLangsAda = undefined;
		app.locals.checkedLangsJava = undefined;
		app.locals.checkedLangsScala = undefined;
		app.locals.checkedLangsPython = undefined;
		
		app.locals.checkedDaysMon = undefined;
		app.locals.checkedDaysTue = undefined;
		app.locals.checkedDaysWed = undefined;
		app.locals.checkedDaysThu = undefined;
		app.locals.checkedDaysFri = undefined;
		app.locals.checkedDaysSat = undefined;
		app.locals.checkedDaysSun = undefined;
		app.locals.contact = undefined;

		console.log(codersArray);
		res.clearCookie('firstname');
		res.render('webform1');
	}
	
	if (inputValue == "Cancel") {	
		for (var i=0; i< codersArray.length; i++) {
			if (codersArray[i].firstname === myFirstname) {
				codersArray.splice(i, 1);
				break;
			}
		}
		res.clearCookie('firstname');
		app.locals.firstname = undefined;
		app.locals.lastname = undefined;
		app.locals.checkedLangsC = undefined;
		app.locals.checkedLangsRuby = undefined;
		app.locals.checkedLangsAda = undefined;
		app.locals.checkedLangsJava = undefined;
		app.locals.checkedLangsScala = undefined;
		app.locals.checkedLangsPython = undefined;
		
		app.locals.checkedDaysMon = undefined;
		app.locals.checkedDaysTue = undefined;
		app.locals.checkedDaysWed = undefined;
		app.locals.checkedDaysThu = undefined;
		app.locals.checkedDaysFri = undefined;
		app.locals.checkedDaysSat = undefined;
		app.locals.checkedDaysSun = undefined;
		app.locals.contact = undefined;
		res.render('webform1');	
	}	
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

app.post('/', function(req, res) {
	res.status(405);
	   res.render('405.jade', {title:'Method Not Allowed'});
});

app.get('/post_coder1', function(req, res) {	
	res.status(405);
	   res.render('405.jade', {title:'Method Not Allowed'});
});

app.get('/post_coder2', function(req, res) {	
	res.status(405);
	   res.render('405.jade', {title:'Method Not Allowed'});
});

app.get('/post_coder3', function(req, res) {	
	res.status(405);
	   res.render('405.jade', {title:'Method Not Allowed'});
});

app.get('/post_coder4', function(req, res) {	
	res.status(405);
	   res.render('405.jade', {title:'Method Not Allowed'});
});

app.get('/post_coder5', function(req, res) {	
	res.status(405);
	   res.render('405.jade', {title:'Method Not Allowed'});
});


//Handle 404
app.use(function(req, res) {
    res.status(400);
   res.render('404.jade', {title: '404: File Not Found'});
});

// Handle 500
app.use(function(error, req, res, next) {
    res.status(500);
   res.render('500.jade', {title:'500: Internal Server Error', error: error});
});
