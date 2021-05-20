/*
  Common Annotations shared by all apps
*/
using {my.bookshop as my} from '../db/index';


////////////////////////////////////////////////////////////////////////////
//
//	Books Lists
//
annotate my.Books with
@(
    Common.SemanticKey : [title],
    UI : {
        Identification : [{Value : title}],
        SelectionFields : [
            ID,
            author_ID,
            price,
            currency_code
        ],
        LineItem : [
            {Value : ID},
            {Value : title},
            {
                Value : author.name,
                Label : '{i18n>Author}'
            },
            {Value : genre.name},
            {Value : stock},
            {Value : price},
            {
                Value : currency.symbol,
                Label : ' '
            },
            {
                $Type : 'UI.DataFieldForAction',
                Label : '{i18n>AddToOrder}',
                Action : 'AdminService.addToOrder'
            },
        ]
    }
) {
    author
    @ValueList.entity : 'Authors';
};


////////////////////////////////////////////////////////////////////////////
//
//	Books Details
//
annotate my.Books with
@(UI : {HeaderInfo : {
    TypeName : '{i18n>Book}',
    TypeNamePlural : '{i18n>Books}',
    TypeImageUrl : 'sap-icon://course-book',
    Title : {Value : title},
    Description : {Value : author.name}
}, });


////////////////////////////////////////////////////////////////////////////
//
//	Books Elements
//
annotate my.Books with {
    ID
    @title : '{i18n>ID}'
    @UI.HiddenFilter;
    title
    @title : '{i18n>Title}';
    genre
    @title : '{i18n>Genre}'
    @Common : {
        Text : genre.name,
        TextArrangement : #TextOnly
    };
    author
    @title : '{i18n>Author}'
    @Common : {
        Text : author.name,
        TextArrangement : #TextOnly
    };
    price
    @title : '{i18n>Price}';
    stock
    @title : '{i18n>Stock}';
    descr
    @title : '{i18n>Description}'
    @UI.MultiLineText;
}


////////////////////////////////////////////////////////////////////////////
//
//	Reviews List
//
annotate my.Reviews with
@(UI : {
    Identification : [
        {
            Value : ID,
            ![@UI.Hidden]
        },
        {Value : title}
    ],
    SelectionFields : [
        book_ID,
        rating
    ],
    LineItem : [
        {
            Value : modifiedAt,
            Label : 'Date'
        },
        {
            Value : createdBy,
            Label : '{i18n>User}'
        },
        {
            $Type : 'UI.DataFieldForAnnotation',
            Label : '{i18n>Book}',
            Target : '@UI.FieldGroup#BookAndAuthor'
        },
        {
            $Type : 'UI.DataFieldForAnnotation',
            Label : '{i18n>Rating}',
            Target : '@UI.DataPoint#rating'
        },
        {
            Value : title,
            Label : '{i18n>Review}'
        }
    ],
    FieldGroup #BookAndAuthor : {Data : [
        {Value : book.title},
        {Value : book.author.name}
    ]},
    DataPoint #rating : {
        Value : rating,
        Visualization : #Rating,
        MinimumValue : 0,
        MaximumValue : 5
    }
});

annotate my.Reviews with {
    ID
    @title : '{i18n>ID}'
    @UI.HiddenFilter;
    title
    @title : '{i18n>Title}';
    book
    @ValueList.entity : 'Books'
    @title : '{i18n>Book}'
    @Common : {
        Text : book.title,
        TextArrangement : #TextOnly
    };
    date
    @title : '{i18n>Date}';
    rating
    @title : '{i18n>Rating}';
    text
    @title : '{i18n>Text}'
    @UI.MultiLineText;
}


////////////////////////////////////////////////////////////////////////////
//
//	Genres List
//
annotate my.Genres with
@(
    Common.SemanticKey : [name],
    UI : {
        SelectionFields : [name],
        LineItem : [
            {Value : name},
            {
                Value : parent.name,
                Label : 'Main Genre'
            },
        ],
    }
);


////////////////////////////////////////////////////////////////////////////
//
//	Genre Details
//
annotate my.Genres with
@(UI : {
    Identification : [{Value : name}],
    HeaderInfo : {
        TypeName : '{i18n>Genre}',
        TypeNamePlural : '{i18n>Genres}',
        Title : {Value : name},
        Description : {Value : ID}
    },
    Facets : [{
        $Type : 'UI.ReferenceFacet',
        Label : '{i18n>SubGenres}',
        Target : 'children/@UI.LineItem'
    }, ],
});


