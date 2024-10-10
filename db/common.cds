namespace my.bookshop;
using {sap.attachments.Attachments} from `com.sap.cds/cds-feature-attachments`;

////////////////////////////////////////////////////////////////////////////
//
//	Commmon Types
//
type TechnicalBooleanFlag : Boolean @(
    UI.Hidden,
    Core.Computed
);

// annotate Attachments with {
//     modifiedAt @(odata.etag: null);
// }
