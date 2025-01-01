let restId = 2001; // for testing

// 定義元素
let productCateNavEle = $(".productCategoryNav");

// ajax

// function for ProductCategories
function getProductCategories(){
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
            console.log(res);
            console.log('success')

            // 將餐點分類加入上方導覽列
            let productCategories = "";
            $.each(res.data, function(i, item){
                // console.log(i);
                // console.log(item);
                productCategories += `<a class="nav-link productCategory" href="#procat${item.productCategoryId}">${item.categoryName}</a>`;
            });
            console.log(productCategories);
            
            $(".productCategoryNav").html(productCategories);
            
            // 將餐點分類代入下方菜單h3標題
            let productCategorySections = "";
            $.each(res.data, function(i, item){
                productCategorySections += 
                `<section id="procat${item.productCategoryId}" class="menu-section productCategorySection">
                <h3 class="menu-section-title productCategoryTitel">${item.categoryName}</h3>
                </section>`;
            })
            console.log(productCategorySections);
            $(".menuContainer").html(productCategorySections);

        },
        error: err =>{
            console.log(err);
            console.log('error');
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

// function for products
function getProduct(){
    let url = "";
    


}





getProductCategories();



/** 更新購物車總額和數量*/ 
// 更新購物車總額和數量
// 更新按鈕狀態 - 當購物車內的物品數量>0，按鈕樣式變更
// 商品的數量加減按鈕功能




// 初始化
function init(){
    
}