using {my.bookshop as my} from '../db/index';

@path : 'browse'
service CatalogService {
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
