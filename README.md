# CrapMap

## Server

### Deploy

Deploys require the Heroku CLI: https://devcenter.heroku.com/articles/heroku-cli#download-and-install

Deploys require that you have a `heroku` origin setup as follows:

`git remote add heroku https://git.heroku.com/crap-map-server.git`

There is a `deploy` script in `package.json` that can be run to deploy the application to Heroku at https://crap-map-server.herokuapp.com.  Run it as `npm run deploy` from the server directory.
