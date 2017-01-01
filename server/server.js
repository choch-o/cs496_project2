var express = require('express')
var multer = require('multer')
var bodyParser = require('body-parser')
var path = require('path')
var fs = require('fs')
var storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'image/');
    },
    filename: function(req, file, cb) {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});
var upload = multer({ storage: storage })
var app = new express();


var mongoose = require('mongoose');
mongoose.connect('localhost', 'test');

/* MongoDB SCHEMA */ 
var Schema = mongoose.Schema;

// User schema
var userSchema = new Schema({
	userId: String,
	name: String,
	contacts: [{ type: Schema.ObjectId, ref: 'Contact' }],
	image: [{ type: Schema.ObjectId, ref: 'Img' }]
});

// Contacts Schema
var contactSchema = new Schema({
	name: String,
	picture: {
		"data": {
			"is_silouette": Boolean,
			"url": String
		}
	},
	id: String,
	phone_number: String,
	is_facebook: Boolean,
	last_update: Date,
	user: String
});
// Image Schema
var imgSchema = new Schema({
    name: String,
    url: String,
    last_update: Date
});

var User = mongoose.model('User', userSchema);
var Contact = mongoose.model('Contact', contactSchema);
var Img = mongoose.model('image', imgSchema);
module.exports = Img;

app.use(bodyParser.json());

// POST request for contacts
app.post('/upload_contacts', function(req, res) {
	console.log("Contacts POST request");
	Contact.remove({}, function(err, removed) {
		console.log("Removed: " + removed);
	});
	for (var i = 0; i < req.body.length; i++) {
		var newContact = new Contact({
			name: req.body[i]['name'],
			picture: req.body[i]['picture'],
			phone_number: req.body[i]['phone_number'],
			is_facebook: req.body[i]['is_facebook'],
			last_update: req.body[i]['last_update'],
			user: req.body[i]['user']
		});	
		newContact.save(function (err) {
			if (err) throw err;
			console.log("Image saved to DB!");
		});
	}
    // Response
    var responseBody = {
        result: 'OK',
    }

    res.writeHead(200, {'Content-Type':'application/json'});
    res.write(JSON.stringify(responseBody));
    res.end();
});

// Give whole image list
app.get('/image_list', function(req, res) {
    console.log("[*] Got something on GET(image_list)");

    Img.find({}, function(err, imgs) {
        if (err) throw err;

        var responseBody = imgs;
        res.write(JSON.stringify(responseBody));
        res.end();
    });

});


// Give an image in request
app.get('/image/:target', function(req, res) {
    console.log("[*] Got something on GET(image)");
    console.log("   Request on " + req.params.target);

    var filePath = "./image/" + req.params.target;
    var stat = fs.statSync(filePath);

    res.writeHead(200, {'Content-Type':'image', 'Content-Length':stat.size});

    var readStream = fs.createReadStream(filePath);
    readStream.pipe(res);

});

app.post('/image', upload.single('userFile'), function (req, res) {
    // Dealing with the request
    console.log("[*] Got Something on POST");
    console.log(req.file);
    console.log(req.headers['name'] + " ## " + req.headers['last_update']);

    // Add to database
    // TODO : Need to check duplicate
    var newImg = Img({
        name: req.headers['name'],
        url: req.file.destination + req.file.filename,
        last_update: req.headers['last_update']
    });

    newImg.save(function(err) {
        if(err) throw err;
        console.log('Image Saved to DB!');
    });

    // Response
    var responseBody = {
        result: 'OK'
    }

    res.writeHead(200, {'Content-Type':'application/json'});
    res.write(JSON.stringify(responseBody));
    res.end();
});

app.listen(3000, function() { console.log("Listening on port #3000" )});

