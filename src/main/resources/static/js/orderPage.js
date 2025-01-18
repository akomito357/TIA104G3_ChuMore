
async function getOrderMaster(){
    return new Promise(function(resolve, reject){
        $.ajax({
            url: "/orders/findOneFromSession",
            method: "POST",
            dataType: "json",
            async: true,
            success: function (data) {
                console.log(data);
                resolve(data);
            },
            error: function (error) {
                reject(error);
            }
        });
    })
    

}

// getOrderMaster();
let orderMaster = {};

function getProductCategoriesAndProducts(){
    let apiUrl= "/rest/productcategory/getListByRestId"
    // let restId = orderMaster.rest;
    let restData = {
        restId: restId,
    };

    console.log(restData.restId);

    return new Promise((resolve, reject) =>{
        $.ajax({
            url: apiUrl,
            method: 'GET',
            dataType: 'json',
            data: restData,
            async: true,

            success: res =>{
                // console.log(res);
                console.log('getProductCategoriesAndProducts success')
                let productCategoryJson = "";
                /** 將餐點分類加入上方導覽列 **/
                let productCategories = "";
                // console.log(res);
                $.each(res.data, function(i, productCategory){
                    // console.log(i);
                    // console.log(item);
                    productCategoryJson = productCategory;
                    if (productCategory.productList.length != 0){
                        productCategories += `<a class="nav-link productCategory" href="#procat-${productCategory.productCategoryId}">${productCategory.categoryName}</a>`;
                    }
                });
                // console.log(productCategories);
                $(".productCategoryNav").html(productCategories);
                
                /**  將餐點分類代入下方菜單h3標題與餐點代入菜單 **/
                let productCategorySections = "";
                $.each(res.data, function(i, productCategory){
                    if (productCategory.productList.length != 0){ // 該類別有餐點才顯示
                        productCategorySections += 
                        `<section id="procat-${productCategory.productCategoryId}" class="menu-section productCategorySection">
                        <h3 class="menu-section-title productCategoryTitel">${productCategory.categoryName}</h3>
                        `;
                        console.log(productCategory.productList);
                        
                        $.each(productCategory.productList, function(index, product){
                            // console.log(product);
                            productJSON = product;
                            if (product.supplyStatus == 0){ // 有供應才顯示在菜單上
                                // let productDiv = "";
                                productCategorySections += `
                                <div class="menu-item product" id="product-${product.productId}">
                                    <div class="menu-item-img-container">`;
                                if (product.productImage){ // 有圖片才顯示，否則顯示預設
                                    // 使用原生JS才能正確判斷null，jQuery會回傳jQuery物件
                                    // console.log('not null');
                                    // console.log(product.productImage);
                                    productCategorySections += `<img src="data:image/jpeg; base64, ${product.productImage}" alt="${product.productName}" class="menu-item-img productImage">`
                                }else{
                                    // console.log('null');
                                    productCategorySections += `<img src="https://placehold.co/160x120" alt="${product.productName}" class="menu-item-img productImage">`
                                }
                                        // <img src="${product.productImage}" alt="${product.productName}" class="menu-item-img productImage">
                                productCategorySections +=  `</div>
                                    <div class="menu-item-content">
                                        <h5 class="productName">${product.productName}</h5>
                                        <div class="menu-item-description">
                                            <p class="text-muted small mb-0 productDescription">${product.productDescription}</p>
                                        </div>
                                        <div class="menu-item-footer">
                                            <div class="d-flex justify-content-between align-items-center priceAndCount">
                                                <div class="h5 mb-0">$<span class="productPrice">${product.productPrice}</span></div>
                                                <div class="btn-group">
                                                    <button class="btn btn-outline-primary btn-sm btnMinus">-</button>
                                                    <span class="btn btn-outline-primary btn-sm disabled orderCount">0</span>
                                                    <button class="btn btn-outline-primary btn-sm btnPlus">+</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>`;
                            }
                            
                        })
                        productCategorySections += `</section>`
                        // $(".productCategorySection").html(productDiv);
                        // console.log("================")S
                    } else {
                        // productCategorySections += `</section>`
                    }
                    $(".menuContainer").html(productCategorySections);
                })
                // console.log(productCategorySections);
                resolve(res);
            },
            error: err =>{
                // console.log(err);
                console.log('getProductCategoriesAndProducts error');
                reject(err);
            },
        
        });

    });
        
}

