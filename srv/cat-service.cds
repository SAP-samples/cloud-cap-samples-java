using {my.bookshop as my} from '../db/schema';

@path : 'browse'
service CatalogService @(requires: 'any') {
    @readonly
    entity Books       as projection on my.Books excluding {
        createdBy,
        modifiedBy
    };

    @readonly
    entity Authors     as projection on my.Authors;
}
