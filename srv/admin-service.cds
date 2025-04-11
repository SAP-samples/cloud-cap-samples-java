using {sap.common.Languages as CommonLanguages, cuid} from '@sap/cds/common';
using {my.bookshop as my} from '../db/index';
using {sap.changelog as changelog} from 'com.sap.cds/change-tracking';
using {sap.attachments.Attachments} from 'com.sap.cds/cds-feature-attachments';

extend my.Orders with changelog.changeTracked;

@path: 'admin'
@odata.apply.transformations
service AdminService @(requires: 'admin') {
  entity Books          as
    projection on my.Books
    excluding {
      reviews
    }
    actions {
      action addToOrder(order_ID : UUID, quantity : Integer) returns Orders;
    }

  entity Authors        as projection on my.Authors;
  entity Orders         as select from my.Orders;

  type NextSibling : cuid { };
  entity GenreHierarchy as projection on my.Genres
    excluding {children} order by siblingRank
    actions {
      // HierarchySiblingActionHandler.java
      action moveSibling(NextSibling : NextSibling); // to be implemented in custom handler 
    };

  entity ContentsHierarchy as projection on my.Contents;

  @cds.persistence.skip
  entity Upload @odata.singleton {
    csv : LargeBinary @Core.MediaType: 'text/csv';
  }

  @cds.persistence.skip
  @readonly entity Info @odata.singleton {
    hideTreeTable: Boolean;
  }
}

// Deep Search Items
annotate AdminService.Orders with @cds.search: {
  OrderNo,
  Items
};

annotate AdminService.OrderItems with @cds.search: {book};

annotate AdminService.Books with @cds.search: {
  descr,
  title
};

// Enable Fiori Draft for Orders
annotate AdminService.Orders with @odata.draft.enabled;
annotate AdminService.Books with @odata.draft.enabled;
annotate AdminService.GenreHierarchy with @odata.draft.enabled;

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

// Extends the Books entity with the Attachments composition
extend my.Books with {
  covers : Composition of many Attachments;
};
