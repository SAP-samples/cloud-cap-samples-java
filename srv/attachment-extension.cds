using my.bookshop as my from '../db/books';
using {sap.attachments.Attachments, sap.attachments.StatusCode} from`com.sap.cds/sdm`;
using { sap.common.CodeList } from '@sap/cds/common';

extend entity my.Books with {
    attachments : Composition of many Attachments @SDM.Attachments:{maxCount: 4, maxCountError:'Only 4 attachments allowed.'};
    references  : Composition of many Attachments @SDM.Attachments:{maxCount: 5, maxCountError:'Only 5 attachments allowed.'};
    footnotes   : Composition of many Attachments;
}
extend entity my.Notebooks with {
    attachments : Composition of many Attachments @SDM.Attachments:{maxCount: 4, maxCountError:'Only 4 attachments allowed.'};
}
extend entity my.Chapters with { 
  attachments: Composition of many Attachments;
  references: Composition of many Attachments;
  footnotes: Composition of many Attachments;
}

extend entity my.Pages with { 
  attachments: Composition of many Attachments;
  references: Composition of many Attachments;
  footnotes: Composition of many Attachments;
}

entity Statuses @cds.autoexpose @readonly {
    key code : StatusCode;
        text : localized String(255);
}

extend Attachments with {
    statusText : Association to Statuses on statusText.code = $self.status;
    customProperty1 : WDIRS_CodeList_TYPE
        @SDM.Attachments.AdditionalProperty: {
            name: 'Working:DocumentInfoRecordString'
        } 
        @(title: 'DocumentInfoRecordString');
    customProperty2 : Integer
        @SDM.Attachments.AdditionalProperty: {
            name: 'Working:DocumentInfoRecordInt'
        };
    customProperty3 : String
    @SDM.Attachments.AdditionalProperty: {
        name: 'abc:myId1'
    }  
    @(title: 'id1');
    customProperty4 : String
    @SDM.Attachments.AdditionalProperty: {
        name: 'abc:myId2'
    }  
    @(title: 'id2');
    customProperty5 : DateTime
    @SDM.Attachments.AdditionalProperty: {
        name: 'Working:DocumentInfoRecordDate'
    }  
    @(title: 'DocumentInfoRecordDate');
    customProperty6 : Boolean
    @SDM.Attachments.AdditionalProperty: {
        name: 'Working:DocumentInfoRecordBoolean'
    }  
    @(title: 'DocumentInfoRecordBoolean');
}

entity WDIRSCodeList : CodeList {
    key code  : String(30) @Common.Text : name @Common.TextArrangement: #TextFirst;
};

type WDIRS_CodeList_TYPE : Association to one WDIRSCodeList;

annotate my.Books.attachments with {
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: {entity:'Statuses'}
    );
}

annotate my.Books.references with {
    content @Validation.Maximum : '100MB';
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: { entity: 'Statuses' },
        sap.value.list: 'fixed-values'
    );
}

annotate my.Chapters.attachments with {
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: { entity: 'Statuses' },
        sap.value.list: 'fixed-values'
    );
}

annotate my.Chapters.references with {
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: { entity: 'Statuses' },
        sap.value.list: 'fixed-values'
    );
}

annotate my.Pages.attachments with {
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: { entity: 'Statuses' },
        sap.value.list: 'fixed-values'
    );
}

annotate my.Pages.references with {
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: { entity: 'Statuses' },
        sap.value.list: 'fixed-values'
    );
}

annotate my.Chapters.footnotes with {
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: { entity: 'Statuses' },
        sap.value.list: 'fixed-values'
    );
}

annotate my.Pages.footnotes with {
    status @(
        Common.Text: {
            $value: ![statusText.text],
            ![@UI.TextArrangement]: #TextOnly
        },
        ValueList: { entity: 'Statuses' },
        sap.value.list: 'fixed-values'
    );
}
