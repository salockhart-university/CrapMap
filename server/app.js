'use strict';

const express = require('express');
const app = express();

app.set('port', (process.env.PORT || 3000));

app.get('/', function(req, res) {
    res.status(200).send('CrapMap Server is Running');
});

app.listen(app.get('port'), function() {
	console.log(`Listening on port ${app.get('port')}!`);
});
