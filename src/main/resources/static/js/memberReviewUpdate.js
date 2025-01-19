// console.log('add');

const images = {
    environment: [],
    // review: [],
};

let rating = 0.0;

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

    if (images.environment.length === 0 ){
        console.log('no images');
        EnvImgGrid.innerHTML = `<img src="https://placehold.co/200x150" alt="餐廳照片" class="restaurant-image">`
    } else {
        console.log(images.environment.length);
        console.log(images.environment);
        EnvImgGrid.innerHTML = `<img src=${images.environment[0]} alt="已設置餐廳照片" class="restaurant-image">`
    }
}

// 星星評分_
function starRating(){
    // let selectedRating = 0;
    // $(".star-rating").hover(function(){
    //     const rating = $(this).data("rating");
    //     $(".star-rating").find("i").removeClass('fas').addClass('far');
        
    // })

    $(".star-rating").click(function(){
        // selectedRating = $(this).data("rating");
        let currentStarRate = parseInt($(this).attr("data-rating"));
        rating = currentStarRate;
        $("#ratingError").hide();
        $("input.rating").val(rating);
        // console.log('sss');
        $(this).closest(".star-rating-container").find(".star-rating").each(function(index, element){
            if (parseInt($(this).attr("data-rating")) <= currentStarRate){
                $(this).addClass("selected");
                $(this).find("i").removeClass('far').addClass('fas');
            } else {
                $(this).removeClass("selected");
                $(this).find("i").removeClass('fas').addClass('far');
            }
        })

    })

    $(".star-rating").hover(function(){
        let currentStarRate = parseInt($(this).attr("data-rating"));
        $(this).closest(".star-rating-container").find(".star-rating").each(function(index, element){
            if (parseInt($(this).attr("data-rating")) <= currentStarRate){
                $(this).addClass("active");
                $(this).find("i").removeClass('far').addClass('fas');
            }else{
                $(this).removeClass("active");
                $(this).find("i").removeClass('fas').addClass('far');
            }
        })
    })

    $(".star-rating-container").mouseleave(function(){
        let currentStarRate = $("#reviewRating").val();
        // console.log($("#reviewRating").val());
        $(".star-rating-container").find(".star-rating").each(function(index, element){
            if (parseInt($(this).attr("data-rating")) <= currentStarRate){
                $(this).addClass("selected").removeClass("active");
                $(this).find("i").removeClass('far').addClass('fas');
            } else {
                $(this).removeClass("selected").removeClass("active");
                $(this).find("i").removeClass('fas').addClass('far');
            }
        });
    })

    // console.log(rating);
}

// 照片上傳功能
const photoUploadContainer = $('#photoUploadContainer');
const photoUploadInput = $('#upfiles');
const photoPreviewGrid = $('#photoViewGrid');
const updateReviewForm = $('#update_review_form');
let uploadedFiles = [];
let dataTransfer = new DataTransfer();
let imgIndex = 0;

// drag and drop
// photoUploadContainer[0].addEventListener('dragover', (e) =>{
//     e.preventDefault();
//     photoUploadContainer.addClass('dragover');
// });

// photoUploadContainer[0].addEventListener('dragleave', () => {
//     photoUploadContainer.removeClass('dragover');
// });

// photoUploadContainer[0].addEventListener('drop', (e) => {
//     e.preventDefault();
//     photoUploadContainer.removeClass('dragover');
//     handleFileUpload(e.dataTransfer.files);
    // let file_list = e.dataTransfer.files;
    
// });

function showFileUpload() {
    const files = dataTransfer.files;
    // document.querySelectorAll(".newPreview").remove();

    for (let file of dataTransfer.files) {
        if (file.type.match('image.*')){
            const reader = new FileReader();
            // reader.readAsDataURL(file);
            reader.onload = function(e){
                // console.log(file);
                // console.log(photoPreviewGrid);
                photoPreviewGrid.innerHTML = "";
                const previewImg = `<div class="photoPreviewItem photo-preview-item newPreview">
                                        <img src="${e.target.result}" alt="preview" index="${imgIndex++}" class="newImg">
                                        <button class="photo-delete-btn">
                                            <i class="fas fa-times"></i>
                                        </button>
                                    </div>`;
                photoPreviewGrid.append(previewImg);
                // uploadedFiles.push(file);
            }
            reader.readAsDataURL(file);
        }
    }
    imgIndex = 0;
}

