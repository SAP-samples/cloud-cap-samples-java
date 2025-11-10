using {sap.common.Languages as CommonLanguages} from '@sap/cds/common';
using {my.bookshop as my} from '../db/index';
using {sap.changelog as changelog} from 'com.sap.cds/change-tracking';

extend my.Orders with changelog.changeTracked;

@path : 'admin'
service AdminService @(requires: [
    'admin',
    'system-user'
]) {
  entity Books   as projection on my.Books excluding { reviews } actions {
    action addToOrder(order_ID : UUID, quantity : Integer) returns Orders;
  }

  entity Chapters as projection on my.Chapters;
  entity Pages as projection on my.Pages;

  entity Books.attachments as projection on my.Books.attachments
  actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self,up__ID:String,objectIds:String);
    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    
    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    action openAttachment() returns String;
  }

  entity Books.references as projection on my.Books.references
  actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self,up__ID:String,objectIds:String);
    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    
    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    );   
    action openAttachment() returns String;
  }

  entity Books.footnotes as projection on my.Books.footnotes
  actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self,up__ID:String,objectIds:String);
    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    
    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    );  
    action openAttachment() returns String;
  }

  entity Pages.attachments as projection on my.Pages.attachments
    actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self, up__ID:String, objectIds:String);

    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 

    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    action openAttachment() returns String;
  };

  entity Pages.references as projection on my.Pages.references
    actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self, up__ID:String, objectIds:String);

    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 

    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    action openAttachment() returns String;
  };

  // Chapters projections
  entity Chapters.attachments as projection on my.Chapters.attachments
    actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self, up__ID:String, objectIds:String);

    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 

    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    action openAttachment() returns String;
  };

  entity Chapters.references as projection on my.Chapters.references
    actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self, up__ID:String, objectIds:String);

    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 

    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    action openAttachment() returns String;
  };

  entity Chapters.footnotes as projection on my.Chapters.footnotes
    actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self, up__ID:String, objectIds:String);

    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 

    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    action openAttachment() returns String;
  };

  // Pages footnotes projection
  entity Pages.footnotes as projection on my.Pages.footnotes
    actions {
    @(Common.SideEffects : {TargetEntities: ['']},)
    action copyAttachments(in:many $self, up__ID:String, objectIds:String);

    @(Common.SideEffects : {TargetEntities: ['']},)
    action createLink(
      in:many $self,
      @mandatory @Common.Label:'Name' name: String @UI.Placeholder: 'Enter a name for the link',
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 

    action editLink(
      @mandatory @assert.format:'^(https?:\/\/)(([a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}|localhost)(:\d{2,5})?(\/[^\s]*)?$' @Common.Label:'URL' url: String @UI.Placeholder: 'Example: https://www.example.com'
    ); 
    action openAttachment() returns String;
  };

  entity Authors as projection on my.Authors;
  entity Orders  as select from my.Orders;

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
