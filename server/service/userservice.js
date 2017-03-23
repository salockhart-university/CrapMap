'use strict';

const bcrypt = require('bcrypt');
const mongoService = require('./mongoservice');

function getUserCollection() {
	return mongoService.connect().then(function(db) {
		return new Promise(function(resolve, reject) {
			db.collection('user', function(err, collection) {
				if (err) {
					return reject(err);
				}
				resolve(collection);
			});
		});
	});
}

module.exports = {
	get: function(_id) {
		return getUserCollection().then(function(collection) {
			return collection.find({
				_id
			}).limit(1).toArray().then(function(arr) {
				return arr[0];
			});
		});
	},

	getByUsername: function(username) {
		return getUserCollection().then(function(collection) {
			return collection.find({
				username
			}).limit(1).toArray().then(function(arr) {
				return arr[0];
			});
		});
	},

	insert: function(formData) {
		const user = {
			name: formData.name,
			username: formData.username,
			password: ''
		};

		return bcrypt.hash(formData.password, 10).then(function(hash) {
			user.password = hash;
			return getUserCollection();
		}).then(function(collection) {
			return collection.insertOne(user);
		});
	},

	authenticate: bcrypt.compare
};
