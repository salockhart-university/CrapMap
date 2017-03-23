'use strict';

const express = require('express');
const jwt = require('jsonwebtoken');
const router = express.Router();

const userService = require('../service/userservice');

router.post('/', function(req, res) {
	if (!(req.body.name && req.body.username && req.body.password)) {
		return res.status(400).send('Bad Request body needs name, username, and password');
	}

	userService.insert(req.body).then((result) => {
		return userService.get(result.insertedId);
	}).then((document) => {
		delete document.password;
		res.status(200).send(document);
	}).catch(err => {
		if (err.code === 11000) {
			return res.status(400).send('Bad Request username is already in use');
		}
		res.status(500).send('Internal Server Error');
	});
});

router.post('/login', function(req, res) {
	if (!(req.body.username && req.body.password)) {
		return res.status(400).send('Bad Request body needs username and password');
	}

	userService.getByUsername(req.body.username).then(function(doc) {
		if (!doc) {
			return res.sendStatus(401);
		}

		return userService.authenticate(req.body.password, doc.password).then(function(match) {
			if (!match) {
				return res.sendStatus(401);
			}

			const payload = {
				name: doc.name,
				username: doc.username
			};
			const token = jwt.sign(payload, process.env.PASSPORT_SECRET);

			res.status(200).send({
				token: `JWT ${token}`
			});
		});
	});
});

module.exports = router;
