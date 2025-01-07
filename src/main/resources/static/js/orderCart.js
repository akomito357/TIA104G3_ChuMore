let cartData = getCartData();

// 抓回localStorage的資料
function getCartData(){
    if(localStorage.getItem("cartData") != null){
        $('button.submitOrder').removeClass('disabled');
        return JSON.parse(localStorage.getItem("cartData"));
    }
}

// 把資料顯示出來到購物車頁面
function renderCart(){
    let cartHtml = "";
    
    try{
        cartData.order.forEach(orderLine => {
            // console.log(orderLine);
            cartHtml += 
            `<div class="cart-item product" id="product-${orderLine.productId}">
                <div class="row">
                    <div class="col">
                        <div class="item-name productName h5 mb-0">${orderLine.productName}</div>
                        
                        <div class="btn-group mt-3 col-3">
                            <button class="btn btn-outline-primary btn-sm btnMinus">-</button>
                            <span class="btn btn-outline-primary btn-sm disabled orderCount">${orderLine.count}</span>
                            <button class="btn btn-outline-primary btn-sm btnPlus">+</button>
                        </div>
    
                    </div>
                    <div class="col-auto">
                        <div class="right-content">
                            <div class="item-price productPrice">$${orderLine.origPriceForOne * orderLine.count}</div>
                            <button class="delete-btn" onclick="showDeleteConfirm('${orderLine.productId}')">
                                <i class="fas fa-trash"></i>
                                刪除
                            </button>
                        </div>
                    </div>
                </div>
            </div>`;
    
        });
    
        $("div.cart-items").html(cartHtml);

        cartData = getCartData(); 
        $("textarea#memo").text(cartData.memo);
        $("textarea#memo").attr("disabled", false);
    } catch (error){
        cartEmptyNotification();
        $("textarea#memo").attr("disabled", true);
    }

}

// 更新小計
function updateTotal(){
    let totalPrice = 0;
    cartData = getCartData();
    try{
        cartData.order.forEach(orderLine => {
            totalPrice += parseInt(orderLine.origPriceForOne) * parseInt(orderLine.count);
        });
    
        $("span.totalPrice").text(totalPrice);
        // console.log(totalPrice);
    
        if (totalPrice == 0){
            cartEmptyNotification();
        }
    } catch (error){
        cartEmptyNotification();
        $("span.totalPrice").text(0);
    }

}

function cartCheck(){
    if (localStorage.getItem('cartData') == null){
        let modal = new bootstrap.Modal(document.getElementById('cartEmptyModal'));
        modal.show();
    } else {
        showOrderConfirm();
    }
}

function showOrderConfirm(){
    let modal = new bootstrap.Modal(document.getElementById('orderConfirmModal'));
    modal.show();
}

// 刪除確認
function showDeleteConfirm(productId){
    // console.log('sss');
    let modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
    modal.show();
    $('#confirmDelete').off('click').on('click', function(){
        deleteItem(productId);
        // console.log(deleteItem(productId));
        modal.hide();
    })
    modal.show();
}

// 刪除物件並更新購物車資料和介面
function deleteItem(productId){
    console.log(cartData.order);
    cartData.order = cartData.order.filter(function(orderLine){
        return orderLine.productId != productId;
    });
    console.log("new = ", cartData.order);
    if (cartData.order.length == 0){
        localStorage.removeItem('cartData');
        $("textarea#memo").val('');
        $("textarea#memo").attr("disabled", true);
    } else{
        localStorage.setItem('cartData', JSON.stringify(cartData));
    }
    renderCart();
    updateTotal();
}

// 數字按鈕更新
function productCountChange(){
    // let cartData = {};
    $(document).on("click", "button.btnPlus", function(e){
        // console.log("+++");
        // console.log($(this).closest(".btn-group").children(".orderCount"));
        e.preventDefault();
        let thisOrderCountEle = $(this).closest(".btn-group").children(".orderCount")
        let thisItemCount = parseInt(thisOrderCountEle.text());
        thisItemCount ++;
        thisOrderCountEle.text(thisItemCount);
        thisOrderCountEle.addClass("hasCount"); // for cart update

        cartData.order.forEach(function(orderLine){
            if(orderLine.productId == thisOrderCountEle.closest(".product").attr("id").replace("product-", "")){
                orderLine.count = thisItemCount;
                // console.log(orderLine.productId);
            };
        });
        localStorage.setItem('cartData', JSON.stringify(cartData));
        renderCart();
        updateTotal();

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

            cartData.order.forEach(function(orderLine){
            
                if(orderLine.productId == thisOrderCountEle.closest(".product").attr("id").replace("product-", "")){
                    orderLine.count = thisItemCount;
                    // console.log(orderLine.productId);
                    // 如果數量是0就移除
                    if (orderLine.count == 0){
                        cartData.order.splice(cartData.order.indexOf(orderLine), 1);
                    }
                };
            });
            localStorage.setItem('cartData', JSON.stringify(cartData));
            if (cartData.order.length == 0){
                localStorage.removeItem("cartData");
                $("textarea#memo").val('');
                $("textarea#memo").attr("disabled", true);
                $('button.submitOrder').addClass('disabled');
            }
            renderCart();
            updateTotal();
        }

        
    });
}

// 購物車內無商品的訊息顯示
function cartEmptyNotification(){
    if (cartData == undefined || cartData.order.length == 0){
        $("div.cart-items").html(`<span class="d-flex justify-content-center align-items-center notification">購物車內尚無餐點！</span>`);
    }
}

function memoStorage(){
    // sessionStorage.setItem('memo', 'memoo');
    let memo = $("textarea#memo");
    memo.focusout(function(){
        console.log(memo.val());
        console.log('aaa');
        let memoVal = memo.val().trim();
        let cartData = JSON.parse(localStorage.cartData);
        // cartData = JSON.parse(cartData);
        cartData.memo = memoVal;
        localStorage.cartData = JSON.stringify(cartData);
        
        renderCart();
        updateTotal();
    })

}



function submitOrder(){
    console.log('submit');

    let cartDataForOrder = localStorage.getItem('cartData');

    $.ajax({
        url: "/orders/submit",
        method: "POST",
        contentType: "application/json",
        data: cartDataForOrder,

        success: function(res){
            console.log(res);
            console.log("submitOrder success");

            let modal = new bootstrap.Modal(document.getElementById('orderConfirmModal'));
            modal.hide();
            localStorage.removeItem('cartData');
            $("textarea#memo").val('');
            $("textarea#memo").attr("disabled", true);
            renderCart();
            updateTotal();
        },

        error: function(err){
            console.log("submitOrder err");
            console.log(err);

            
        }
    })


}

memoStorage();
renderCart();
updateTotal();
productCountChange();