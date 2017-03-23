'use strict';

const express = require('express');
const router = express.Router();
const haversine = require('haversine');
const passport = require('passport');

const userService = require('../service/userservice');
const bathroomService = require('../service/bathroomservice');
const cloudinaryService = require('../service/cloudinaryservice');

let validDays = ['sun', 'mon', 'tues', 'wed', 'thurs', 'fri', 'sun'];

function pointIsWithinRadius(start, end, radius) {
	const distance = haversine(start, end, {
		unit: 'meter'
	});

	return distance <= radius;
}

function optionalAuth(req, res, next) {
	passport.authenticate('jwt', function(err, user) {
		req.user = user;
		next();
	})(req, res, next);
}

router.get('/', function(req, res) {
	bathroomService.getAllBathrooms().then(function(bathrooms) {
		let lat, long;
		if (req.query.lat) {
			if (isNaN(req.query.lat)) {
				return res.status(400).send('Bad Request query param lat must be a number');
			}
			if (!req.query.long) {
				return res.status(400).send('Bad Request query param lat requires query param long');
			}
			lat = req.query.lat;
		}

		if (req.query.long) {
			if (isNaN(req.query.long)) {
				return res.status(400).send('Bad Request query param long must be a number');
			}
			if (!req.query.lat) {
				return res.status(400).send('Bad Request query param long requires query param lat');
			}
			long = req.query.long;
		}

		if (req.query.radius && isNaN(req.query.radius)) {
			return res.status(400).send('Bad Request query param radius must be a number');
		}

		const radius = req.query.radius || 10000;

		if (lat && long) {
			bathrooms = bathrooms.filter(function(bathroom) {
				return pointIsWithinRadius({
					latitude: lat,
					longitude: long
				}, {
					latitude: bathroom.location.lat,
					longitude: bathroom.location.long
				}, radius);
			});
		}

		res.send(bathrooms);
	});
});

router.post('/', function(req, res) {
	if (!req.body.name) {
		return res.status(400).send('Bad Request body requires name');
	}

	if (!req.body.location || !req.body.location.lat || !req.body.location.long) {
		return res.status(400).send('Bad Request body requires location.lat and location.long');
	}

	if (req.body.requiresPurchase == undefined) {
		return res.status(400).send('Bad Request body requires boolean requiresPurchase');
	}

	if (req.body.hours) {
		let error = req.body.hours.find(hour => {
			let validDay = hour.day && typeof hour.day === 'string' && validDays.includes(hour.day);
			let validOpen = hour.open && !isNaN(hour.open);
			let validClose = hour.close && !isNaN(hour.close);
			return !(validDay && validOpen && validClose);
		});
		if (error) {
			return res.status(400).send('Bad Request hours items require string day, numbers open and close');
		}
	}

	bathroomService.insertBathroom(req.body).then(function(result) {
		return bathroomService.getBathroom(result.insertedId);
	}).then(function(doc) {
		res.send(doc);
	});
});

router.delete('/:id', function(req, res) {
	bathroomService.deleteBathroom(req.params.id).then(function() {
		res.sendStatus(200);
	});
});

router.post('/:id/review', optionalAuth, function(req, res) {
	if (!req.body.stars) {
		return res.status(400).send('Bad Request body requires stars object');
	}
	if (!req.body.stars.cleanliness || !Number.isInteger(req.body.stars.cleanliness)) {
		return res.status(400).send('Bad Request stars object requires cleanliness of type integer');
	}
	if (!req.body.stars.accessibility || !Number.isInteger(req.body.stars.accessibility)) {
		return res.status(400).send('Bad Request stars object requires accessibility of type integer');
	}
	if (!req.body.stars.availability || !Number.isInteger(req.body.stars.availability)) {
		return res.status(400).send('Bad Request stars object requires availability of type integer');
	}

	let promise = Promise.resolve(req.body);

	if (req.user) {
		promise = userService.getByUsername(req.user.username).then(user => {
			if (user) {
				delete user.password;
				req.body.user = user;
			}
			return req.body;
		});
	}

	promise.then(body => {
		return bathroomService.insertReview(req.params.id, body);
	}).then(function(result) {
		if (!result.lastErrorObject.updatedExisting) {
			return res.status(400).send('Bad Request invalid id');
		}
		res.send(result.value);
	});
});

router.post('/:id/image', function(req, res) {
	if (!req.busboy) {
		return res.status(400).send('Bad Request file required in form');
	}
	cloudinaryService.uploadImageFromUpload(req.busboy).then(function(result) {
		return bathroomService.insertImage(req.params.id, result.public_id);
	}).then(function(result) {
		if (!result.lastErrorObject.updatedExisting) {
			return res.status(400).send('Bad Request invalid id');
		}
		res.send(result.value);
	});
});

module.exports = router;
