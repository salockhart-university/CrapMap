'use strict';

const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const busboy = require('connect-busboy');

app.set('port', (process.env.PORT || 3000));

app.get('/', function(req, res) {
	res.status(200).send('CrapMap Server is Running');
});

const bathroomController = require('./controller/bathroomcontroller');
const imageController = require('./controller/imagecontroller');

app.use(bodyParser.json());

app.use(busboy({immediate: true}));

app.use('/bathroom', bathroomController);
app.use('/image', imageController);

app.listen(app.get('port'), function() {
	console.log(`Listening on port ${app.get('port')}!`);
});
