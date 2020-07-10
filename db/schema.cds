namespace my.bookshop;
using { Currency, managed, cuid } from '@sap/cds/common';

@fiori.draft.enabled
entity Books : cuid, managed {
  title    : localized String(111);
  descr    : localized String(1111);
  author   : Association to Authors;
  stock    : Integer;
  price    : Decimal(9,2);
  currency : Currency;
}

entity Authors : cuid, managed {
  @assert.format: '^\p{Lu}.*' // assert that name starts with a capital letter
  name         : String(111);
  dateOfBirth  : Date;
  dateOfDeath  : Date;
  placeOfBirth : String;
  placeOfDeath : String;
  books        : Association to many Books on books.author = $self;
}

entity Orders : cuid, managed {
  OrderNo  : String @title:'{i18n>OrderNumber}' @mandatory; //> readable key
  Items    : Composition of many OrderItems on Items.parent = $self;
  total    : Decimal(9,2) @readonly;
  currency : Currency;
}

entity OrderItems : cuid {
  parent    : Association to Orders;
  book      : Association to Books;
  amount    : Integer;
  netAmount : Decimal(9,2);
}
