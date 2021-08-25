using NotesService from '../../srv/notes-mashup';

annotate NotesService.Addresses with @(UI : {
    LineItem : [
        {
            Value : businessPartner,
            Label : '{i18n>BusinessPartner}'
        },
        {
            Value : ID,
            Label : '{i18n>ID}'
        },
        {
            Value : street,
            Label : '{i18n>StreetName}'
        },
        {
            Value : city,
            Label : '{i18n>CityName}'
        },
        {
            Value : country,
            Label : '{i18n>Country}'
        }
    ],
    HeaderInfo : {
        TypeName : '{i18n>ShippingAddress}',
        TypeNamePlural : '{i18n>ShippingAddresses}',
        Title : {Value : ID},
        Description : {Value : businessPartner},
    },
    PresentationVariant : {
        Text : 'Default',
        Visualizations : ['@UI.LineItem']
    },
    Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Target : '@UI.FieldGroup#Address',
            Label : '{i18n>ShippingAddress}',
        },
        {
            $Type  : 'UI.ReferenceFacet',
            Label  : '{i18n>Notes}',
            Target : 'notes/@UI.LineItem'
        }
    ],
    FieldGroup #Address : {Data : [
        {
            Value : street,
            Label : '{i18n>StreetName}'
        },
        {
            Value : houseNumber,
            Label : '{i18n>HouseNumber}'
        },
        {
            Value : postalCode,
            Label : '{i18n>PostalCode}'
        },
        {
            Value : city,
            Label : '{i18n>CityName}'
        },
        {
            Value : country,
            Label : '{i18n>Country}'
        }
    ]},
}) {
    businessPartner
    @title : '{i18n>BusinessPartner}'
    @UI.HiddenFilter;
    ID
    @title : '{i18n>ID}'
    @UI.HiddenFilter;
    street
    @title : '{i18n>StreetName}';
    city
    @title : '{i18n>CityName}';
    country
    @title : '{i18n>Country}';
};
