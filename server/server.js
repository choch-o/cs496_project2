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
	userID: String,
	userName: String,
    profileURL: String,
    awake: Boolean,
    phone: String,
    keycode: Number,
	counterpart: [{ type: Schema.ObjectId, ref: 'User' }],
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
    fbid: String,
    fbname: String
});


var User = mongoose.model('User', userSchema);
var Contact = mongoose.model('Contact', contactSchema);
var Img = mongoose.model('image', imgSchema);
module.exports = Img;

app.use(bodyParser.json());

app.get('/remove_contacts', function(req, res) {
	Contact.remove({}, function(err, removed) {
		console.log("Removed: " + removed);
	});
    var responseBody = {
        result: 'OK',
    }

    res.writeHead(200, {'Content-Type':'application/json'});
    res.write(JSON.stringify(responseBody));
    res.end();
});

// POST request for contacts
app.post('/upload_contacts', function(req, res) {
	for (var i = 0; i < req.body.length; i++) {
		var newContact = new Contact({
			name: req.body[i]['name'],
			picture: req.body[i]['picture'],
			phone_number: req.body[i]['phone_number'],
			is_facebook: req.body[i]['is_facebook'],
			user: req.body[i]['user']
		});	
		newContact.save(function (err) {
			if (err) throw err;
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

    // Add to database
    var newImg = Img({
        name: req.headers['name'],
        url: req.file.destination + req.file.filename,
        fbid: req.headers['fbid'],
        fbname: req.headers['fbname']
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


///// Alarm Clock /////

app.get('/check_me/:userID', function(req, res) {
    // User : "Is there my userID in the database?"
    console.log("[Alarm] Got something on GET/check_me");

    console.log("[Alarm] " + req.params.userID)
    User.find({userID : req.params.userID}, function(err, results) {
        if (err) {
            console.log("[Alarm/Get/check_me] Something goes wrong :(");
            throw err;
        }
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify({result:results.length}));
        res.end();
    });
});

app.post('/enroll_me', function(req, res) {
    // User : "I'll give you the information and profile picture."
    // TODO : Add user to the database
    console.log("[Alarm] Got something on POST/enroll_me");
    console.log(req.body);
    var newUser = User({
        userID: req.body['userID'],
        userName: req.body['userName'],
        profileURL: req.body['profileURL'],
        awake: true,
        phone: req.body['phone'],
        keycode: req.body['keycode'],
        counterpart: null
    });

    newUser.save(function(err) {
        if(err) throw err;
        console.log("[Alarm/POST/enroll_me] User saved");
        User.find({}, function(err, results) {
            if(err) throw err;
			rebuildMap(results);
        })
    })

    res.writeHead(200, {'Content-Type':'application/json'});
    res.write(JSON.stringify({result: 'OK'}));
    res.end();
});

function rebuildMap(results) {
	shuffle(results);
	connectMap(results, 0);
}

function connectMap(results, i) {
	if(i==results.length) {
		return;
	}
	curr = i;
	next = (i+1)%results.length;
	User.findOne({userID: results[next]['userID']}, {_id:1, userID:1}, function(err, result) {
		User.findOneAndUpdate({userID: results[curr]['userID']}, {counterpart: result["_id"]},
								function(err, res) {
										if (err) throw err;
										connectMap(results, i+1);
									})});
}

function shuffle(a) {
    for (let i = a.length; i; i--) {
        let j = Math.floor(Math.random() * i);
        [a[i - 1], a[j]] = [a[j], a[i - 1]];
    }
}

app.get('/profile_list', function(req, res) {
    // User : "Give me the full list of profile
    console.log("[Alarm/GET/profile_list] Something happened");

    User.find({}, function(err, results) {
        if (err) throw err;

        var responseBody = results;
        res.write(JSON.stringify(responseBody));
        res.end();

		// Uncomment this, if rebuild the map everytime user reload the status
        //rebuildMap(results);
    });
});

app.get('/profile_counter/:userID', function(req, res) {
    console.log("[Alarm] Got something on GET/profile_counter");
    User.findOne({userID : req.params.userID}, {counterpart:1},
            function(err, result) {
                User.findOne({_id : result['counterpart']},
                        function(err, counterresult) {
                            res.writeHead(200, {'Content-Type':'application/json'});
                            res.write(JSON.stringify(counterresult));
                            res.end();
                        })
            });
});


// Give an image in request
app.get('/profile_image/:target', function(req, res) {
    // User : "Give me the picture whose url is 'target'."
    // pictures will be fetched from the web (url provieded by facebook)
});

app.get('/good/:command/:userID', function(req, res) {
    // User : "Set me as a sleeping status."
    console.log("[Alarm] Got something on GET/good"+req.params.command);
    console.log("[Alarm] " + req.params.userID);

    var awake = true;
    if(req.params.command == "night") {
        awake = false; }

    User.findOneAndUpdate({userID : req.params.userID}, {awake : awake},
            function(err, results) {
        if (err) {
            console.log("[Alarm/GET/goodnight] Something goes wrong :(");
            throw err;
        }
        res.writeHead(200, {'Content-Type':'application/json'});
        res.write(JSON.stringify({result:results.length}));
        res.end();
    });
});


var alarmTime = 0;
app.post('/set_time', function(req, res) {
    reqTime = req.body[0];

    var flag = true;
    User.find({}, function(err, results) {
        if (err) throw err;

        for (i=0 ; i<results.length ; i++) {
            flag = flag & results[i]['awake'];
        }

        if (flag) {
            alarmTime = reqTime;
            res.writeHead(200, {'Content-Type': 'application/json'});
            res.write(JSON.stringify({result: 'OK', time: alarmTime}));
        } else {
            res.writeHead(409, {'Content-Type': 'application/json'});
            res.write(JSON.stringify({result: 'Not changed', time: alarmTime}));
        }
        res.end();
    });
   
	console.log("alarm time", alarmTime);
});

app.get('/get_time', function(req, res) {
	res.write(JSON.stringify({result: 'OK', time: alarmTime}));
	res.end();
	console.log("alarm time", alarmTime);
}); 

app.get('/get_keycode/:userID', function(req, res) {

});

app.listen(3000, function() { console.log("Listening on port #3000" )});

