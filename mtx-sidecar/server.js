const app = require('express')();
const cds = require('@sap/cds');

const main = async () => {

	app.use(defaults.correlate);

	await cds.connect.to('db');
	const PORT = process.env.PORT || 4004;
	await cds.mtx.in(app);

	app.listen(PORT);
}

const defaults = {
	// REVISIT: we need to align that with lib/req/context.js#L68 and remove redundancies
	// ensure correlation id and set intermediate or augment cds.context until tx opened in protocol adpater
	get correlate() {
		return (req, _, next) => {
			if (!cds.context) cds.context = {}
			const id = cds.context.id
				|| req.headers['x-correlation-id'] || req.headers['x-correlationid']
				|| req.headers['x-request-id'] || req.headers['x-vcap-request-id']
				|| cds.utils.uuid()
			cds.context.id = req.headers['x-correlation-id'] = id
			if (!cds.context._) cds.context._ = {}
			if (!cds.context._.req) cds.context._.req = req
			next();
		}
	}
}

main();
