'use strict';

const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const busboy = require('connect-busboy');

const passport = require('passport');
const JwtStrategy = require('passport-jwt').Strategy;
const ExtractJwt = require('passport-jwt').ExtractJwt;

const token = process.env.PASSPORT_SECRET;

const passportOpts = {
	jwtFromRequest: ExtractJwt.fromAuthHeader(),
	secretOrKey: token,
	ignoreExpiration: true
};

passport.use(new JwtStrategy(passportOpts, function(jwt_payload, done) {
	done(null, jwt_payload);
}));

app.set('port', (process.env.PORT || 3000));

app.get('/', function(req, res) {
	res.status(200).send('CrapMap Server is Running');
});

const bathroomController = require('./controller/bathroomcontroller');
const imageController = require('./controller/imagecontroller');
const userController = require('./controller/usercontroller');

app.use(bodyParser.json());

app.use(busboy({
	immediate: true
}));

// app.use(function(req, res, next) {
// 	const oldWrite = res.write;
// 	const oldEnd = res.end;

// 	var chunks = [];

// 	res.write = function(chunk) {
// 		chunks.push(chunk);

// 		oldWrite.apply(res, arguments);
// 	};

// 	res.end = function(chunk) {
// 		if (chunk)
// 			chunks.push(chunk);

// 		let body = Buffer.concat(chunks).toString('utf8');

// 		if (body.startsWith('{') || body.startsWith('[')) {
// 			body = JSON.parse(body);
// 		}

// 		oldEnd.apply(res, arguments);

// 		console.log(JSON.stringify({
// 			request: {
// 				route: req.url,
// 				method: req.method,
// 				body: req.body,
// 				params: req.params,
// 				queries: req.query,
// 			},
// 			response: {
// 				code: res.statusCode,
// 				body
// 			}
// 		}));
// 	};

// 	next();
// });

app.use('/bathroom', bathroomController);
app.use('/image', imageController);
app.use('/user', userController);

app.listen(app.get('port'), function() {
	console.log(`Listening on port ${app.get('port')}!`);
});