function addShowFileUpload(file){
    if (file.type.match('image.*')){
        const reader = new FileReader();
        // reader.readAsDataURL(file);
        reader.onload = function(e){
            // console.log(file);
            // console.log(photoPreviewGrid);
            // photoPreviewGrid.innerHTML = "";
            let index = 0;
            Array.from(dataTransfer.files).forEach((dataTransferfile, dataTransferindex) => {
                // console.log(`Index: ${index}, File: ${dataTransferfile.name}`);
                if (file === dataTransferfile){
                    index = dataTransferindex
                }

            });

            const previewImg = `<div class="photoPreviewItem photo-preview-item newPreview">
                                    <img src="${e.target.result}" alt="preview" index="${index}" class="newImg">
                                    <button class="photo-delete-btn">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>`;
            photoPreviewGrid.append(previewImg);
            // uploadedFiles.push(file);
        }
        reader.readAsDataURL(file);
    }
}


photoUploadInput.on('change', function(e) {
    for (let file of e.target.files){
        uploadedFiles.push(file);
        dataTransfer.items.add(file);
        addShowFileUpload(file);
    }

    // photoUploadInput.value = "";
    // showFileUpload();
    // uploadedFiles.push(file);
});

$(document).on('click', '.photo-delete-btn', function(e){
    e.preventDefault();
    const imgEle = $(this).parent('.photoPreviewItem').find('img');

    if(imgEle.attr('index') === undefined){
        // console.log('oldUpdated');
        // console.log(imgEle.attr('src'));
        let reviewImgId = imgEle.attr('src').split('=').pop();
        // console.log(reviewImgId);
        deleteOldImgFetch(reviewImgId);
        imgEle.parent('.photoPreviewItem').remove();
    } else {
        let newDataTransfer = new DataTransfer();
        let currentImgIndex = imgEle.attr('index');
        // console.log(document.querySelector("#upfiles").value);
        // console.log('currentImgIndex = ' + currentImgIndex);
        uploadedFiles.splice(currentImgIndex, 1);
        $(this).parent('.photoPreviewItem').remove();
        // console.log('uploadedFiles =' + uploadedFiles);

        // update indexs
        document.querySelectorAll('img.newImg').forEach((item, index) => {
            if (item.getAttribute('index') > currentImgIndex){
                currentIndex = item.getAttribute('index');
                item.setAttribute('index', currentIndex - 1);
                // console.log("change index = ", index);
            }
        })
        // imgIndex --;
        
        for (let i = 0; i < uploadedFiles.length; i++){
            newDataTransfer.items.add(uploadedFiles[i]);
        }
    
        // console.log('uploadedFiles =' + uploadedFiles);
        dataTransfer = newDataTransfer;
        Array.from(dataTransfer.files).forEach((file, index) => {
            console.log(`Index: ${index}, File: ${file.name}`);
        });
        // showFileUpload();
    }

})


document.querySelector("#avgCost").addEventListener("keydown", function(e) {
    if (e.keyCode == 13) {
        e.preventDefault();
        // document.querySelector("#avgCost").blur();
    }
})

updateReviewForm.submit(function(e){
    // console.log('addReviewForm submit');
    photoUploadInput[0].files = dataTransfer.files;
});


async function deleteOldImgFetch(reviewImgId){
    const url = "/member/reviews/images/delete/" + reviewImgId;
    try {
        console.log(url);
        let res = await fetch(url, {
            method: 'DELETE',
            
        })
        if (!res.ok){
            throw new Error('deleteOldImgFetch error with id = ' + reviewImgId);
        }
        // let resJson = await res.json();
        console.log(res);
        return res;
    } catch(error){
        console.log(error);
        return [];
    }

}



$(document).ready(function(){
    starRating();
})