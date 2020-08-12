using { sap.common.Languages as CommonLanguages } from '@sap/cds/common';
using { my.bookshop as my } from '../db/schema';

@path:'admin'
service AdminService @(requires:'admin') {
  entity Books as projection on my.Books actions {
    action addToOrder(order_ID: UUID, amount: Integer) returns Orders;
  }
  entity Authors as projection on my.Authors;
  entity Orders as select from my.Orders;
}

// Deep Search Items
annotate AdminService.Orders with @cds.search : {OrderNo, Items};
annotate AdminService.OrderItems with @cds.search : {book};
annotate AdminService.Books with @cds.search : {descr, title};

// Enable Fiori Draft for Orders
annotate AdminService.Orders with @odata.draft.enabled;
annotate AdminService.Books with @odata.draft.enabled @Capabilities.Insertable: false;

// workaround to enable the value help for languages
// Necessary because auto exposure is currently not working
// for if Languages is only referenced by the generated
// _texts table
extend service AdminService with {
  entity Languages as projection on CommonLanguages;
}
