/*
  Annotations for the Browse Books App
*/

using CatalogService from '../../srv/cat-service';

////////////////////////////////////////////////////////////////////////////
//
//	Books Object Page
//
annotate CatalogService.Books with @(
	UI: {
		HeaderInfo: {
			Description: {Value: author}
		},
		HeaderFacets: [
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Description}', Target: '@UI.FieldGroup#Descr'},
		],
		Facets: [
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Details}', Target: '@UI.FieldGroup#Price'},
		],
		FieldGroup#Descr: {
			Data: [
				{Value: descr},
			]
		},
		FieldGroup#Price: {
			Data: [
				{Value: price},
				{Value: currency.symbol, Label: '{i18n>Currency}'},
			]
		},
	}
);


////////////////////////////////////////////////////////////////////////////
//
//	Books Object Page
//
annotate CatalogService.Books with @(
	UI: {
	  SelectionFields: [ ID, price, currency_code ],
		LineItem: [
			{Value: title},
			{Value: author, Label:'{i18n>Author}'},
			{Value: price},
			{Value: currency.symbol, Label:' '},
		]
	},
);
