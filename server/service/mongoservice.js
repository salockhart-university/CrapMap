'use strict';

// https://mongodb.github.io/node-mongodb-native/2.2/quick-start/
const MongoClient = require('mongodb').MongoClient;

const url = process.env.MONGO_URL;

module.exports = {
	connect: function () {
		return new Promise(function (resolve, reject) {
			MongoClient.connect(url, function(err, db) {
				if (err) {
					console.err('fatal error in creating db object');
					reject('Error getting DB object');
				}
				resolve(db);
			});
		});
	}
};
