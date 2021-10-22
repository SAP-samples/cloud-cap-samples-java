namespace my.bookshop;

using {
    User,
    managed,
    cuid
} from '@sap/cds/common';
using my.bookshop.Books from './books';

entity Reviews : cuid, managed {
    @cds.odata.ValueList
    book     : Association to Books;
    rating   : Rating;
    title    : String(111);
    text     : String(1111);
}

// input validation
annotate Reviews with {
    subject @mandatory;
    title @mandatory;
    rating @assert.range;
}

type Rating : Integer enum {
    Best  = 5;
    Good  = 4;
    Avg   = 3;
    Poor  = 2;
    Worst = 1;
}
