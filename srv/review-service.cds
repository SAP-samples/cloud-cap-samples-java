using {my.bookshop as my} from '../db/index';

@path : 'review'
service ReviewService {
    entity Reviews as projection on my.Reviews;

    @readonly
    entity Books   as projection on my.Books excluding {
        createdBy,
        modifiedBy
    }

    @readonly
    entity Authors as projection on my.Authors;

    // access control restrictions
    annotate Reviews with @restrict : [
        {
            grant : '*',
            to : 'authenticated-user',
            where : 'createdBy=$user'
        },
        {
            grant : '*',
            to : 'admin',
        }
    ];
}

annotate ReviewService.Reviews with @odata.draft.enabled;
