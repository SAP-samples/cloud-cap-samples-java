namespace my.bookshop;

using {Currency, sap, managed, cuid} from '@sap/cds/common';
using my.bookshop.Reviews from './reviews';
using my.bookshop.TechnicalBooleanFlag from './common';
using {my.common.Hierarchy as Hierarchy} from './hierarchy';

@fiori.draft.enabled
entity Books : cuid, managed {
    title        : localized String(111);
    descr        : localized String(1111);
    author       : Association to Authors;
    genre        : Association to Genres;
    stock        : Integer;
    price        : Decimal(9, 2);
    currency     : Currency;
    rating       : Decimal(2, 1);
    reviews      : Association to many Reviews
                       on reviews.book = $self;
    isReviewable : TechnicalBooleanFlag not null default true;
    contents     : Composition of many Contents on contents.book = $self @odata.contained:false;
}

entity Authors : cuid, managed {
    @assert.format: '^\p{Lu}.*' // assert that name starts with a capital letter
    name         : String(111);
    dateOfBirth  : Date;
    dateOfDeath  : Date;
    placeOfBirth : String;
    placeOfDeath : String;
    books        : Association to many Books
                       on books.author = $self;
    details      : Map;
}

// annotations for Data Privacy
annotate Authors with
@PersonalData: {
    DataSubjectRole: 'Author',
    EntitySemantics: 'DataSubject'
} {
    ID @PersonalData.FieldSemantics: 'DataSubjectID';
    name @PersonalData.IsPotentiallySensitive;
}

/**
 * Hierarchically organized Code List for Genres
 */
entity Genres : sap.common.CodeList, Hierarchy {
    key ID          : UUID;
        // move siblings
        siblingRank : Integer;
        parent      : Association to Genres;
        // for cascade delete
        children    : Composition of many Genres on children.parent = $self;
}


/**
 * Hierarchically organized entity for Contents
 */
entity Contents: Hierarchy {
    key ID     : UUID;
        name   : String;
        page   : Integer;
        parent : Association to Contents @odata.draft.enclosed;
        book   : Association to Books;
        // for cascade delete
        @cascade.delete
        @odata.draft.enclosed
        children : Association to many Contents on children.parent = $self;
}

