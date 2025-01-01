// let restId = 2001; // for testing
let orderItem = null;

// 定義元素
// let productCateNavEle = $(".productCategoryNav");

// ajax
// function for ProductCategories and Products
function getProductCategoriesAndProducts(){
    let apiUrl= "http://localhost:8080/rest/productcategory/getListByRestId"
    let restData = {
        restId: 2001
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

            /** 將餐點分類加入上方導覽列 **/
            let productCategories = "";
            $.each(res.data, function(i, productCategory){
                // console.log(i);
                // console.log(item);
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
                    `<section id="procat${productCategory.productCategoryId}" class="menu-section productCategorySection">
                    <h3 class="menu-section-title productCategoryTitel">${productCategory.categoryName}</h3>
                    `;
                    // console.log(productCategory.productList);
                    
                    $.each(productCategory.productList, function(index, product){
                        // console.log(product);
                        if (product.supplyStatus == 0){ // 有供應才顯示在菜單上
                            // let productDiv = "";
                            productCategorySections += `
                            <div class="menu-item product">
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
                                        <div class="d-flex justify-content-between align-items-center">
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

    // fetch("http://localhost:8080/rest/productcategory/getListByRestId?restId=" + restData.restId) // restId is just for testing
    // .then((res) => {
    //     if(res.ok){
    //         return res.json();
    //     }
    // }).then((data) => {
    //     console.log(data);
    //     console.log("sss");
    // });
}

/**get orderMaster */




/** for 點餐紀錄 */
function getOrderItem(){
    let url = "http://localhost:8080/rest/orderitem/findByOrderId"
    let orderData = {
        orderId: 1
    };
    
    $.ajax({
        url: url,
        method: 'GET',
        dataType: 'json',
        data: orderData,
        async: true,

        success: res =>{
            console.log("getOrderItem success");
            console.log(res);
            orderItem = res;
            console.log(orderItem);
        },

        error: err =>{
            console.log("getOrderItem error");
            console.log(err);
        },
    })
}






getProductCategoriesAndProducts();
getOrderItem();



/** 更新購物車總額和數量*/ 
// 更新購物車總額和數量
// 更新按鈕狀態 - 當購物車內的物品數量>0，按鈕樣式變更
// 商品的數量加減按鈕功能




// 初始化
function init(){
    
}