// let restId = 2001; // for testing

// 定義元素
// let productCateNavEle = $(".productCategoryNav");
// let productCategoryJson = null; // line33
// let productJson = null; // line53
// let orderMasterJson = null;
// let orderItemJson = null; //
// let cartData = {}; // 預計存入local storage 的資料
// localStorage['cartData'] = {};


// ajax
// function for ProductCategories and Products
function getProductCategoriesAndProducts(){
    let apiUrl= "http://localhost:8080/rest/productcategory/getListByRestId"
    let restData = {
        restId: 2003
    };

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
            console.log(res);
            $.each(res.data, function(i, productCategory){
                // console.log(i);
                // console.log(item);
                productCategoryJson = productCategory;
                if (productCategory.productList.length != 0){
                    productCategories += `<a class="nav-link productCategory" href="#procat${productCategory.productCategoryId}">${productCategory.categoryName}</a>`;
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
                    // console.log(productCategory.productList);
                    
                    $.each(productCategory.productList, function(index, product){
                        // console.log(product);
                        productJSON = product;
                        if (product.supplyStatus == 0){ // 有供應才顯示在菜單上
                            // let productDiv = "";
                            productCategorySections += `
                            <div class="menu-item product" id="product-${product.productId}">
                                <div class="menu-item-img-container">`;
                            if (product.productImages){ // 有圖片才顯示，否則顯示預設
                                // 使用原生JS才能正確判斷null，jQuery會回傳jQuery物件
                                // console.log('not null');
                                // console.log(product.productImage);
                                productCategorySections += `<img src="${product.productImage}" alt="${product.productName}" class="menu-item-img productImage">`
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
        },
        error: err =>{
            // console.log(err);
            console.log('getProductCategoriesAndProducts error');
        },
    })

    
}

/**get orderMaster */
function getOrderMaster(){
    let url = "http://localhost:8080/orders/findOneByOrderId";
    let orderData = {
        orderId: 1
    };

    $.ajax({
        url: url,
        method: 'GET',
        dataType: 'json',
        data: orderData,
        async: true,

        success: res => {
            console.log('getOrderMaster success');
            orderMasterJson = res.data;
            console.log(orderMasterJson);

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
                    console.log(orderLineItem);
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
            $(".modal-body").html(orderItemHistory);

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

/** for 點餐紀錄 */
// function getOrderItem(){
//     let url = "http://localhost:8080/rest/orderitem/findByOrderId";
//     let orderData = {
//         orderId: 1
//     };
    
//     $.ajax({
//         url: url,
//         method: 'GET',
//         dataType: 'json',
//         data: orderData,
//         async: true,

//         success: res =>{
//             console.log("getOrderItem success");
//             // console.log(res);
//             orderItemJson = res.data;
//             console.log(orderItemJson);
//         },

//         error: err =>{
//             console.log("getOrderItem error");
//             console.log(err);
//         },
//     })
// }


getProductCategoriesAndProducts();
getOrderMaster();
// getOrderItem();
// setOrderHistory(orderMasterJson);



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

        // 檢查是否有index
        // if (!localStorage['cartIndex']){
        //     localStorage['cartIndex'] = 0;
        // }

       

        // 新點餐
        let productIdText = $(this).closest('.product').attr('id').replace('product-', '')
        let productNameText = $(this).closest('.menu-item-content').children('h5.productName').text()
        let productPriceInt = parseInt($(this).closest(".priceAndCount").children("div.h5").children(".productPrice").text());
        // console.log(productEl.text())

        let newData = {
            // index: localStorage['cartIndex'],
            productId: productIdText,
            productName: productNameText,
            count: thisItemCount,
            origPriceForOne: productPriceInt,
        }

        if (localStorage['cartData']){
            let newCartData = [];
            let originCartData = JSON.parse(localStorage.cartData);
            console.log(originCartData);
            let isSame = false;
            // console.log(JSON.stringify(originCartData));

            // originCartData.forEach(function(i, item){
            //     console.log(i);
            //     console.log(item);
            // });

            for (let i = 0; i < originCartData.order.length; i++){
                console.log(originCartData.order[i].productId);
                // const test = originCartData.order[i];
                // console.log(typeof test); // obj
                let thisLineItem = originCartData.order[i];
                if (thisLineItem.productId == newData.productId){
                    // 如果購物車已有此商品
                    console.log('had!');
                    let newItemData = {...thisLineItem, count: thisItemCount};
                    console.log(newItemData);
                    originCartData.order[i] = newItemData;
                    isSame = true;
                    // thisLineItem.count = parseInt(thisItemCount);
                    // console.log(newCartData);
                    // localStorage.cartData = JSON.stringify({order: [originCartData.order]}); // localStorage只能存字串
                    break;
                } else{
                    console.log('no');
                    
                    // let newCartData = [];
                    // console.log(originCartData);
                    // localStorage.cartData = JSON.stringify(newCartData);
                }
            }
            if (!isSame){
                originCartData.order.push(newData);
            }
            localStorage.cartData = JSON.stringify({order: originCartData.order});
            // let newCartData = {...originCartData, }
        
        } else {
            localStorage.cartData = JSON.stringify({order: [newData]})
            
        }


        // // 檢查購物車資料存不存在(local storage裡面有沒有相應的物件)，
        // if (localStorage['cartData'] != undefined) {
        //     // originCartData = localStorage.cartData

        // } else {

        // }

    })

    $(document).on("click", "button.btnMinus", function(e){
        // console.log("---");
        // console.log($(this).closest(".btn-group").children(".orderCount"));
        e.preventDefault();
        let thisOrderCountEle = $(this).closest(".btn-group").children(".orderCount")
        let thisItemCount = parseInt(thisOrderCountEle.text());
        if (thisItemCount > 0){
            thisItemCount --;
        }
        // console.log(thisItemCount);
        thisOrderCountEle.text(thisItemCount);
    })
}



/** 更新購物車總額和數量*/ 
// 更新購物車總額和數量


// 更新按鈕狀態 - 當購物車內的物品數量>0，按鈕樣式變更
// 商品的數量加減按鈕功能




// 初始化
function init(){
    
}
productCountChange()