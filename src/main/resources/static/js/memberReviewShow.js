const images = {
    environment: [],
    review: [],
};

async function fetchEnvImgs(restId){
    let imgUrlList = [];
    try {
        let res = await fetch(`/envImg/images/${restId}`);
        if (!res.ok){
            throw new Error(`Failed to fetch image IDs: ${res.status}`);
        }
        let imagesIds = await res.json();

        for (const id of imagesIds){
            imgUrlList.push(`/envImg/image/${id}`);
        }

        // console.log(imgUrlList);
        return imgUrlList;
    } catch (error){
        console.error(error);
        return [];
    }
}

async function loadEnvImgsUrl(restId){
    images.environment = await fetchEnvImgs(restId);
}

async function populateImageGrid(restId){
    await loadEnvImgsUrl(restId);

    const EnvImgGrid = document.querySelector('.restaurant-image-container');
    EnvImgGrid.innerHTML = "";

    if (images.environment.length === 0){
        console.log('no images');
        EnvImgGrid.innerHTML = `<img src="https://placehold.co/200x150" alt="餐廳照片" class="restaurant-image">`
    } else {
        EnvImgGrid.innerHTML = `<img src=${images.environment[0]} alt="餐廳照片" class="restaurant-image">`
    }
}

// populateImageGrid(2001);

async function fetchRevImgs(reviewId){
    let imgUrlList = [];
    try{
        let res = await fetch(`/member/reviews/images/${reviewId}`)
        if (!res.ok){
            throw new Error(`Failed to fetch review-img Ids: ${res.status}`);
        }
        let imageIds = await res.json();
        for (const id of imageIds){
            imgUrlList.push(`/member/reviews/image/${id}`);
            console.log(imgUrlList);
        }
        return imgUrlList;
    }catch (error){
        console.log(error);
        return [];
    }
}

async function loadRevImgsUri(reviewId){
    images.review = await fetchRevImgs(reviewId);
}

async function populateRevImageGrid(reviewId){
    await loadRevImgsUri(reviewId);

    const ReviewImgGrid = document.querySelector('.review-img-grid');
    ReviewImgGrid.innerHTML = '';
    if (images.review.length != 0){
        for (const img of images.review){
            ReviewImgGrid.innerHTML += `<div class="photoPreviewItem photo-preview-item">
                                            <img src="${img}" alt="預覽圖" class="review-image" style="cursor: pointer;">
                                        </div>`
        }
    }


    const reviewImages = document.querySelectorAll('.review-image');
    reviewImages.forEach(img => {
        img.addEventListener('click', function() {
            const modalImage = document.getElementById('modalImage');
            modalImage.src = this.src;
            const imageModal = new bootstrap.Modal(document.getElementById('imageModal'));
            imageModal.show();
        });
    });
}