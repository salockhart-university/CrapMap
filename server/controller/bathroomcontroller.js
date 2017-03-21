'use strict';

const express = require('express');
const router = express.Router();
const haversine = require('haversine');

const bathroomService = require('../service/bathroomservice');
const cloudinaryService = require('../service/cloudinaryservice');

function pointIsWithinRadius(start, end, radius) {
	const distance = haversine(start, end, {
		unit: 'meter'
	});

	return distance <= radius;
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

router.post('/:id/review', function(req, res) {
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
	bathroomService.insertReview(req.params.id, req.body).then(function(result) {
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
