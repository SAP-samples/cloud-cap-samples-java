namespace my.bookshop;
using { managed, cuid } from '@sap/cds/common';

@assert.unique.chapterIndex: [book, number]
entity Chapters: cuid, managed {
  book: String @mandatory;
  number: Integer @mandatory;
  title: String @mandatory;
  wordCount: Integer64 @Core.Computed;
  pages: Composition of many Pages on $self = pages.chapter;
}

@assert.unique.pageIndex: [chapter, number]
entity Pages: cuid {
  chapter: Association to Chapters @mandatory @cascade: {insert, update};
  number: Integer @mandatory;
  content: String @mandatory;
}