////////////////////////////////////////////////////////////////////////////
//
//	Genres Elements
//
annotate my.Genres with {
    ID
    @title : '{i18n>ID}';
    name
    @title : '{i18n>Genre}';
}


////////////////////////////////////////////////////////////////////////////
//
//	Authors List
//
annotate my.Authors with
@(
    Common.SemanticKey : [name],
    UI : {
        Identification : [{Value : name}],
        SelectionFields : [name],
        LineItem : [
            {Value : ID},
            {Value : name},
            {Value : dateOfBirth},
            {Value : dateOfDeath},
            {Value : placeOfBirth},
            {Value : placeOfDeath},
        ],
    }
);


////////////////////////////////////////////////////////////////////////////
//
//	Author Details
//
annotate my.Authors with
@(UI : {
    HeaderInfo : {
        TypeName : '{i18n>Author}',
        TypeNamePlural : '{i18n>Authors}',
        Title : {Value : name},
        Description : {Value : dateOfBirth}
    },
    Facets : [{
        $Type : 'UI.ReferenceFacet',
        Target : 'books/@UI.LineItem'
    }, ],
});


////////////////////////////////////////////////////////////////////////////
//
//	Authors Elements
//
annotate my.Authors with {
    ID
    @title : '{i18n>ID}'
    @UI.HiddenFilter;
    name
    @title : '{i18n>Name}';
    dateOfBirth
    @title : '{i18n>DateOfBirth}';
    dateOfDeath
    @title : '{i18n>DateOfDeath}';
    placeOfBirth
    @title : '{i18n>PlaceOfBirth}';
    placeOfDeath
    @title : '{i18n>PlaceOfDeath}';
}


////////////////////////////////////////////////////////////////////////////
//
//	Languages List
//
annotate common.Languages with
@(
    Common.SemanticKey : [code],
    Identification : [{Value : code}],
    UI : {
        SelectionFields : [
            name,
            descr
        ],
        LineItem : [
            {Value : code},
            {Value : name},
        ],
    }
);


////////////////////////////////////////////////////////////////////////////
//
//	Language Details
//
annotate common.Languages with
@(UI : {
    HeaderInfo : {
        TypeName : '{i18n>Language}',
        TypeNamePlural : '{i18n>Languages}',
        Title : {Value : name},
        Description : {Value : descr}
    },
    Facets : [{
        $Type : 'UI.ReferenceFacet',
        Label : '{i18n>Details}',
        Target : '@UI.FieldGroup#Details'
    }, ],
    FieldGroup #Details : {Data : [
        {Value : code},
        {Value : name},
        {Value : descr}
    ]},
});


////////////////////////////////////////////////////////////////////////////
//
//	Currencies List
//
annotate common.Currencies with
@(
    Common.SemanticKey : [code],
    Identification : [{Value : code}],
    UI : {
        SelectionFields : [
            name,
            descr
        ],
        LineItem : [
            {Value : descr},
            {Value : symbol},
            {Value : code},
        ],
    }
);


////////////////////////////////////////////////////////////////////////////
//
//	Currency Details
//
annotate common.Currencies with
@(UI : {
    HeaderInfo : {
        TypeName : '{i18n>Currency}',
        TypeNamePlural : '{i18n>Currencies}',
        Title : {Value : descr},
        Description : {Value : code}
    },
    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Details}',
            Target : '@UI.FieldGroup#Details'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Extended}',
            Target : '@UI.FieldGroup#Extended'
        },
    ],
    FieldGroup #Details : {Data : [
        {Value : name},
        {Value : symbol},
        {Value : code},
        {Value : descr}
    ]},
    FieldGroup #Extended : {Data : [
        {Value : numcode},
        {Value : minor},
        {Value : exponent}
    ]},
});


////////////////////////////////////////////////////////////////////////////
//
//	Currencies Elements
//
annotate common.Currencies with {
    numcode
    @title : '{i18n>NumCode}';
    minor
    @title : '{i18n>MinorUnit}';
    exponent
    @title : '{i18n>Exponent}';
}

////////////////////////////////////////////////////////////////////////////
//
//	Fiori requires generated IDs to be annotated with @Core.Computed
//
using {cuid} from '@sap/cds/common';

annotate cuid with {
    ID
    @Core.Computed
}
