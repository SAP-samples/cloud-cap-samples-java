using {sap.common.Languages as CommonLanguages} from '@sap/cds/common';
using {my.bookshop as my} from '../db/index';
using {sap.changelog as changelog} from 'com.sap.cds/change-tracking';
using {my.admin.bookshop.Hierarchy as Hierarchy} from './genres';

extend my.Orders with changelog.changeTracked;

@path : 'admin'
@odata.apply.transformations
service AdminService @(requires : 'admin') {
  entity Books   as projection on my.Books excluding { reviews } actions {
      action addToOrder(order_ID : UUID, quantity : Integer) returns Orders;
    }

  entity Authors as projection on my.Authors;
  entity Orders  as select from my.Orders;
  extend my.Genres with Hierarchy;

  entity GenreHierarchy as projection on my.Genres {
    node   as node_id,
    parent_node as parent_id,
    *
  } excluding { node, parent_node }

  @cds.persistence.skip
  entity Upload @odata.singleton {
    csv : LargeBinary @Core.MediaType : 'text/csv';
  }
}

// Deep Search Items
annotate AdminService.Orders with @cds.search : {
  OrderNo,
  Items
};

annotate AdminService.OrderItems with @cds.search : {book};

annotate AdminService.Books with @cds.search : {
  descr,
  title
};

// Enable Fiori Draft for Orders
annotate AdminService.Orders with @odata.draft.enabled;
annotate AdminService.Books with @odata.draft.enabled;

// workaround to enable the value help for languages
// Necessary because auto exposure is currently not working
// for if Languages is only referenced by the generated
// _texts table
extend service AdminService with {
  entity Languages as projection on CommonLanguages;
}

// Change-track orders and items
annotate AdminService.Orders {
  OrderNo @changelog;
};

annotate AdminService.OrderItems {
  quantity @changelog;
  book @changelog: [
    book.title,
    book.isbn
  ]
};

// Assign identifiers to the tracked entities
annotate AdminService.Orders with @changelog: [OrderNo];
annotate AdminService.OrderItems with @changelog: [
  parent.OrderNo,
  book.title,
];