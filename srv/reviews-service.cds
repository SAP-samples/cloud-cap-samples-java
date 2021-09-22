using {my.reviews } from '../db/reviews';

service ReviewsService {
    
    // Sync API
    entity Reviews as projection on reviews.Reviews;

    // Async API
    event reviewed : {
     subject: type of Reviews:subject;
     rating: Decimal(2,1)
    }

	action someTests ( message: String );

    // access control restrictions
    annotate Reviews with @restrict : [
        {
            grant : 'WRITE',
            to : 'authenticated-user',
            where : 'createdBy=$user'
        },
        {
            grant : 'WRITE',
            to : 'admin',
        },
		{
            grant : 'READ',
            to : 'any',
        }
    ];
}