/**
 * 
 */



$(document).ready(function() {
	const tabs = $(".nav-link");
	const content = $(".tab-content");



	const diningPrevButton = $("#diningPreBtn.prev-btn");
	const diningNextButton = $("#diningNextBtn.next-btn");
	$(diningPrevButton).on("click", function() {
		const pageNumElement = $("#diningPageNum.page-num");
		let page = pageNumElement.text();
		// console.log(page)
		let pageIndex = parseInt(page) - 1;

		loadOrderData(pageIndex - 1, currentSort.field, currentSort.order);
	});

	$(diningNextButton).on("click", function() {
		const pageNumElement = $("#diningPageNum.page-num");
		let page = pageNumElement.text();
		// console.log(page)
		let pageIndex = parseInt(page) - 1;

		loadOrderData(pageIndex + 1, currentSort.field, currentSort.order);
	});


	const size = 10;

	//for dining
	function loadOrderData(page, sortField, sortOrder) {
		fetch(`member/orderMaster/orders?page=${page}&size=${size}&sort=${sortField},${sortOrder}`, {
			method: "GET",
		}).then(res => res.json())
			.then(response => {
				// console.log(response);

				const pageData = response.data;
				const orderList = pageData.content;
				const totalPages = pageData.totalPages;
				const newParams = `${sortField},${sortOrder}`;

				let table_body = "";
				orderList.forEach((orderMaster, index) => {

					const [date, time] = orderMaster.servedDatetime.split(" ");
					const formAction = orderMaster.reviewId ? "/member/reviews/getReview" : "/member/reviews/addReview";
					const btnClass = orderMaster.reviewId ? "btn btn-outline-secondary btn-sm check-review" : "btn btn-primary btn-sm add-review";
					const btnText = orderMaster.reviewId ? "查看評論" : "進行評論"

					table_body += `
	                    <tr>
	                        <td class="px-4 py-3 rest-column">${orderMaster.restName}</td>
	                        <td class="px-4 py-3">${date}</td>
	                        <td class="px-4 py-3">${time}</td>
	                    	<td class="px-4 py-3">${orderMaster.totalPrice}</td>
	                        <td class="px-4 py-3"><a href="javascript:void(0);" class="show-detail">
	                                <i class="fa-solid fa-arrow-up-right-from-square" data-orderId=${orderMaster.orderId}></i></a>
	                        </td>        
	                    	<td class="px-4 py-3">
	                            ${orderMaster.reviewId ? `<span class="status-badge status-completed">已評論</span>` :
							`<span class="status-badge status-pending">未評論</span>`}
	                        </td>
	                    	<td class="px-4 py-3">${orderMaster.pointEarned}</td>
	                    	<td class="px-4 py-3">
								<form action="${formAction}" method="post">
									<input type="hidden" value="${orderMaster.orderId}" name="orderId">
									<button class="${btnClass}" type="submit">${btnText}</button>
								</form>
	                    	</td>
	                    </tr>`;
				});

				document.getElementById("order-tbody").innerHTML = table_body;

				const detailLinks = document.querySelectorAll('td a i.fa-arrow-up-right-from-square');
				// console.log(detailLinks);
				detailLinks.forEach((link) =>
					link.addEventListener("click", function(event) {
						event.preventDefault();
						let orderId = $(this).attr("data-orderId");

						showModal(orderId);
					})
				);
				renderPage(page, totalPages, "dining");
			});
	}

	let currentSort = { field: "b", order: "desc" };
	//載入頁面時，撈第一頁的資料
	loadOrderData(0, currentSort.field, currentSort.order);

	// 排序功能
	$('.sort-icon').click(function() {
		const sortField = $(this).data('sort');
		//                 console.log(sortField);
		const currentOrder = currentSort.field === sortField && currentSort.order === 'asc' ? 'desc' : 'asc';

		currentSort = { field: sortField, order: currentOrder };
		$('.sort-icon').data('order', '');
		$(this).data('order', currentOrder);

		const pageNumElement = $("#diningPageNum.page-num");
		let page = pageNumElement.text();
		// console.log(page)
		let pageIndex = parseInt(page) - 1;
		let currentPage = pageIndex;

		loadOrderData(currentPage, currentSort.field, currentSort.order);
	});

	//分頁功能
	function renderPage(currentPage, totalPages, tabType) {
		if (tabType == "dining") {
			const pagination = $("#diningPageBlock.pagination");
			const pageNumElement = $("#diningPageNum.page-num");

			pageNumElement.text(currentPage + 1);

			const prevButton = $("#diningPreBtn.prev-btn");
			const nextButton = $("#diningNextBtn.next-btn");

			prevButton.prop("disabled", currentPage === 0);
			nextButton.prop("disabled", currentPage === totalPages - 1);

		} else {
			const pagination = $("#reviewPageBlock.pagination");
			const pageNumElement = $("#reviewPageNum.page-num");

			pageNumElement.text(currentPage + 1);

			const prevButton = $("#reviewPreBtn.prev-btn");
			const nextButton = $("#reviewNextBtn.next-btn");

			prevButton.prop("disabled", currentPage === 0);
			nextButton.prop("disabled", currentPage === totalPages - 1);
		}
	}


	//評論的分頁功能

	// const reviewPrevButton = $("#reviewPreBtn.prev-btn");
	// const reviewNextButton = $("#reviewNextBtn.next-btn");
	// $(reviewPrevButton).on("click", function () {
	//     const pageNumElement = $("#reviewPageNum.page-num");
	//     let page = pageNumElement.text();
	//     console.log(page)
	//     let pageIndex = parseInt(page) -1;

	//     loadReviewData(pageIndex - 1);
	// });

	// $(reviewNextButton).on("click", function () {
	//     const pageNumElement = $("#reviewPageNum.page-num");
	//     let page = pageNumElement.text();
	//     console.log(page)
	//     let pageIndex = parseInt(page) -1;

	//     loadReviewData(pageIndex + 1);
	// });

	// loadReviewData(0);

	//頁面切換
	tabs.on("click", function(e) {
		e.preventDefault();
		tabs.removeClass("active");
		content.removeClass("active");
		// console.log(this);
		$(this).addClass("active");

		const targetId = $(this).data("target");
		// console.log(targetId);
		$(`#${targetId}`).addClass("active");
	});


});


