namespace my.bookshop;

using
{
    my.bookshop as my,
    Currency,
    User,
    managed,
    cuid
} from '@sap/cds/common';

entity Orders : cuid, managed
{
    OrderNo : String @title : '{i18n>OrderNumber}'  @mandatory; //> readable key
    Items : Composition of many OrderItems
                on Items.parent = $self;
    buyer : User;
    total : Decimal(9, 2)@readonly;
    currency : Currency;
}

entity OrderItems : cuid
{
    parent : Association to Orders;
    book : Association to my.Books;
    amount : Integer;
    netAmount : Decimal(9, 2);
}
