'use strict';

const mongoService = require('./mongoservice');
const ObjectId = require('mongodb').ObjectID;

function getBathroomCollection() {
	return mongoService.connect().then(function(db) {
		return new Promise(function(resolve, reject) {
			db.collection('bathroom', function(err, collection) {
				if (err) {
					return reject(err);
				}
				resolve(collection);
			});
		});
	});
}

module.exports = {
	getAllBathrooms: function() {
		return getBathroomCollection().then(function(collection) {
			return collection.find({}).toArray();
		});
	},

	getBathroom: function(_id) {
		return getBathroomCollection().then(function(collection) {
			return collection.find({
				_id: new ObjectId(_id)
			}).limit(1).toArray().then(function(arr) {
				return arr[0];
			});
		});
	},

	insertBathroom: function(formData) {
		const bathroom = {
			name: formData.name,
			location: formData.location,
			requiresPurchase: !!formData.requiresPurchase,
			images: [],
			reviews: []
		};

		return getBathroomCollection().then(function(collection) {
			return collection.insertOne(bathroom);
		});
	},

	deleteBathroom: function (bathroomID) {
		return getBathroomCollection().then(function(collection) {
			return collection.findOneAndDelete({
				_id: new ObjectId(bathroomID)
			});
		});
	},

	insertReview: function(bathroomID, formData) {
		const review = {
			user: formData.user || null,
			stars: formData.stars,
			review: formData.review || ''
		};

		return getBathroomCollection().then(function(collection) {
			return collection.findOneAndUpdate({
				_id: new ObjectId(bathroomID)
			}, {
				$push: {
					reviews: review
				}
			}, {
				returnOriginal: false
			});
		});
	},

	insertImage: function(bathroomID, image) {
		return getBathroomCollection().then(function(collection) {
			return collection.findOneAndUpdate({
				_id: new ObjectId(bathroomID)
			}, {
				$push: {
					images: image
				}
			});
		});
	}
};
