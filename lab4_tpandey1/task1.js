var http = require('http') // http module
var qs = require('querystring'); // querystring parser
var url = require('url');

var codersArray = [];
var bg_color = 'white';
// create the http server
http.createServer(
		function(req, res) {

			// handle the routes
			if (req.method == 'POST') {

				var urlObj = url.parse(req.url, true, false);
				if (urlObj.pathname == '/post_coder') {
					var formData = '';
					var formDataObject = undefined;
					req.on('data', function(data) {
						formData += data;
					});
					req.on('end', function() {
						formDataObject = qs.parse(formData);
						if (formDataObject.firstname === '' &&
								formDataObject.lastname === '' &&
								formDataObject.contact === '' &&
								formDataObject.languages === undefined &&
								formDataObject.days === undefined) {
							res.end(htmlResHeaderString + bodyHtmlStringStart + bg_color + ">" + failureMessageHtml
									+ "<p>Total number of entries in array: "
									+ codersArray.length + "</p><br />"
									+ webFormHtmlString);
						} else {
							
							codersArray.push(formDataObject);
							res.writeHead(200, {
								'Content-Type' : 'text/html',
								'Cache-Control': 'no-cache'	
							});
							bg_color = 'white';
	
							res.end(htmlResHeaderString + bodyHtmlStringStart + bg_color + ">" + successMessageHtml
									+ "<p>Total number of entries in array: "
									+ codersArray.length + "</p><br />"
									+ webFormHtmlString);
						}
					});
					req.on('error', function(err) {
						console.log(err);
						res.writeHead(200, {
							'Content-Type' : 'text/html',
							'Cache-Control': 'no-cache'	
						});
						bg_color = 'white';

						res.end(htmlResHeaderString + bodyHtmlStringStart + bg_color + ">" + failureMessageHtml
								+ "<p>Total number of entries in array: "
								+ codersArray.length + "</p><br />"
								+ webFormHtmlString);
					});
				}

			} else {

				// for GET requests
				var urlObj = url.parse(req.url, true, false);
				var filteredResult = false;
				if (urlObj.pathname == '/') {
					res.writeHead(200, {
						'Content-Type' : 'text/html',
						'Cache-Control': 'no-cache'						
					});
					bg_color = 'white';
					res.end(htmlHeaderString +  bodyHtmlStringStart + bg_color + ">" + webFormHtmlString);
				} else if (urlObj.pathname == '/coders') {
					if (req.headers['user-agent'].indexOf('Chrome') > -1) {
						bg_color = 'pink';
					} else {
						bg_color = 'white';
					}
						
					var filteredCodersArray = [];
					if (JSON.stringify(urlObj.query) !== JSON.stringify({}))
						filteredResult = true;
					var index = 0, value = '';
					
					codersArray.forEach(function(item, index, array) {
						var searchHit = 1;
						for (field in urlObj.query) {
							
							if (item[field]!== undefined && item[field].constructor === Array) {
								// handling multi valued params
								var multiValueHit = 0;
								var multiValueArray = urlObj.query[field].split(" ");
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
								//handling single valued params
								if (item[field]!== undefined && item[field].indexOf(urlObj.query[field]) <= -1) {
									searchHit = 0;
									break;
								}
							}
						}
						if (searchHit == 1) {
							filteredCodersArray.push(item);
						}

					});
					
					res.writeHead(200, {
						'Content-Type' : 'text/html',
						'Cache-Control': 'no-cache'	
					});
					res.end(htmlHeaderString +  bodyHtmlStringStart + bg_color + ">"
							+ getCodersArrayAsHtml(filteredResult == true ? filteredCodersArray : codersArray)
							+ '</body></html>');
				} else {
					res.writeHead(404, {
						'Content-Type' : 'text/html',
						'Cache-Control': 'no-cache'
					});
					res.end();
				}
			}

		}).listen(8081);

function getCodersArrayAsHtml(myCodersArray) {
	var outputString = "<h1>Results:</h1><br />";
	myCodersArray.forEach(function(item, index, array) {
		outputString += 'Result#' + ++index + ' - ';
		outputString += 'Firstname: ' + item.firstname;
		outputString += ', Lastname: ' + item.lastname;
		outputString += ', Languages: ' + item.languages;
		outputString += ', Days: ' + item.days;
		outputString += ', Contact: ' + item.contact;
		outputString += '<br><br>';
	});
	return outputString;
}

var htmlHeaderString = "";
htmlHeaderString += "<!DOCTYPE html>";
htmlHeaderString += "<html>";
htmlHeaderString += "<head>";
htmlHeaderString += "<title>Web Form<\/title>";
htmlHeaderString += "<\/head>";

var htmlResHeaderString = "";
htmlResHeaderString += "<!DOCTYPE html>";
htmlResHeaderString += "<html>";
htmlResHeaderString += "<head>";
htmlResHeaderString += "<title>Results<\/title>";
htmlResHeaderString += "<\/head>";

var bodyHtmlStringStart = "<body bgcolor=";

var webFormHtmlString = "";
webFormHtmlString += "<form action=\"post_coder\" method=\"POST\">";
webFormHtmlString += "  First name:";
webFormHtmlString += "  <input type=\"text\" name=\"firstname\"><br><br>";
webFormHtmlString += "  Last name: ";
webFormHtmlString += "  <input type=\"text\" name=\"lastname\"><br><br>";
webFormHtmlString += "";
webFormHtmlString += "  Programming Languages Known:<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"languages\" value=\"C\"> C<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"languages\" value=\"Ruby\"> Ruby<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"languages\" value=\"Ada\"> Ada<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"languages\" value=\"Java\"> Java<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"languages\" value=\"Scala\"> Scala<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"languages\" value=\"Python\"> Python<br><br>";
webFormHtmlString += "";
webFormHtmlString += "  Available on Day(s) of the week:<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"days\" value=\"Monday\"> Monday<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"days\" value=\"Tuesday\"> Tuesday<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"days\" value=\"Wednesday\"> Wednesday<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"days\" value=\"Thursday\"> Thursday<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"days\" value=\"Friday\"> Friday<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"days\" value=\"Saturday\"> Saturday<br>";
webFormHtmlString += "  <input type=\"checkbox\" name=\"days\" value=\"Sunday\"> Sunday<br><br>";
webFormHtmlString += "";
webFormHtmlString += "  Contact Number:";
webFormHtmlString += "  <input type=\"text\" name=\"contact\"><br><br>";
webFormHtmlString += "";
webFormHtmlString += "  <input type=\"submit\" value=\"Submit\">";
webFormHtmlString += "<\/form>";
webFormHtmlString += "";
webFormHtmlString += "";
webFormHtmlString += "<\/body>";
webFormHtmlString += "<\/html>";
webFormHtmlString += "";

var successMessageHtml = "<p>The action was successful!</p>";
var failureMessageHtml = "<p>The action was not successful. Please try again.</p>";