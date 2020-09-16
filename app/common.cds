/*
  Common Annotations shared by all apps
*/

using { my.bookshop as my } from '../db/schema';


////////////////////////////////////////////////////////////////////////////
//
//	Books Lists
//
annotate my.Books with @(
	UI: {
		Identification: [{Value:title}],
	  SelectionFields: [ ID, author_ID, price, currency_code ],
		LineItem: [
			{Value: ID},
			{Value: title},
			{Value: author.name, Label:'{i18n>Author}'},
			{Value: stock},
			{Value: price},
			{Value: currency.symbol, Label:' '},
		]
	}
) {
	author @ValueList.entity:'Authors';
};

annotate my.Authors with @(
	UI: {
		Identification: [{Value:name}],
	}
);


////////////////////////////////////////////////////////////////////////////
//
//	Books Details
//
annotate my.Books with @(
	UI: {
  	HeaderInfo: {
  		TypeName: '{i18n>Book}',
  		TypeNamePlural: '{i18n>Books}',
  		Title: {Value: title},
  		Description: {Value: author.name}
  	},
	}
);



////////////////////////////////////////////////////////////////////////////
//
//	Books Elements
//
annotate my.Books with {
	ID @title:'{i18n>ID}' @UI.HiddenFilter;
	title @title:'{i18n>Title}';
	author @title:'{i18n>AuthorID}';
	price @title:'{i18n>Price}';
	stock @title:'{i18n>Stock}';
	descr @UI.MultiLineText;
}


////////////////////////////////////////////////////////////////////////////
//
//	Authors Elements
//
annotate my.Authors with {
	ID @title:'{i18n>ID}' @UI.HiddenFilter;
	name @title:'{i18n>AuthorName}';
}

////////////////////////////////////////////////////////////////////////////
//
//	Fiori requires generated IDs to be annotated with @Core.Computed
//
using { cuid } from '@sap/cds/common';

annotate cuid with {
	ID @Core.Computed
}
