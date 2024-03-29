package com.shows_lesdominik

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.Executors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel(private val database: ShowsDatabase) : ViewModel() {

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private val _newReviewLiveData = MutableLiveData<Review>()
    val newReviewLiveData: LiveData<Review> = _newReviewLiveData

    private val _showDetailsLiveData = MutableLiveData<Show>()
    val showDetailsLiveData: LiveData<Show> = _showDetailsLiveData

    private var noOfReviews: Int? = null
    private var showAvgRating: Float? = null


    fun getNoOfReviews() = noOfReviews

    fun getShowAvgRating() = showAvgRating

    fun getShowDetails(showId: String) {
        ApiModule.retrofit.getShowDetails(showId)
            .enqueue(object: Callback<ShowDetailsResponse> {
                override fun onResponse(call: Call<ShowDetailsResponse>, response: Response<ShowDetailsResponse>) {
                    if (response.isSuccessful) {
                        _showDetailsLiveData.value = response.body()?.show
                        noOfReviews = response.body()?.show?.noOfReviews
                        showAvgRating = response.body()?.show?.averageRating
                    } else {
                        _showDetailsLiveData.value = null
                    }
                }

                override fun onFailure(call: Call<ShowDetailsResponse>, t: Throwable) {
                    _showDetailsLiveData.value = null
                }

            })
    }

    fun getShowDetailsFromDatabase(showId: String): LiveData<ShowEntity> {
        return database.showDao().getShow(showId)
    }

    fun getShowReviews(showId: String) {
        ApiModule.retrofit.getShowReviews(showId)
            .enqueue(object: Callback<ReviewListResponse> {
                override fun onResponse(call: Call<ReviewListResponse>, response: Response<ReviewListResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { addReviewsToDatabase(it.reviews) }
                        _reviewsLiveData.value = response.body()?.reviews
                    } else {
                        _reviewsLiveData.value = emptyList()
                    }
                }

                override fun onFailure(call: Call<ReviewListResponse>, t: Throwable) {
                    _reviewsLiveData.value = emptyList()
                }

            })
    }

    fun addReviewsToDatabase(reviews: List<Review>) {
        Executors.newSingleThreadExecutor().execute {
            database.reviewDao().createReviews(reviews.map { review ->
                ReviewEntity(
                    review.id,
                    review.comment,
                    review.rating,
                    review.showId,
                    review.user.id,
                    review.user.email,
                    review.user.imageUrl
                )
            })
        }
    }

    fun getReviewsFromDatabase(showId: String): LiveData<List<ReviewEntity>> {
        return database.reviewDao().getAllReviews(showId)
    }

    fun addReview(rating: Int, comment: String?, showId: String) {
        val reviewCreateRequest = ReviewCreateRequest(
            rating = rating,
            comment = comment,
            showId = showId.toInt()
        )

        noOfReviews = noOfReviews?.plus(1)

        ApiModule.retrofit.createShowReview(reviewCreateRequest)
            .enqueue(object: Callback<ReviewCreateResponse> {
                override fun onResponse(call: Call<ReviewCreateResponse>, response: Response<ReviewCreateResponse>) {
                    if (response.isSuccessful) {
                        val review = response.body()?.review
                        addReviewsToDatabase(listOf(review) as List<Review>)
                        _newReviewLiveData.value = response.body()?.review
                    }
                    _newReviewLiveData.value = null
                }

                override fun onFailure(call: Call<ReviewCreateResponse>, t: Throwable) {
                    _newReviewLiveData.value = null
                }

            })
    }
}