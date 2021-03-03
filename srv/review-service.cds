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

    // input validation
    annotate Reviews with {
        subject @mandatory;
        title   @mandatory;
        rating  @assert.enum;
    }

    // access control restrictions
    annotate ReviewService.Reviews with @restrict : [
    {
        grant : 'READ',
        to    : 'any'
    },
    {
        grant : 'CREATE',
        to    : 'authenticated-user'
    },
    {
        grant : 'UPDATE',
        to    : 'authenticated-user',
        where : 'reviewer=$user'
    },
    {
        grant : 'DELETE',
        to    : 'admin'
    },
    {
        grant : 'DELETE',
        to    : 'authenticated-user',
        where : 'reviewer=$user'
    }
    ];
}

annotate ReviewService.Reviews with @odata.draft.enabled;
