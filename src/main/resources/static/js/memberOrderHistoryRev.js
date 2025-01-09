// $(document).on("click", ".add-review", )

console.log("memberOrderHistoryRev.js");

async function fetchMemberReview(){
    const url = "/member/reviews/getMemberReviews"

    try{
        let res = await fetch(`/member/reviews/getMemberReviews`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            }
        });
        if(!res.ok){
            throw new Error("Failed to fetch reviews");
        }
        let memberReviews = await res.json();
        console.log(memberReviews);
        document.getElementById("reviewsList").innerHTML = "";
        let reviewCards = "";

        for (let review of memberReviews.data) {
            // console.log(reviews.product.productName);
            if (review.reviewImages.length === 0 || review.reviewImages === null) {
                reviewCards += `<div class="review-card" data-id="1">
                                    <div class="row">
                                        <div class="col pl-1">
                                            <img src="https://placehold.co/130x100" alt="${review.restName}" class="restaurant-image">
                                        </div>`
            } else {
                reviewCards +=
                        `<div class="review-card" data-id="1">
                            <div class="row">
                                <div class="col pl-1">
                                    <img src="data:image/jpeg; base64, ${review.reviewImages[0].reviewImage}" alt="${review.restName}" class="restaurant-image">
                                </div>`
            }
            reviewCards +=
                        `<div class="col-8">
                            <h5 class="mb-2">${review.restName}</h5>
                            <div class="star-rating mb-2">`
            
            for(let i = 0; i < review.reviewRating; i++){
                reviewCards += `<i class="fas fa-star"></i>`
            }
            for(let i = 0; i < 5 - review.reviewRating; i++){
                reviewCards += `<i class="far fa-star"></i>`
            }
            reviewCards +=  
                        `</div>
                        <div class="text-muted mb-2">用餐日期：${review.formattedServedDatetime}</div>
                        <div class="text-muted mb-2">評論日期：${review.formattedReviewDatetime}</div>
                        <div class="text-muted mb-2">人均消費：${review.avgCost}</div>
                        <div class="text-muted mb-2">最推薦餐點：${review.product.productName}</div>
                        <div class="review-content">
                            ${review.reviewText}
                            <span class="show-more">顯示更多</span>
                        </div>
                    </div>
                    <div class="col">
                        <div class="action-buttons mt-2">
                            <form action="/member/reviews/update" method="post">
                                <input type="hidden" name="reviewId" value="${review.reviewId}">
                                <button class="btn btn-outline-secondary edit-btn">
                                    編輯
                                </button>
                            </form>
                            <button class="btn delete-btn" onclick="showDeleteConfirm('${review.reviewId}')">
                                <i class="fas fa-trash-alt me-1"></i>刪除
                            </button>
                        </div>
                    </div>
                </div>
            </div>`;
        }

        document.getElementById("reviewsList").innerHTML = reviewCards;
    } catch (error){
        console.log(error);
        return [];
    }
    

}

$(document).on("click")


function showDeleteConfirm(reviewId){
    console.log("showDeleteConfirm called with reviewId:", reviewId);
    console.log(reviewId);

    const modal = document.getElementById("mealDetailsModal");
    let modalReview = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
    modalReview.show();
    // $("#cancelReviewdeleteBtn").off("click").on("click", function(){
    //     modalReview.hide();
    //     console.log("cancel");
    // })
    // $(".btn-close").off("click").on("click", function(){
    //     modalReview.hide();
    //     console.log("cancel");
    // })
    $("#confirmDeleteReview").off("click").on("click", function(){
        deleteReview(reviewId);
        modalReview.hide();
    })
    
}

async function deleteReview(reviewId){
    const url = "/member/reviews/deleteReview"
    let data = {
        "reviewId": reviewId
    }
    try{
        let res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(data)
        });

        if(!res.ok){
            throw new Error("Failed to fetch reviews which id = " + reviewId);
        }

        let response = await res.json();
        console.log(response);
        fetchMemberReview();

    } catch(error) {
        console.log(error);
        return [];
    }
}
    


$(document).ready(function () {
    fetchMemberReview();
});


// $(".modal").on("shown.bs.modal", function () {
//     if ($(".modal-backdrop").length > 1) {
//         console.log("more than one modal");
//         $(".modal-backdrop").not(':first').remove();
//     }
// })
