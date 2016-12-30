var express = require('express');
var app = express();

var jsonMessage = '{ "status": "true", "message": "hello node.js" }';


app.get('/hello', function (req, res) {
	res.writeHead(200, {'Content-Type': 'text/plain'});

	res.end(jsonMessage);
});

var server = app.listen(3000, function () {
	var port = server.address().port;

	console.log("Node app listening at port %s", port);	
});
