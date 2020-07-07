/*
  Annotations for the Manage Books App
*/

using AdminService from '../../srv/admin-service';

////////////////////////////////////////////////////////////////////////////
//
//	Books Object Page
//
annotate AdminService.Books with @(
	UI: {
		LineItem: [
			{Value: ID},
			{Value: title},
			{Value: author.name, Label:'{i18n>Author}'},
			{Value: stock},
			{Value: price},
			{Value: currency.symbol, Label:' '},
			{$Type: 'UI.DataFieldForAction', Label: '{i18n>AddToOrder}', Action: 'AdminService.addToOrder'},
		],
		Facets: [
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>General}', Target: '@UI.FieldGroup#General'},
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Translations}', Target:  'texts/@UI.LineItem'},
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Details}', Target: '@UI.FieldGroup#Details'},
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Admin}', Target: '@UI.FieldGroup#Admin'},
		],
		FieldGroup#General: {
			Data: [
				{Value: title},
				{Value: author_ID},
				{Value: descr},
			]
		},
		FieldGroup#Details: {
			Data: [
				{Value: stock},
				{Value: price},
				{Value: currency_code, Label: '{i18n>Currency}'},
			]
		},
		FieldGroup#Admin: {
			Data: [
				{Value: createdBy},
				{Value: createdAt},
				{Value: modifiedBy},
				{Value: modifiedAt}
			]
		}
	}
);

annotate AdminService.Books_texts with @(
	UI: {
		Identification: [{Value:title}],
		SelectionFields: [ locale, title ],
		LineItem: [
			{Value: locale, Label: '{i18n>Locale}'},
			{Value: title, Label: '{i18n>Title}'},
			{Value: descr, Label: '{i18n>Description}'},
		]
	}
);

// Add Value Help for Locales
annotate AdminService.Books_texts {
	locale @ValueList:{entity:'Languages',type:#fixed}
}


annotate AdminService.Books actions {
	addToOrder(
		order_ID @(
			title: '{i18n>Order}',
			Common: {
				ValueListMapping: {
					Label: '{i18n>Orders}',
					CollectionPath: 'Orders',
					Parameters: [
					{ $Type:'Common.ValueListParameterInOut', LocalDataProperty: order_ID, ValueListProperty: 'ID' },
					{ $Type:'Common.ValueListParameterDisplayOnly', ValueListProperty: 'OrderNo' },
					{ $Type:'Common.ValueListParameterDisplayOnly', ValueListProperty: 'createdBy' },
					{ $Type:'Common.ValueListParameterDisplayOnly', ValueListProperty: 'createdAt' }
					],
				}
			}
		),
		amount @title: '{i18n>Amount}'
	)
}
