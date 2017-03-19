// calcserver.js
var net = require('net');		
net.createServer(function (sock) {
    console.log("Incoming connection accepted");
    sock.on('data', function (data) {
    	console.log("Received from client: " + data);
        var s = data.toString();
        var res = s.split(" ");       
        var clientId = res[0];						
        var cmd = res[1];                        
        var value = res[2];
            	
        if (clientId === "ASU") {
        	//If client id received is “ASU” then the cmd should only happen after a delay of 30 seconds
        	setTimeout(processing, 30000);
        } else if (clientId === "UA") {
        	//If client id received is “UA” then the cmd should happen immediately, before any other work on the event queue
        	process.nextTick(processing);
        } else if (clientId === "NAU") {
        	// If client id received is “NAU” then the cmd should execute normally and a custom event should be emitted of
        	// type ‘lumberjack’.
        	var events = require('events');
        	var eventEmitter = new events.EventEmitter();

        	var ljack = function ljack()
        	{
        	  console.log('I saw a lumberjack!');
        	}
        	eventEmitter.on('lumberjack', ljack);
        	processing();
        	eventEmitter.emit('lumberjack');
        	
        } else {
        	processing();
        }

        function processing() {
        	//Processing
        	//Check for Invalid request specification 
            if (cmd!= 'a' &&  cmd!= 'm' && cmd!= 's' && cmd!= 'q') {
            	console.log('Invalid request specification: Invalid command');
            	sock.write("Invalid request specification: Invalid command", function() {
                console.log("Finished response to client");
                sock.destroy();
            	})
            
            } else if (isNaN(value) && cmd != 'q'){
            	console.log('Invalid request specification: Value is not number');
            	sock.write("Invalid request specification: Value is not number", function() {
                	console.log("Finished response to client");
                	sock.destroy();
                })
            } else {
          	        	
            if (runningTotal[clientId] == undefined) {
            	
            	switch(cmd){
            	case 'a':
            		runningTotal[clientId] =  parseInt(value);
            		console.log("Client ID: "+ clientId + "  " + "Running Total: " +runningTotal[clientId]);
            		break;
            		
            	case 'm':
            		runningTotal[clientId] = - parseInt(value);
            		console.log("Client ID: "+ clientId + "  " + "Running Total: " +runningTotal[clientId]);
            		break;
            	
            	case 's':
            		runningTotal[clientId] = parseInt(value);
            		console.log("Client ID: "+ clientId + "  " + "Running Total: " +runningTotal[clientId]);
            		break;
            		
            	case 'q':
            		runningTotal[clientId]=0;
                    sock.write(""+runningTotal[clientId], function() {
                    	var output = '';
                    	for (clientId in runningTotal) {
                    	    output += 'Dumping Client ID: ' + clientId + '  ' + 'Running Total: ' + runningTotal[clientId]+'; ' +'\n';
                    	}
                      	console.log(output);
                            	
                    	sock.destroy();           
                		process.exit(0);
                    })
            		break;        		
            	}
            	
            } else {
            	//switch a, s, m
            	
            	switch(cmd){
            	case 'a':
            		runningTotal[clientId] = parseInt(runningTotal[clientId]) + parseInt(value);
            		console.log("Client ID: "+ clientId + "  " + "Running Total: " +runningTotal[clientId]);
            		break;
            		
            	case 'm':
            		runningTotal[clientId] = parseInt(runningTotal[clientId]) - parseInt(value);
            		console.log("Client ID: "+ clientId + "  " + "Running Total: " +runningTotal[clientId]);
            		break;
            	
            	case 's':
            		runningTotal[clientId] = parseInt(value);
            		console.log("Client ID: "+ clientId + "  " + "Running Total: " +runningTotal[clientId]);
            		break;
            		
            	case 'q':
                    sock.write(""+runningTotal[clientId], function() {
                    	var output = '';
                    	for (clientId in runningTotal) {
                    	    output += 'Dumping Client ID: ' + clientId + '  ' + 'Running Total: ' + runningTotal[clientId]+'; ' +'\n';
                    	}
                      	console.log(output);
                    	
                    	sock.destroy();
                    	process.exit(0);                        
                    })
            		break;        
            	}
            }
        	sock.write(""+runningTotal[clientId], function() {
            	console.log("Finished response to client");
            })
            }
        	
        	//Processing
        }	
    }).on('error', function (e) {
	console.log("Some kind of server error"+e);
    });
}).listen(3000);

var runningTotal = {
};