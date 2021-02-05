namespace my.bookshop;

using
{
    my.bookshop as my,
    User,
    managed,
    cuid
} from '@sap/cds/common';

entity Reviews : cuid, managed
{
    @cds.odata.ValueList
    book : Association to my.Books;
    @Core.Computed
    reviewer : User;
    rating : Rating;
    title : String(111);
    text : String(1111);
    @Core.Computed
    date : DateTime;
}

// Auto-fill reviewers and review dates
annotate Reviews with
{
    reviewer @cds.on.insert : $user;
    date @cds.on.insert : $now;
    date @cds.on.update : $now;
}

type Rating : Integer enum
{
    Best = 5;
    Good = 4;
    Avg = 3;
    Poor = 2;
    Worst = 1;
}
