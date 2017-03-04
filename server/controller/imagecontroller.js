'use strict';

const express = require('express');
const router = express.Router();
const request = require('request');

const cloudinaryService = require('../service/cloudinaryservice');

router.get('/:id', function(req, res) {
	cloudinaryService.getImageFromID(req.params.id).then(function(image) {
		request(image.secure_url).pipe(res);
	}).catch(function() {
		res.sendStatus(404);
	});
});

module.exports = router;
