using {CatalogService} from '../../app/browse/fiori-service';
using {my.bookshop as my} from '../../db/index';


// domain model

extend my.Books with {
    isbn : String(40);
}

//	UI
annotate CatalogService.Books with @(UI : {LineItem : [
    ...up to
    {Value : title},
    {
        Value : isbn,
        Label : 'ISBN'
    },
    ...
]});
