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
            $Type  : 'UI.ReferenceFacet',
            ID     : 'AttachmentsFacet',
            Target : 'covers/@UI.LineItem'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Admin}',
            Target : '@UI.FieldGroup#Admin'
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

// Add Value Help for Tree Table
annotate AdminService.Books with {
    genre @(Common: {
        Label    : 'Genre',
        ValueList: {
            CollectionPath              : 'GenreHierarchy',
            Parameters                  : [
            {
                $Type            : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty: 'name',
            },
            {
                $Type            : 'Common.ValueListParameterInOut',
                LocalDataProperty: genre_ID,
                ValueListProperty: 'ID',
            }
            ],
            PresentationVariantQualifier: 'VH',
        }
    });
}

// Hide ID because of the ValueHelp
annotate AdminService.GenreHierarchy with {
  ID @UI.Hidden;
};

annotate AdminService.GenreHierarchy with @UI: {
    PresentationVariant #VH: {
        $Type                      : 'UI.PresentationVariantType',
        Visualizations             : ['@UI.LineItem'],
        RecursiveHierarchyQualifier: 'GenreHierarchy'
    },
    LineItem               : [{
        $Type: 'UI.DataField',
        Value: name,
    }]
};

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

// Hides technical field up__ID in View Setitings dialog for Books.covers
annotate AdminService.Books.covers:up_ with @UI.Hidden;
