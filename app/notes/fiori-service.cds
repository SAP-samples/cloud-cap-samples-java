using NotesService from '../../srv/notes-mashup';

annotate NotesService.Notes with @odata.draft.enabled @(UI : {
    LineItem : [
        {
            Value : address.businessPartner,
            Label : '{i18n>BusinessPartner}'
        },
        {
            Value : address.street,
            Label : '{i18n>StreetName}'
        },
        {
            Value : address.city,
            Label : '{i18n>CityName}'
        },
        {
            Value : note,
            Label : '{i18n>Note}'
        }
    ],
    HeaderInfo : {
        TypeName : '{i18n>Note}',
        TypeNamePlural : '{i18n>Notes}',
        Title : {Value : '{i18n>Note}'},
        Description : {Value : ID},
    },
    PresentationVariant : {
        Text : 'Default',
        Visualizations : ['@UI.LineItem']
    },
    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Target : '@UI.FieldGroup#Note',
            Label : '{i18n>Note}',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Target : '@UI.FieldGroup#Address',
            Label : '{i18n>ShippingAddress}',
        }
    ],
    FieldGroup #Note : {Data : [
        {
            Value : note,
            Label : '{i18n>Note}'
        }
    ]},
    FieldGroup #Address : {Data : [
        {
            Value : address_businessPartner,
            Label : '{i18n>BusinessPartner}'
        },
        {
            Value : address.ID,
            Label : '{i18n>ID}'
        },
        {
            Value : address.street,
            Label : '{i18n>StreetName}'
        },
        {
            Value : address.houseNumber,
            Label : '{i18n>HouseNumber}'
        },
        {
            Value : address.postalCode,
            Label : '{i18n>PostalCode}'
        },
        {
            Value : address.city,
            Label : '{i18n>CityName}'
        },
        {
            Value : address.country,
            Label : '{i18n>Country}'
        }
    ]},
}, Common : {
        SideEffects #AddressChanges : {
            SourceProperties : [address_businessPartner],
            TargetEntities   : [address]
        }
}) {
    ID
    @title : '{i18n>ID}'
    @UI.HiddenFilter;
    note
    @title : '{i18n>Note}'
    @UI.MultiLineText;
    address
    @(Common : {
        FieldControl : #Mandatory,
        ValueList    : {
            CollectionPath  : 'Addresses',
            Label           : '{i18n>ShippingAddress}',
            SearchSupported : false,
            Parameters      : [
                {
                    $Type             : 'Common.ValueListParameterOut',
                    LocalDataProperty : 'address_businessPartner',
                    ValueListProperty : 'businessPartner'
                },
                {
                    $Type             : 'Common.ValueListParameterOut',
                    LocalDataProperty : 'address_ID',
                    ValueListProperty : 'ID',
                },
                {
                    $Type             : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'postalCode'
                },
                {
                    $Type             : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'city'
                },
                {
                    $Type             : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'country'
                },
                {
                    $Type             : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'street'
                },
                {
                    $Type             : 'Common.ValueListParameterDisplayOnly',
                    ValueListProperty : 'houseNumber'
                },
            ]
        }
    });
};
