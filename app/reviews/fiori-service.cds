/*
  Annotations for the Browse Books App
*/

using ReviewService from '../../srv/review-service';

annotate ReviewService.Reviews with @(UI : {
    HeaderInfo : {
        TypeName : '{i18n>Review}',
        TypeNamePlural : '{i18n>Reviews}',
        Title : {Value : title},
        Description : {Value : createdBy},
    },
    PresentationVariant : {
        Text : 'Default',
        SortOrder : [{
            Property : modifiedAt,
            Descending : true
        }],
        Visualizations : ['@UI.LineItem']
    },
    SelectionFields : [
        book_ID,
        rating
    ],
    HeaderFacets : [{
        $Type : 'UI.ReferenceFacet',
        Target : '@UI.DataPoint#rating'
    }, ],
    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Target : '@UI.FieldGroup#General',
            Label : '{i18n>General}'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Target : '@UI.FieldGroup#Review',
            Label : '{i18n>Review}',
        }
    ],
    FieldGroup #General : {Data : [
        {
            Value : createdAt,
            Label : '{i18n>Created}'
        },
        {
            Value : createdBy,
            Label : '{i18n>CreatedBy}'
        },
        {
            Value : modifiedAt,
            Label : '{i18n>Modified}'
        },
        {
            Value : modifiedBy,
            Label : '{i18n>ModifiedBy}'
        },
        {Value : book_ID},
    ]},
    FieldGroup #Review : {Data : [
        {
            Value : rating,
            Label : '{i18n>Rating}'
        },
        {
            Value : title,
            Label : '{i18n>Title}'
        },
        {
            Value : text,
            Label : '{i18n>Text}'
        }
    ]},
    FieldGroup #BookAndAuthor : {Data : [
        {Value : book.title},
        {Value : book.author.name}
    ]},
    DataPoint #rating : {
        Title : '{i18n>Rating}',
        Value : rating,
        Visualization : #Rating,
        MinimumValue : 0,
        MaximumValue : 5
    }
}) {
    rating @title : '{i18n>Rating}';
    title @title : '{i18n>Title}';
    text @title : '{i18n>Text}'  @UI.MultiLineText;
};
