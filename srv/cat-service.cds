using {my.bookshop as my} from '../db/index';
using {my.common.Hierarchy as Hierarchy} from './hierarchy';

@path : 'browse'
@odata.apply.transformations
service CatalogService @(requires: 'any') {
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

    extend my.Genres with Hierarchy;
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
