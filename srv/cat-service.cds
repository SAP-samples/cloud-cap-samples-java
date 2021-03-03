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
    entity Reviews     as projection on my.Reviews actions {
        action like();
        action unlike();
    };

    @readonly
    entity ListOfBooks as
        select from Books
        excluding {
            descr
        };

    action submitOrder(book : Books : ID, amount : Integer) returns {
        stock : Integer
    };

    // input validation
    annotate Reviews with {
        subject @mandatory;
        title   @mandatory;
        rating  @assert.enum;
    }

    // access control restrictions
    annotate CatalogService.Reviews with @restrict : [
    {
        grant : 'READ',
        to    : 'any'
    },
    {
        grant : 'CREATE',
        to    : 'authenticated-user'
    },
    {
        grant : 'UPDATE',
        to    : 'authenticated-user',
        where : 'reviewer=$user'
    },
    {
        grant : 'DELETE',
        to    : 'admin'
    },
    {
        grant : 'DELETE',
        to    : 'authenticated-user',
        where : 'reviewer=$user'
    }
    ];
}
