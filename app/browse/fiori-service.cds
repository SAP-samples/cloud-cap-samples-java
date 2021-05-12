/*
  Annotations for the Browse Books App
*/

using CatalogService from '../../srv/cat-service';

////////////////////////////////////////////////////////////////////////////
//
//	Books Object Page
//
annotate CatalogService.Books with @(UI : {
    HeaderInfo : {
        TypeName : '{i18n>Book}',
        TypeNamePlural : '{i18n>Books}',
        Title : {Value : title},
        Description : {Value : author.name}
    },
    Identification : [
        {Value : title},
        {
            $Type : 'UI.DataFieldForAction',
            Label : '{i18n>AddReview}',
            Action : 'CatalogService.addReview'
        }
    ],
    PresentationVariant : {
        Text : 'Default',
        SortOrder : [{Property : title}],
        Visualizations : ['@UI.LineItem']
    },
    SelectionFields : [
        author_ID,
        genre_ID
    ],
    HeaderFacets : [
        {
            $Type : 'UI.ReferenceFacet',
            Target : '@UI.DataPoint#rating'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Target : '@UI.DataPoint#price'
        }
    ],
    LineItem : [
        {Value : title},
        {
            Value : author.name,
            Label : '{i18n>Author}'
        },
        {
            Value : genre.name,
            Label : '{i18n>Genre}'
        },
        {
            $Type : 'UI.DataFieldForAnnotation',
            Target : '@UI.DataPoint#rating',
            Label : '{i18n>Rating}'
        },
        {Value : price},
        {
            $Type : 'UI.DataFieldForAnnotation',
            Label : '{i18n>AddReview}',
            Target : '@UI.FieldGroup#AddReview'
        }
    ],
    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>General}',
            Target : '@UI.FieldGroup#General'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Description}',
            Target : '@UI.FieldGroup#Descr'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Reviews}',
            Target : 'reviews/@UI.LineItem'
        }
    ],
    FieldGroup #AddReview : {Data : [{
        $Type : 'UI.DataFieldForAction',
        Label : '{i18n>AddReview}',
        Action : 'CatalogService.addReview',
        InvocationGrouping : #ChangeSet
    }, ]},
    FieldGroup #General : {Data : [
        {Value : title},
        {Value : author_ID},
        {Value : genre_ID}
    ]},
    FieldGroup #Descr : {Data : [{Value : descr}]},
    DataPoint #stock : {
        Value : stock,
        Title : '{i18n>Stock}'
    },
    DataPoint #price : {
        Value : price,
        Title : '{i18n>Price}'
    },
    DataPoint #rating : {
        Value : rating,
        Title : '{i18n>Rating}',
        Visualization : #Rating,
        MinimumValue : 0,
        MaximumValue : 5,
        TargetValue : 5
    }
}) {
    @Measures.ISOCurrency : currency_code
    price
};

annotate CatalogService.Books.texts with @(UI : {LineItem : [
    {Value : locale},
    {Value : title},
    {Value : descr}
]});

annotate CatalogService.Reviews with @(UI : {
    PresentationVariant : {
        $Type : 'UI.PresentationVariantType',
        SortOrder : [{
            $Type : 'Common.SortOrderType',
            Property : modifiedAt,
            Descending : true
        }, ],
    },
    LineItem : [
        {
            $Type : 'UI.DataFieldForAnnotation',
            Label : '{i18n>Rating}',
            Target : '@UI.DataPoint#rating'
        },
        {
            $Type : 'UI.DataFieldForAnnotation',
            Label : '{i18n>User}',
            Target : '@UI.FieldGroup#ReviewerAndDate'
        },
        {
            Value : title,
            Label : '{i18n>Title}'
        },
        {
            Value : text,
            Label : '{i18n>Text}'
        },
    ],
    DataPoint #rating : {
        Value : rating,
        Visualization : #Rating,
        MinimumValue : 0,
        MaximumValue : 5
    },
    FieldGroup #ReviewerAndDate : {Data : [
        {Value : createdBy},
        {Value : modifiedAt}
    ]}
});

annotate CatalogService.Books actions {
    @(
        Common.SideEffects : {
            TargetProperties : ['_it/rating'],
            TargetEntities : [
                _it,
                _it.reviews
            ]
        },
        cds.odata.bindingparameter.name : '_it',
        Core.OperationAvailable : _it.isReviewable
    )
    addReview(rating @title : '{i18n>Rating}', title  @title : '{i18n>Title}', text  @title : '{i18n>Text}')
}
