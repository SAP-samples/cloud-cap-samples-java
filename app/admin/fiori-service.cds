/*
  Annotations for the Manage Books App
*/

using AdminService from '../../srv/admin-service';


////////////////////////////////////////////////////////////////////////////
//
//	Books Object Page
//
annotate AdminService.Books with @(UI : {
    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>General}',
            Target : '@UI.FieldGroup#General'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Translations}',
            Target : 'texts/@UI.LineItem'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Details}',
            Target : '@UI.FieldGroup#Details'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Admin}',
            Target : '@UI.FieldGroup#Admin'
        },
        {
            $Type  : 'UI.ReferenceFacet',
            ID     : 'AttachmentsFacet',
            Label  : '{i18n>attachmentsAndLinks}',
            Target : 'attachments/@UI.LineItem'
        }
    ],
    FieldGroup #General : {Data : [
        {Value : title},
        {Value : author_ID},
        {Value : genre_ID},
        {Value : descr},
    ]},
    FieldGroup #Details : {Data : [
        {Value : stock},
        {Value : price},
        {
            Value : currency_code,
            Label : '{i18n>Currency}'
        },
    ]},
    FieldGroup #Admin : {Data : [
        {Value : createdBy},
        {Value : createdAt},
        {Value : modifiedBy},
        {Value : modifiedAt}
    ]}
});


////////////////////////////////////////////////////////////
//
//  Draft for Localized Data
//

annotate my.bookshop.Books with @fiori.draft.enabled;
annotate AdminService.Books with @odata.draft.enabled;

annotate AdminService.Books.texts with @(UI : {
    Identification : [{Value : title}],
    SelectionFields : [
        locale,
        title
    ],
    LineItem : [
        {
            Value : locale,
            Label : 'Locale'
        },
        {
            Value : title,
            Label : 'Title'
        },
        {
            Value : descr,
            Label : 'Description'
        },
    ]
});


// Add Value Help for Locales
annotate AdminService.Books.texts {
    locale @ValueList : {
        entity : 'Languages',
        type : #fixed
    }
}

annotate AdminService.Books actions {
    @(
        Common.SideEffects : {
            TargetProperties : ['_it/order_ID'],
            TargetEntities : [_it]
        },
        cds.odata.bindingparameter.name : '_it'
    )
    addToOrder(order_ID @(
        title : '{i18n>Order}',
        Common : {ValueListMapping : {
            Label : '{i18n>Orders}',
            CollectionPath : 'Orders',
            Parameters : [
                {
                    $Type : 'Common.ValueListParameterInOut',
                    LocalDataProperty : order_ID,
                    ValueListProperty : 'ID'
                },
                {
                    $Type : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'OrderNo'
                },
                {
                    $Type : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'createdBy'
                },
                {
                    $Type : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'createdAt'
                }
            ],
        }}
    ),
    quantity @title : '{i18n>Quantity}'
    )
}