// 顯示 Modal(餐點明細)
function showModal(orderId) {

	fetch(`member/orderLineItem/items?orderId=${orderId}`, {
		method: "GET",
	}).then(res => res.json())
		.then(response => {

			// console.log(response);
			const mealDetailList = response.orderItemListDto;
			// console.log(mealDetailList);



			let mealDetail_body = "";

			mealDetailList.forEach((orderItem, index) => {

				const mealLineList = orderItem.lineItemList;
				let mealLineDetail_body = "";

				mealLineList.forEach((orderLineItem, index) => {

					mealLineDetail_body += `
                            	<div class="flex justify-between items-center mb-1 p-2 border-b">
	                                <span style="margin-left: 40px">${orderLineItem.productName} x ${orderLineItem.quantity}</span>
	                                <span class="text-gray-600  flex-grow text-right">$ ${orderLineItem.price}</span>
	                            </div>

                            `;
				});


				mealDetail_body += `
                            <div class="order-section mb-6">
                                <h4 class="text-lg font-semibold text-gray-800" style="margin-left: 20px">第${index + 1}次點餐</h4>
                                <div>${mealLineDetail_body}
                                  ${orderItem.memo ? `<div class="text-sm text-gray-500 mb-3 mt-3" style="margin-left: 20px;">備註: ${orderItem.memo}</div>` : ''}
                                </div >
                            </div >
                            `;

			});

			let mealSummary = "";

			mealSummary += `
                        <div class="summary">
                                <p class="text-gray-600">小計: <span id="discountPoints">${response.subtotalPrice}</span></p>
                                <p class="text-gray-600">折扣點數: <span id="discountPoints">${response.pointUsed}點   (＝折抵${response.pointUsed * 10} 元)</span></p>
                                <p class="text-xl font-semibold">訂單總金額: <span id="totalAmount">${response.totalPrice}</span></p>
                        </div>
                        
                        `;

			// console.log(mealDetail_body);
			document.querySelector(".modal-body").innerHTML = mealDetail_body;
			document.querySelector(".modal-footer").innerHTML = mealSummary;


		});


	const modal = document.getElementById("mealDetailsModal");

	// 顯示 Modal
	modal.classList.add("show");
}

// 關閉 Modal
function closeModal() {
	const modal = document.getElementById("mealDetailsModal");
	modal.classList.remove("show");
}



document.addEventListener("DOMContentLoaded", function () {
    const tabs = document.querySelectorAll(".nav-link");
    const content = document.querySelectorAll(".tab-content");

    tabs.forEach(function (tab) {
        tab.addEventListener("click", function (e) {
            e.preventDefault();

            tabs.forEach(function (tab) {
                tab.classList.remove("active");
            });
            content.forEach(function (cont) {
                cont.classList.remove("active");
            });

            tab.classList.add("active");

            const targetId = tab.getAttribute("data-target");
            document.getElementById(targetId).classList.add("active");
        });
    });
});