/**get orderMaster */
function getOrderMaster(){
    // let orderId = [[${orderId}]];

    let url = "/orders/findOneByOrderId";
    // let orderId = orderMaster.orderId;
    let orderData = {
        orderId: orderId,
    };

    console.log(orderData.orderId);

    $.ajax({
        url: url,
        method: 'GET',
        dataType: 'json',
        data: orderData,
        async: true,

        success: res => {
            console.log('getOrderMaster success');
            orderMasterJson = res.data;
            // console.log(orderMasterJson);

            // 將orderItem資料代入點餐紀錄
            let orderItemHistory = "";
            $.each(orderMasterJson.orderItems, function(index, orderItem){
                // console.log(orderItem);
                // console.log(index + 1);
                orderItemHistory += `<div class="card mb-3 orderItem">
                <div class="card-header d-flex justify-content-between">
                    <div><span>第</span><span class="orderItemCount">${index + 1}</span><span>次點餐</span></div>
                    <div><span class="text-muted orderItemCreateDatetime">${orderItem.formatUpdatedDatetime}</span></div>
                </div>`;

                // 將orderLineItem資料代入每次點餐
                orderItemHistory += `<div class="card-body">`;
                let orderLineItemHistory = "";
                let orderItemPrice = 0;
                $.each(orderItem.orderLineItem, function(index, orderLineItem){
                    // console.log(orderLineItem);
                    orderLineItemHistory += `<div class="d-flex justify-content-between mb-2">
                                <div class="orderLineItem">${orderLineItem.product.productName}</div>
                                <div>$<span class="orderLineItemPrice">${orderLineItem.product.productPrice}</span> x <span class="orderLineItemQuantity">${orderLineItem.quantity}</span></div>
                            </div>`;
                    orderItemPrice += parseInt(orderLineItem.product.productPrice) * parseInt(orderLineItem.quantity);
                })
                orderItemHistory += orderLineItemHistory;

                if (orderItem.memo){
                    orderItemHistory += `<div class="border-top mt-2 pt-2 small text-secondary memoContainer">備註：<span class="memo">${orderItem.memo}</span></div>`;
                } else{
                    orderItemHistory += `<div class="border-top mt-2 pt-2 small text-secondary memoContainer">備註：<span class="memo">無</span></div>`;
                }
                
                orderItemHistory += `<div class="border-top pt-2 mt-2 text-end">
                                <strong>小計：$<span class="orderLinePrice">${orderItemPrice}</span></strong>
                            </div>`
                orderItemHistory += `</div>`;
                orderItemHistory += `</div>`;
            })
            // orderItemHistory += `</div>`;
            $(".orderHistoryModalBody").html(orderItemHistory);

            // 將小計代入history總計消費
            // let orderMaterSubtotalPrice = "";
            $(".subtotalPrice").text(orderMasterJson.subtotalPrice);

        },
        error: err =>{
            console.log('getOrderMaster error');
            console.log(err);
        }
    })


}

/**按按鈕的時候數量變更 */
function productCountChange(){
    let cartData = {};
    $(document).on("click", "button.btnPlus", function(e){
        // console.log("+++");
        // console.log($(this).closest(".btn-group").children(".orderCount"));
        e.preventDefault();
        let thisOrderCountEle = $(this).closest(".btn-group").children(".orderCount")
        let thisItemCount = parseInt(thisOrderCountEle.text());
        thisItemCount ++;
        // console.log(thisItemCount);
        thisOrderCountEle.text(thisItemCount);
        thisOrderCountEle.addClass("hasCount"); // for cart update

        // 新點餐
        let productIdText = $(this).closest('.product').attr('id').replace('product-', '')
        let productNameText = $(this).closest('.menu-item-content').children('h5.productName').text()
        let productPriceInt = parseInt($(this).closest(".priceAndCount").children("div.h5").children(".productPrice").text());

        orderInStorage(productIdText, productNameText, productPriceInt, thisItemCount);

    })

    $(document).on("click", "button.btnMinus", function(e){
        // console.log("---");
        // console.log($(this).closest(".btn-group").children(".orderCount"));
        e.preventDefault();
        let thisOrderCountEle = $(this).closest(".btn-group").children(".orderCount")
        let thisItemCount = parseInt(thisOrderCountEle.text());
        if (thisItemCount > 0){
            thisItemCount --;
            thisOrderCountEle.text(thisItemCount);

            if (thisItemCount == 0){
                thisOrderCountEle.removeClass('hasCount');
            }
            
            let productIdText = $(this).closest('.product').attr('id').replace('product-', '');
            let productNameText = $(this).closest('.menu-item-content').children('h5.productName').text();
            let productPriceInt = parseInt($(this).closest(".priceAndCount").children("div.h5").children(".productPrice").text());

            orderInStorage(productIdText, productNameText, productPriceInt, thisItemCount);
        }
        // console.log(thisItemCount);
    })
}

function orderInStorage(productIdText, productNameText, productPriceInt, thisItemCount){
    let newData = {
        // index: localStorage['cartIndex'],
        productId: productIdText,
        productName: productNameText,
        count: thisItemCount,
        origPriceForOne: productPriceInt,
    }

    if (localStorage['cartData']){
        let newCartData = [];
        let cartData = JSON.parse(localStorage.cartData);
        // console.log(cartData);
        let isSame = false;

        for (let i = 0; i < cartData.order.length; i++){
            // console.log(cartData.order[i].productId);
            let thisLineItem = cartData.order[i];
            if (thisLineItem.productId == newData.productId){
                // 如果購物車已有此商品
                // console.log('had!');
                let newItemData = {...thisLineItem, count: thisItemCount};
                // console.log(newItemData);
                cartData.order[i] = newItemData;

                // 如果數量是0就移除
                if (cartData.order[i].count == 0){
                    cartData.order.splice(i, 1);
                }
                
                isSame = true;

                break;
            };
        }
        if (!isSame){
            cartData.order.push(newData);
        }

        // 如果購物車空，移除localstorage
        if (cartData.order.length == 0){
            localStorage.removeItem("cartData");
        } else{
            localStorage.cartData = JSON.stringify({order: cartData.order, memo: cartData.memo});
        }

    } else {
        localStorage.cartData = JSON.stringify({order: [newData], memo: ''});
    }
    updateCartBtn();
}


