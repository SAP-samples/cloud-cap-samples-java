using {my.bookshop as my} from '../db/index';

/**
 * CatalogService exposes the Books, Authors, Reviews, and GenreHierarchy entities.
 * It also defines actions for adding reviews and submitting orders.
 */
@path : 'browse'
@odata.apply.transformations
@description: '@description: CatalogService exposes the Books, Authors, Reviews, and GenreHierarchy entities. It also defines actions for adding reviews and submitting orders.'
service CatalogService @(requires: 'any') {
    /**
     * Doc comment: Books entity projection with addReview action
     */
    //@description: '@description: Books entity projection with addReview action'
    @readonly
    entity Books       as projection on my.Books excluding {
        createdBy,
        modifiedBy
    } actions {
        action addReview(rating : Integer, title : String, text : String) returns Reviews;
    };

    @readonly
    entity Authors     as projection on my.Authors;

    @readonly
    entity Reviews     as projection on my.Reviews;
    @readonly
    entity GenreHierarchy as projection on my.Genres;

    action submitOrder(book : Books : ID, quantity : Integer) returns {
        stock : Integer
    };

    // access control restrictions
    annotate Reviews with @restrict : [
        {
            grant : 'READ',
            to : 'any'
        },
        {
            grant : 'CREATE',
            to : 'authenticated-user'
        }
    ];
}

annotate CatalogService with @mcp;