// Sample review data
const reviews = [
	{
		id: 1,
		restaurantName: '麥x勞南京復興店',
		rating: 4,
		diningDate: '2024/11/06 17:00',
		averageCost: '400-800',
		recommendedDish: '麥克雞塊牛堡',
		reviewContent: '就是麥當勞，環境整潔，服務親切，餐點出餐速度快。雖然價格比以前貴了不少，但還是會想來吃。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 2,
		restaurantName: '鼎泰豐信義店',
		rating: 5,
		diningDate: '2024/11/05 12:30',
		averageCost: '800-1200',
		recommendedDish: '小籠包、蝦仁燒賣',
		reviewContent: '不愧是米其林推薦餐廳，小籠包的湯汁很多，皮薄餡多，蝦仁燒賣也很新鮮。服務人員訓練有素，環境整潔明亮。雖然要排隊但值得一等。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 3,
		restaurantName: '日本料理無二',
		rating: 5,
		diningDate: '2024/11/04 19:00',
		averageCost: '1500-2500',
		recommendedDish: '生魚片、烤物套餐',
		reviewContent: '食材新鮮度極高，主廚發揮的很好，每道菜都很精緻。服務細心，用餐環境安靜舒適。適合商務約會或節慶慶祝。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 4,
		restaurantName: '春水堂中山店',
		rating: 3,
		diningDate: '2024/11/03 15:00',
		averageCost: '200-400',
		recommendedDish: '珍珠奶茶、蔥油餅',
		reviewContent: '珍奶還是一樣好喝，但服務品質不太穩定，這次等了很久才上餐。餐點份量還可以，價格偏高。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 5,
		restaurantName: '金峰魯肉飯',
		rating: 4,
		diningDate: '2024/11/02 11:30',
		averageCost: '100-200',
		recommendedDish: '魯肉飯、貢丸湯',
		reviewContent: '傳統的好味道，魯肉香而不膩，飯粒分明。價格實惠，服務快速。用餐環境普通，建議避開用餐尖峰時間。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 6,
		restaurantName: '青花驕麻辣鍋',
		rating: 5,
		diningDate: '2024/11/01 18:30',
		averageCost: '800-1200',
		recommendedDish: '麻辣鍋底、手工丸子',
		reviewContent: '湯底香辣夠味，食材新鮮，服務很好。環境乾淨，通風良好。適合朋友聚餐。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 7,
		restaurantName: '添好運點心',
		rating: 4,
		diningDate: '2024/10/31 12:00',
		averageCost: '400-600',
		recommendedDish: '叉燒包、蝦餃',
		reviewContent: '港式點心的好選擇，價格合理，品質穩定。服務算快，但用餐環境較擁擠吵雜。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 8,
		restaurantName: 'Mr. 和牛燒肉',
		rating: 5,
		diningDate: '2024/10/30 19:30',
		averageCost: '2000-3000',
		recommendedDish: 'A5和牛、醃製牛舌',
		reviewContent: '肉質極好，服務優質，座位間隔寬敞舒適。價格偏高但物有所值，適合重要場合。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 9,
		restaurantName: '瓦城泰式料理',
		rating: 3,
		diningDate: '2024/10/29 18:00',
		averageCost: '600-800',
		recommendedDish: '打拋豬、泰式奶茶',
		reviewContent: '味道普通，份量足夠，服務一般。餐廳有點吵雜，建議改善通風。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 10,
		restaurantName: '漢堡王市府店',
		rating: 3,
		diningDate: '2024/10/28 12:30',
		averageCost: '200-400',
		recommendedDish: '華堡、薯條',
		reviewContent: '一般連鎖速食店的水準，服務快速但不夠親切。環境還好，座位有點擁擠。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 11,
		restaurantName: '涓豆腐',
		rating: 4,
		diningDate: '2024/10/27 19:00',
		averageCost: '400-600',
		recommendedDish: '海鮮豆腐煲、韓式炸雞',
		reviewContent: '豆腐煲很好吃，份量充足，服務態度佳。餐廳裝潢有特色，氣氛不錯。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	},
	{
		id: 12,
		restaurantName: '樂麵屋',
		rating: 4,
		diningDate: '2024/10/26 11:30',
		averageCost: '200-400',
		recommendedDish: '豚骨拉麵、炸豬排',
		reviewContent: '湯頭濃郁，麵條彈牙，配料新鮮。服務快速，環境乾淨。',
		restaurantImageUrl: 'https://placehold.co/100x100'
	}
];

// Pagination settings
const itemsPerPage = 5;
let currentPage = 1;

// Initialize
$(document).ready(function() {
	renderPage(currentPage);
	setupEventHandlers();
});

// Render reviews for current page
function renderPage(page) {
	const container = $('#reviewsList');
	container.empty();

	// Calculate start and end index for current page
	const startIndex = (page - 1) * itemsPerPage;
	const endIndex = startIndex + itemsPerPage;
	const pageReviews = reviews.slice(startIndex, endIndex);

	// Render reviews
	pageReviews.forEach(review => {
		const stars = generateStars(review.rating);

		container.append(`
                   <div class="review-card" data-id="${review.id}">
                       <div class="row">
                           <div class="col-2">
                               <img src="${review.restaurantImageUrl}" alt="${review.restaurantName}" class="restaurant-image">
                           </div>
                           <div class="col-8">
                               <h5 class="mb-2">${review.restaurantName}</h5>
                               <div class="star-rating mb-2">${stars}</div>
                               <div class="text-muted mb-2">用餐日期：${review.diningDate}</div>
                               <div class="text-muted mb-2">人均消費：${review.averageCost}</div>
                               <div class="text-muted mb-2">最推薦餐點：${review.recommendedDish}</div>
                               <div class="review-content">
                                   ${review.reviewContent}
                                   <span class="show-more">顯示更多</span>
                               </div>
                           </div>
                           <div class="col-2">
                               <div class="action-buttons">
                                   <button class="btn btn-outline-secondary edit-btn">編輯</button>
                                   <button class="btn delete-btn">
                                       <i class="fas fa-trash-alt me-1"></i>刪除
                                   </button>
                               </div>
                           </div>
                       </div>
                   </div>
               `);

	});

	// Update pagination
	renderPagination();
}

// Render pagination controls
function renderPagination() {
	const totalPages = Math.ceil(reviews.length / itemsPerPage);
	const pagination = $('#pagination');
	pagination.empty();

	//     // Previous button
	pagination.append(`
                           < li class="page-item ${currentPage === 1 ? 'disabled' : ''}" >
                               <a class="page-link" href="#" data-page="${currentPage - 1}">上一頁</a>
               </li >
                           `);

	// Page numbers
	for (let i = 1; i <= totalPages; i++) {
		pagination.append(`
                           < li class="page-item ${currentPage === i ? 'active' : ''}" >
                               <a class="page-link" href="#" data-page="${i}">${i}</a>
                   </li >
                           `);
	}

	// Next button
	pagination.append(`
                           < li class="page-item ${currentPage === totalPages ? 'disabled' : ''}" >
                               <a class="page-link" href="#" data-page="${currentPage + 1}">下一頁</a>
               </li >
                           `);
}

// Generate star rating HTML
function generateStars(rating) {
	let stars = '';
	for (let i = 1; i <= 5; i++) {
		stars += `<i class="${i <= rating ? 'fas' : 'far'} fa-star"></i>`;
	}
	return stars;
}


// Setup event handlers
function setupEventHandlers() {
	// Pagination click handler
	$(document).on('click', '.page-link', function(e) {
		e.preventDefault();
		const newPage = $(this).data('page');
		if (newPage && newPage !== currentPage &&
			newPage >= 1 &&
			newPage <= Math.ceil(reviews.length / itemsPerPage)) {
			currentPage = newPage;
			renderPage(currentPage);
		}
	});

	// Show more button
	$(document).on('click', '.show-more', function() {
		const content = $(this).closest('.review-content');
		content.toggleClass('expanded');
		$(this).text(content.hasClass('expanded') ? '顯示較少' : '顯示更多');
	});

	// Edit button
	$(document).on('click', '.edit-btn', function() {
		const reviewId = $(this).closest('.review-card').data('id');
		// Implement edit functionality
		console.log('Edit review:', reviewId);
	});

	// Delete button
	// $(document).on('click', '.delete-btn', function() {
	// 	const reviewId = $(this).closest('.review-card').data('id');
	// 	// showDeleteConfirm(reviewId);
	// });

	// Confirm delete
	// $('#confirmDelete').on('click', function() {
	// 	const reviewId = $(this).data('reviewId');
	// 	deleteReview(reviewId);
	// 	$('#deleteConfirmModal').modal('hide');
	// });
}

// Show delete confirmation
function showDeleteConfirm(reviewId) {
	$('#confirmDelete').data('reviewId', reviewId);
	$('#deleteConfirmModal').modal('show');
}

// Delete review
function deleteReview(reviewId) {
	// Here you would typically call your backend API
	const index = reviews.findIndex(review => review.id === reviewId);
	if (index !== -1) {
		reviews.splice(index, 1);

		// Recalculate total pages
		const totalPages = Math.ceil(reviews.length / itemsPerPage);

		// If we're on the last page and it's now empty, go to previous page
		if (currentPage > totalPages) {
			currentPage = totalPages || 1;
		}

		// Re-render the current page
		renderPage(currentPage);
	}

	// Remove from UI with animation
	$(`.review - card[data - id="${reviewId}"]`).fadeOut(300, function() {
		$(this).remove();
	});
}



