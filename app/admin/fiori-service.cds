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
            Label  : '{i18n>attachments}',
            Target : 'attachments/@UI.LineItem'
        },
        {
            $Type  : 'UI.ReferenceFacet',
            ID     : 'ReferencesFacet',
            Label  : 'References',
            Target : 'references/@UI.LineItem'
        },
        {
            $Type  : 'UI.ReferenceFacet',
            ID     : 'FootNotesFacet',
            Label  : 'FootNotes',
            Target : 'footnotes/@UI.LineItem'
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Chapters}',
            ID : 'i18nChapters',
            Target : 'cHapters/@UI.LineItem#i18nChapters',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : '{i18n>Pages}',
            ID : 'i18nPages',
            Target : 'pages/@UI.LineItem#i18nPages',
        },
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

annotate AdminService.Books.attachments with {
  customProperty1 @Common.ValueListWithFixedValues;
}
annotate AdminService.Books.references with {
  customProperty1 @Common.ValueListWithFixedValues;
}
annotate AdminService.Books.footnotes with {
  customProperty1 @Common.ValueListWithFixedValues;
}

// Chapters annotations
annotate AdminService.Chapters with @title : '{i18n>Chapter}';

annotate AdminService.Books.chapters with @(
    title : '{i18n>Chapters}'
);

annotate AdminService.Chapters with @(
    UI.LineItem : [
        {
            $Type : 'UI.DataField',
            Value : title,
            Label : '{i18n>ChapterTitle}',
        },
        {
            $Type : 'UI.DataField',
            Value : chapterType,
            Label : '{i18n>ChapterType}',
        },
        {
            $Type : 'UI.DataField',
            Value : description,
            Label : '{i18n>Description}',
        },
    ],
    UI.LineItem #i18nChapters : [
        {
            $Type : 'UI.DataField',
            Value : title,
            Label : '{i18n>ChapterTitle}',
        },
        {
            $Type : 'UI.DataField',
            Value : chapterType,
            Label : '{i18n>ChapterType}',
        },
        {
            $Type : 'UI.DataField',
            Value : description,
            Label : '{i18n>Description}',
        },
    ]
);

annotate AdminService.Chapters with @(
    UI.HeaderInfo : {
        Title : {
            $Type : 'UI.DataField',
            Value : title,
        },
        TypeName : '{i18n>Chapter}',
        TypeNamePlural : '{i18n>Chapters}',
        Description : {
            $Type : 'UI.DataField',
            Value : description,
        },
    }
);

annotate AdminService.Chapters with @(
    UI.FieldGroup #GeneratedGroup1 : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Value : title,
                Label : '{i18n>ChapterTitle}',
            },
            {
                $Type : 'UI.DataField',
                Value : chapterType,
                Label : '{i18n>ChapterType}',
            },
            {
                $Type : 'UI.DataField',
                Value : description,
                Label : '{i18n>Description}',
            },
            {
                $Type : 'UI.DataField',
                Value : url,
                Label : '{i18n>URL}',
            },
        ],
    },
  UI.Facets : [
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'GeneratedFacet1',
      Label : '{i18n>GeneralInformation}',
      Target : '@UI.FieldGroup#GeneratedGroup1',
    },
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'AttachmentsFacet',
      Label : '{i18n>attachments}',
      Target : 'attachments/@UI.LineItem'
    },
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'ReferencesFacet',
      Label : '{i18n>references}',
      Target : 'references/@UI.LineItem'
    },
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'FootnotesFacet',
      Label : '{i18n>Footnotes}',
      Target : 'footnotes/@UI.LineItem'
    }
  ]
);

//////////

// Pages annotations
annotate AdminService.Pages with @title : '{i18n>Page}';

annotate AdminService.Books.pages with @(
    title : '{i18n>Pages}'
);

annotate AdminService.Pages with @(
    UI.LineItem : [
        {
            $Type : 'UI.DataField',
            Value : title,
            Label : '{i18n>PageTitle}',
        },
        {
            $Type : 'UI.DataField',
            Value : pageType,
            Label : '{i18n>PageType}',
        },
        {
            $Type : 'UI.DataField',
            Value : description,
            Label : '{i18n>Description}',
        },
    ],
    UI.LineItem #i18nPages : [
        {
            $Type : 'UI.DataField',
            Value : title,
            Label : '{i18n>PageTitle}',
        },
        {
            $Type : 'UI.DataField',
            Value : pageType,
            Label : '{i18n>PageType}',
        },
        {
            $Type : 'UI.DataField',
            Value : description,
            Label : '{i18n>Description}',
        },
    ]
);

annotate AdminService.Pages with @(
    UI.HeaderInfo : {
        Title : {
            $Type : 'UI.DataField',
            Value : title,
        },
        TypeName : '{i18n>Page}',
        TypeNamePlural : '{i18n>Pages}',
        Description : {
            $Type : 'UI.DataField',
            Value : description,
        },
    }
);

annotate AdminService.Pages with @(
    UI.FieldGroup #GeneratedGroup1 : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Value : title,
                Label : '{i18n>PageTitle}',
            },
            {
                $Type : 'UI.DataField',
                Value : pageType,
                Label : '{i18n>PageType}',
            },
            {
                $Type : 'UI.DataField',
                Value : description,
                Label : '{i18n>Description}',
            },
            {
                $Type : 'UI.DataField',
                Value : url,
                Label : '{i18n>URL}',
            },
        ],
    },
  UI.Facets : [
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'GeneratedFacet1',
      Label : '{i18n>GeneralInformation}',
      Target : '@UI.FieldGroup#GeneratedGroup1',
    },
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'AttachmentsFacet',
      Label : '{i18n>attachments}',
      Target : 'attachments/@UI.LineItem'
    },
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'ReferencesFacet',
      Label : '{i18n>references}',
      Target : 'references/@UI.LineItem'
    },
    {
      $Type : 'UI.ReferenceFacet',
      ID : 'FootnotesFacet',
      Label : '{i18n>Footnotes}',
      Target : 'footnotes/@UI.LineItem'
    }
  ]
);