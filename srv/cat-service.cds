using {my.bookshop as my} from '../db/index';

@path : 'browse'
service CatalogService {
    
    @readonly
    entity Books       as projection on my.Books;

    @readonly
    entity Authors     as projection on my.Authors;
   

    action submitOrder(book : Books : ID, amount : Integer) returns {
        stock : Integer
    };
}
