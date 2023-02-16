using {CatalogService} from '../../app/browse/fiori-service';
using {my.bookshop as my} from '../../db/index';


// domain model

extend my.Books with {
    isbn : String(40);
}

// Feature 'isbn' will display ISBN in table and on object page
annotate CatalogService.Books with @(UI: {
    FieldGroup #General: {Data: [
        ...up to
        {Value: title},
        {
            Value: isbn,
            Label: '{i18n>ISBN}'
        },
        ...
    ]},
    LineItem: [
        ...up to
        {Value: title},
        {
            Value: isbn,
            Label: '{i18n>ISBN}'
        },
        ...
    ]
});