/** 更新購物車總額和數量*/ 
// 更新購物車總額和數量
function updateCartBtn(){
    let totalItemCount = 0;
    let totalPriceForBtnView = 0;

    $(".orderCount").each(function(){
        let thisCount = parseInt($(this).text());
        // console.log($(this).closest('.btn-group').closest('.priceAndCount'));
        let thisPrice = parseInt($(this).closest('.menu-item-footer').find('.productPrice').text());
        totalItemCount += thisCount;
        totalPriceForBtnView += thisPrice * thisCount;
        // console.log(thisPrice);
    })

    $("span.currentPrice").text(totalPriceForBtnView);
    $("span.cart-badge").text(totalItemCount);

    // 更新按鈕狀態 - 當購物車內的物品數量>0，按鈕樣式變更
    if (totalItemCount > 0){
        $("span.cart-badge").removeClass("d-none");
        $("button.cart").addClass("btn-primary");
        $("button.cart").removeClass("btn-outline-primary")
    } else {
        $("span.cart-badge").addClass("d-none");
        $("button.cart").removeClass("btn-primary");
        $("button.cart").addClass("btn-outline-primary")
    }

}

// 載入頁面時讀取local storage的資料
function init(){
    if (localStorage.cartData){
        // console.log(localStorage.cartData);
        let cartData = JSON.parse(localStorage.cartData).order;
        // console.log(cartData);
        cartData.forEach(orderLine => {
            // console.log(orderLine.productId);
            let productId = "product-" + orderLine.productId;
            // console.log('id= '+ productId);
            let productEle = document.getElementById(productId);
            // console.log(productEle.querySelector(".orderCount"));
            productEle.querySelector(".orderCount").innerText = orderLine.count;
        });
    }

}

// 服務鈴/結束用餐按鈕召喚modal
function showServiceBellModal(){
    let modal = new bootstrap.Modal(document.getElementById('serviceBellModal'));
    modal.show();
}

$("#service_bell_btn").click(function(e){
    e.preventDefault();
    // const restId = restId;
    // const tableName = tableName;
    console.log(restId, caller);

    $.ajax({
        url: "/notification/serviceBell",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            restId: restId,
            caller: caller
        }),
        success: function(res){
            console.log(res);
            let modal = new bootstrap.Modal(document.getElementById('serviceBellPressedModal'));
            modal.show();
        },
        error: function(err){
            console.log(err);
            let modal = new bootstrap.Modal(document.getElementById('serviceBellFailedModal'));
            modal.show();
        }
    })
})

function showFinishDiningModal(){
    let modal = new bootstrap.Modal(document.getElementById('diningFinishConfirmModal'));
    modal.show();
}

console.log(window.orderId);
console.log(orderId);

$("#diningFinishConfirm").click(async function(e){
    e.preventDefault();
    $.ajax({
        url: "/notification/requestCheckout",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify({
            restId: restId,
            caller: caller
        }),
        success: function(res){
            console.log(res);
            
        },
        error: function(err){
            console.log(err);
            
        }
    })

    if (localStorage.getItem("cartData") != null){
        localStorage.removeItem("cartData");
    }

    const orderData = {
        orderId: orderId
    }
    console.log(JSON.stringify(orderData));

    try{
        const url = "/orders/finishOrder";
        const res = await fetch(url, {
            method: "POST",
            body: JSON.stringify(orderData),
            headers:{
                "Content-Type": "application/json; charset=UTF-8"
            },
        })
        if (!res.ok){
            // errorData = await res.json();
            let errorData = await res.json();
            return {
                success: false,
                status: res.status,
                message: errorData.message || "error occured",
                errorData: errorData
            }
            // throw new Error(errorData.message || "error occured");
        }
        console.log(res);

        const html = await res.text();
        document.open();
        document.write(html);
        document.close();

    }catch(error){
        
        console.log(error);
        return [];
    }
})

const images = {
    environment: [],
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

    const EnvImgGrid = document.querySelector('.restBanner');
    EnvImgGrid.innerHTML = "";

    if (images.environment.length === 0){
        console.log('no images');
        EnvImgGrid.innerHTML = `<img src="https://placehold.co/400x200" alt="餐廳圖片" class="w-100 h-100 object-fit-cover restEnvImg">`
    } else {
        EnvImgGrid.innerHTML = `<img src=${images.environment[0]} alt="餐廳圖片" class="w-100 h-100 object-fit-cover restEnvImg">`
    }
}


// 初始化
async function main(){
    orderMaster = await getOrderMaster()
    productCountChange()
    await getProductCategoriesAndProducts();
    await init()
    await updateCartBtn()
    await populateImageGrid(restId)
    getOrderMaster();
    // showServiceBellModal();
}


main();

