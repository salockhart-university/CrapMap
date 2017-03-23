'use strict';

const MongoClient = require('mongodb').MongoClient;

const url = process.env.MONGO_URL;

function ensureIndexes(db) {
	Promise.all([
		db.ensureIndex('user', { username: 1 }, { unique: true })
	]).catch(function (err) {
		console.error('error in creating indexes', err);
	});
}

MongoClient.connect(url, function(err, db) {
	if (err) {
		console.fatal('fatal error in creating db object');
		throw 'Error getting DB object';
	}
	ensureIndexes(db);
});

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
