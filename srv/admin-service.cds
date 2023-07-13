using {sap.common.Languages as CommonLanguages} from '@sap/cds/common';
using {my.bookshop as my} from '../db/schema';

@path: 'admin'
service AdminService @(requires: 'admin') {
  @odata.draft.enabled
  entity Books     as projection on my.Books;

  entity Authors   as projection on my.Authors;
}

// workaround to enable the value help for languages
// Necessary because auto exposure is currently not working
// for if Languages is only referenced by the generated
// _texts table
extend service AdminService with {
  entity Languages as projection on CommonLanguages;
}
