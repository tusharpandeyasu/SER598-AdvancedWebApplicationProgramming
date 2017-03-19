// calcclient to go with calcserver.js
var sock = require('net').Socket();
sock.on('data', function(data) {
	console.log('Response: ' + data);
	sock.destroy(); // kill client after server's response
});
sock.on('close', function() {
	console.log('Connection closed');
});

sock.on('error', function() {
	console.log('Error connecting to server');
});

sock.connect(3000);
if (process.argv[2] === undefined) {
	console.log('Invalid request specification: Client ID missing');
	sock.destroy();
} else {
	var data = process.argv[2] + " " + process.argv[3] + " " + process.argv[4];
	sock.write(data);
}