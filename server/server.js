var express = require('express')
var multer = require('multer')
var bodyParser = require('body-parser')
var path = require('path')
var fs = require('fs')
var storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'images/');
    },
    filename: function(req, file, cb) {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});
var upload = multer({ storage: storage })
var app = new express();


var mongoose = require('mongoose');
var Schema = mongoose.Schema;
mongoose.connect('localhost', 'test');
var imgSchema = new Schema({
    name: String,
    url: String,
    last_update: Date
});

var Img = mongoose.model('image', imgSchema);
module.exports = Img;

app.use(bodyParser.json());

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

    var filePath = "./images/" + req.params.target;
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

