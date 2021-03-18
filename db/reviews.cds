namespace my.bookshop;

using {
    my.bookshop as my,
    User,
    managed,
    cuid
} from '@sap/cds/common';

entity Reviews : cuid, managed {
    @cds.odata.ValueList
    book     : Association to my.Books;
    rating   : Rating;
    title    : String(111);
    text     : String(1111);
}

// input validation
annotate Reviews with {
    subject @mandatory;
    title @mandatory;
    rating @assert.enum;
}

type Rating : Integer enum {
    Best  = 5;
    Good  = 4;
    Avg   = 3;
    Poor  = 2;
    Worst = 1;
}
