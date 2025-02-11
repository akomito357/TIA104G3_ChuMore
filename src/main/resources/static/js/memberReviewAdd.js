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

    if (images.environment.length === 0){
        console.log('no images');
        EnvImgGrid.innerHTML = `<img src="https://placehold.co/200x150" alt="餐廳照片" class="restaurant-image">`
    } else {
        EnvImgGrid.innerHTML = `<img src=${images.environment[0]} alt="餐廳照片" class="restaurant-image">`
    }
}

// 星星評分_
function starRating(){
    let currentStarRate = 0;
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
        if ($("#reviewRating").val() == ""){
            currentStarRate = rating;
        } else{
            currentStarRate = $("#reviewRating").val();
        }
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
const addReviewForm = $('#reviewAddForm');
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

    for (let file of dataTransfer.files) {
        if (file.type.match('image.*')){
            const reader = new FileReader();
            // reader.readAsDataURL(file);
            reader.onload = function(e){
                // console.log(file);
                // console.log(photoPreviewGrid);
                photoPreviewGrid.innerHTML = "";
                const previewImg = `<div class="photoPreviewItem photo-preview-item">
                                        <img src="${e.target.result}" alt="preview" index="${imgIndex++}">
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

            const previewImg = `<div class="photoPreviewItem photo-preview-item">
                                    <img src="${e.target.result}" alt="preview" index="${index}">
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
    let newDataTransfer = new DataTransfer();

    let currentImgIndex = $(this).parent('.photoPreviewItem').find('img').attr('index');
    // console.log(document.querySelector("#upfiles").value);
    console.log('currentImgIndex = ' + currentImgIndex);
    uploadedFiles.splice(currentImgIndex, 1);
    $(this).parent('.photoPreviewItem').remove();
    console.log('uploadedFiles =' + uploadedFiles);

    // update indexs
    document.querySelectorAll('.photoPreviewItem img').forEach((item, index) => {
        if (item.getAttribute('index') > currentImgIndex){
            item.setAttribute('index', index);
        }
    })
    imgIndex --;
    
    for (let i = 0; i < uploadedFiles.length; i++){
        newDataTransfer.items.add(uploadedFiles[i]);
    }

    // console.log('uploadedFiles =' + uploadedFiles);
    dataTransfer = newDataTransfer;
    // showFileUpload();
})


document.querySelector("#avgCost").addEventListener("keydown", function(e) {
    if (e.keyCode == 13) {
        e.preventDefault();
        // document.querySelector("#avgCost").blur();
    }
})

addReviewForm.submit(function(e){
    // console.log('addReviewForm submit');
    photoUploadInput[0].files = dataTransfer.files;
});

$(document).ready(function(){
    starRating();
    $(".star-rating-container").mouseleave(); // 讓驗證錯誤時星星的顯示也能讀回來
})

