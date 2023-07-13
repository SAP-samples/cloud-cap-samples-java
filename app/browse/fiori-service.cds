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
        {Value : title}
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
        {Value : price},
        {Value: title},
        {
            Value: isbn,
            Label: '{i18n>ISBN}'
        },
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
        }
    ],
    FieldGroup #General : {Data : [
        {Value : title},
        {Value : author_ID},
        {Value : genre_ID},
        {Value: title},
        {
            Value: isbn, 
            Label: '{i18n>ISBN}'
        },
    ]},
    FieldGroup #Descr : {Data : [{Value : descr}]},
    DataPoint #stock : {
        Value : stock,
        Title : '{i18n>Stock}'
    },
    DataPoint #price : {
        Value : price,
        Title : '{i18n>Price}'
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