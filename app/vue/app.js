/* global Vue axios */ //> from vue.html
const $ = (sel) => document.querySelector(sel);
const GET = (url) => axios.get("/api/ReviewsService" + url);
const PUT = (cmd, data) => axios.patch("/api/ReviewsService" + cmd, data);
const POST = (cmd, data) => axios.post("/api/ReviewsService" + cmd, data);

const reviews = new Vue({
  el: "#app",

  data: {
    list: [],
    review: undefined,
    message: {},
    Ratings: Object.entries({
      5: "★★★★★",
      4: "★★★★",
      3: "★★★",
      2: "★★",
      1: "★",
    }).reverse(),
  },

  methods: {
    search: ({ target: { value: v } }) => reviews.fetch(v && "&$search=" + v),

    async fetch(etc = "") {
      const { data } = await GET(`/Reviews?${etc}`);
      reviews.list = data.value;
    },

    async inspect(eve) {
      const review = (reviews.review =
        reviews.list[eve.currentTarget.rowIndex - 1]);
      const res = await GET(`/Reviews(ID=${review.ID})/text/$value`);
      review.text = res.data;
      reviews.message = {};
    },

    newReview() {
      reviews.review = {};
      reviews.message = {};
      setTimeout(() => $("form > input").focus(), 111);
    },

    async submitReview() {
      const review = reviews.review;
      review.rating = parseInt(review.rating); // REVISIT: Okra should be less strict
      try {
        if (!review.ID) {
          const res = await POST(`/Reviews`, review);
          reviews.ID = res.data.ID;
        } else {
          console.trace();
          await PUT(`/Reviews(ID=${review.ID})`, review);
        }
        reviews.message = {
          succeeded: "Your review was submitted successfully. Thanks.",
        };
        document.location.reload();
      } catch (e) {
        reviews.message = { failed: e.response.data.error.message };
      }
    },
  },

  filters: {
    stars: (r) => ("★".repeat(Math.round(r)) + "☆☆☆☆☆").slice(0, 5),
    datetime: (d) => d && new Date(d).toLocaleString(),
  },
});

// initially fill list of my reviews
reviews.fetch();
