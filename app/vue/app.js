/* global Vue axios */ //> from vue.html
const $ = sel => document.querySelector(sel)
const GET = (url) => axios.get('/api/browse'+url)
const POST = (cmd,data) => axios.post('/api/browse'+cmd,data)

const bookshop = new Vue ({

    el:'#app',

    data: {
        books: [],
        reviews: [],
        book: undefined,
        review: undefined,
        order: { amount:1, succeeded:'', failed:'' },
        message: {},
        Ratings: Object.entries({
            5 : '★★★★★',
            4 : '★★★★',
            3 : '★★★',
            2 : '★★',
            1 : '★',
        }).reverse()
    },

    methods: {

        searchBooks: ({target:{value:v}}) => bookshop.fetchBooks(v && '&$search='+v),
        
        searchReviews: ({target:{value:v}}) => bookshop.fetchReviews(v && '?$search='+v),

        async fetchBooks (etc='') {
            const {data} = await GET(`/Books?$expand=author,genre,currency${etc}`)
            bookshop.books = data.value
        },
        
        async fetchReviews (etc='') {
            const {data} = await GET(`/Books(ID=${bookshop.book.ID})/reviews${etc}`)
            bookshop.reviews = data.value
        },

        async inspectBook (eve) {
            const book = bookshop.book = bookshop.books [eve.currentTarget.rowIndex-1]
            const res = await GET(`/Books(ID=${book.ID})?$expand=reviews&$select=descr,stock`)
            Object.assign (book, res.data)
            bookshop.order = { amount:1 }
            bookshop.reviews = book.reviews
            setTimeout (()=> $('form > input').focus(), 111)
        },

        async submitOrder () {
            const {book,order} = bookshop, amount = parseInt (bookshop.order.amount) || 1 // REVISIT: Okra should be less strict
            try {
                const res = await POST(`/submitOrder`, { amount, book: book.ID })
                book.stock = res.data.stock
                bookshop.order = { amount, succeeded: `Successfully orderd ${amount} item(s).` }
            } catch (e) {
                bookshop.order = { amount, failed: e.response.data.error.message }
            }
        },
        
        async inspectReview (eve) {
        	bookshop.review = bookshop.reviews [eve.currentTarget.rowIndex-1]
        },
        
        newReview () {
            bookshop.review = {}
            bookshop.message = {}
        },
        
        async submitReview () {
            const review = bookshop.review; review.rating = parseInt (review.rating) // REVISIT: Okra should be less strict
            const payload = {rating: review.rating, title: review.title, text: review.text}
            try {
                await POST(`/Books(ID=${bookshop.book.ID})/CatalogService.addReview`, payload)
                bookshop.message = { succeeded: 'Your review was submitted successfully. Thanks.' }
            } catch (e) {
                bookshop.message = { failed: e.response.data.error.message }
            }
        }

    }
})

// initially fill list of books
bookshop.fetchBooks()