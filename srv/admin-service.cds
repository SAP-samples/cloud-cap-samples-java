using { sap.common.Languages as CommonLanguages } from '@sap/cds/common';
using { my.bookshop as my } from '../db/schema';

@path:'admin'
service AdminService @(requires:'admin') {
  entity Books as projection on my.Books;
  entity Authors as projection on my.Authors;
  entity Orders as select from my.Orders;
}

// Deep Search
annotate AdminService.Orders with {
  OrderNo @Search.defaultSearchElement;
  Items @Search.cascade;
}

annotate AdminService.OrderItems with {
  book  @Search.cascade;
}

annotate AdminService.Books with {
  descr @Search.defaultSearchElement;
  title @Search.defaultSearchElement;
}

// Enable Fiori Draft for Orders
annotate AdminService.Orders with @odata.draft.enabled;
annotate AdminService.Books with @Capabilities.Insertable: false;
annotate AdminService.Books with @odata.draft.enabled;

// Add Action to Books
extend entity AdminService.Books with actions {
  action addToOrder(order_ID: UUID, amount: Integer) returns AdminService.Orders;
}

// workaround to enable the value help for languages
// Necessary because auto exposure is currently not working
// for if Languages is only referenced by the generated
// _texts table
extend service AdminService with {
  entity Languages as projection on CommonLanguages;
}