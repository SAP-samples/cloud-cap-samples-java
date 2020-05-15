/*
  Annotations for the Manage Orders App
*/

using AdminService from '../../srv/admin-service';

annotate AdminService.Books with {
	price @Common.FieldControl: #ReadOnly;
}
////////////////////////////////////////////////////////////////////////////
//
//	Common
//
annotate AdminService.OrderItems with {
	book @(
		Common: {
			Text: book.title,
			FieldControl: #Mandatory
		},
		ValueList.entity:'Books',
	);
	amount @(
		Common.FieldControl: #Mandatory
	);
}


annotate AdminService.Orders with @(
	UI: {
		////////////////////////////////////////////////////////////////////////////
		//
		//	Lists of Orders
		//
		SelectionFields: [ createdAt, createdBy ],
		LineItem: [
			{Value: createdBy, Label:'{i18n>CreatedBy}'},
			{Value: total, Label: '{i18n>Total}' },
			{Value: createdAt, Label:'{i18n>CreatedAt}'}
		],
		////////////////////////////////////////////////////////////////////////////
		//
		//	Order Details
		//
		HeaderInfo: {
			TypeName: '{i18n>Order}', TypeNamePlural: '{i18n>Orders}',
			Title: {
				Label: '{i18n>OrderNumber}', //A label is possible but it is not considered on the ObjectPage yet
				Value: OrderNo
			},
			Description: {Value: createdBy}
		},
		Identification: [ //Is the main field group
			{Value: createdBy, Label:'{i18n>CreatedBy}'},
			{Value: createdAt, Label:'{i18n>CreatedAt}'},
			{Value: OrderNo },
		],
		HeaderFacets: [
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Created}', Target: '@UI.FieldGroup#Created'},
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Modified}', Target: '@UI.FieldGroup#Modified'},
		],
		Facets: [
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>Details}', Target: '@UI.FieldGroup#Details'},
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>OrderItems}', Target: 'Items/@UI.LineItem'},
		],
		FieldGroup#Details: {
			Data: [
				{Value: total, Label:'{i18n>Total}'},
				{Value: currency_code, Label:'{i18n>Currency}'}
			]
		},
		FieldGroup#Created: {
			Data: [
				{Value: createdBy},
				{Value: createdAt},
			]
		},
		FieldGroup#Modified: {
			Data: [
				{Value: modifiedBy},
				{Value: modifiedAt},
			]
		},
	},
	Common: {
		SideEffects#AmountChanges: {
			SourceEntities: [
				Items
			],
			TargetProperties: [
				total
			]
		},
		SideEffects#CurrencyChanges: {
			SourceProperties: [
				currency_code
			],
			TargetProperties: [
				currency.code,
				total
			]
		}
	}
) {
	createdAt @UI.HiddenFilter:false;
	createdBy @UI.HiddenFilter:false;
	total
		@Common.FieldControl: #ReadOnly
		@Measures.ISOCurrency:currency.code; //Bind the currency field to the amount field
		//In all services we always find currency as the code and not as an object that contains a property code
		//it seems to work but at least to me this is unconventional modeling.
};



//The enity types name is AdminService.my_bookshop_OrderItems
//The annotations below are not generated in edmx WHY?
annotate AdminService.OrderItems with @(
	UI: {
		HeaderInfo: {
			TypeName: '{i18n>OrderItem}', TypeNamePlural: '{i18n>OrderItems}',
			Title: {
				Value: book.title
			},
			Description: {Value: book.descr}
		},
		// There is no filterbar for items so the selctionfileds is not needed
		SelectionFields: [ book_ID ],
		////////////////////////////////////////////////////////////////////////////
		//
		//	Lists of OrderItems
		//
		LineItem: [
			{Value: book_ID, Label:'{i18n>Books}'},
			//The following entry is only used to have the assoication followed in the read event
			{Value: book.price, Label:'{i18n>BookPrice}'},
			{Value: amount, Label:'{i18n>Amount}'},
			{Value: netAmount, Label: '{i18n>NetAmount}'}
		],
		Identification: [ //Is the main field group
			//{Value: ID, Label:'{i18n>ID}'}, //A guid shouldn't be on the UI
			{Value: book_ID, Label:'{i18n>Book}'},
			{Value: amount, Label:'{i18n>Amount}'},
			{Value: netAmount, Label: '{i18n>NetAmount}'}
		],
		Facets: [
			{$Type: 'UI.ReferenceFacet', Label: '{i18n>OrderItem}', Target: '@UI.Identification'},
		],
	},
	Common: {
		SideEffects#AmountChanges: {
			SourceProperties: [
				amount
			],
			TargetProperties: [
				netAmount, parent.total
			]
		},
		SideEffects#BookChanges: {
			SourceProperties: [
				book_ID
			],
			TargetProperties: [
				netAmount, book.price, parent.total
			]
		}
	}
) {
	netAmount
		@Common.FieldControl: #ReadOnly;
		//ERROR ALERT: The following line refering to the parents currency code will lead to a server error
		//@Measures.ISOCurrency:parent.currency.code; //Bind the currency field to the amount field of the parent
};
