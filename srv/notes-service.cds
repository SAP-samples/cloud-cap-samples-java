using my.bookshop from '../db/notes';

@path: 'notes'
service NotesService {
    entity Notes as projection on bookshop.Notes;
}
