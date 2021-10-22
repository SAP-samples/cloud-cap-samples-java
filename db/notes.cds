namespace my.bookshop;

using { cuid } from '@sap/cds/common';

entity Notes: cuid {
    note: String;
}
