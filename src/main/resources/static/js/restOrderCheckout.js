console.log('aaamanage');
// let order = window.orderMaster;
console.log(orderMaster);
let memberPhoneVerify = {};
let submitUsePoints = 0;


async function getOrderMaster(){
    let url = "/orders/findOneByOrderId";
    // let order = {
    //     orderId: orderMaster.orderId,
    // }

    fetch('/orders/findOneByOrderId?orderId=' + orderMaster.orderId).then((res) => {
        if (res.ok){
            return res.json();
        }
    }).then((data) => {
        console.log(data);
    });
}

function verifyPhoneNumber(){
    const verifyPhoneNumberBtn = $("#verifyPhoneNumberBtn");
    verifyPhoneNumberBtn.on("click", async function(e){
        e.preventDefault();
        memberPhoneVerify = await verifyPhoneFetch();
        try{
            let memberPoints = await getMemberDiscPts(memberPhoneVerify.data.memberId, restId);
        } catch (error){
            console.log(error);
        }



    });
    verifyPhoneNumberBtn.disabled = true;
    // verifyPhoneNumberBtn.disabled = false;
}


async function verifyPhoneFetch(){
    // let url = "http://localhost:8080/member/findMemberByPhone";

    // e.preventDefault();
    console.log("click verify");
    // let memberPhoneForm = $("#memberPhoneForm");
    let memberNumFormData = new FormData(document.getElementById("memberPhoneForm"));

    try{
        const url = "/rest/member/findMemberByPhone";
        const res = await fetch(url, {
            method: "POST",
            body: memberNumFormData,
        })
        if (!res.ok){
            // errorData = await res.json();
            let errorData = await res.json();
            // return {
            //     success: false,
            //     status: res.status,
            //     message: errorData.message || "error occured",
            //     errorData: errorData
            // }
            throw new Error(errorData.message || "error occured");
        }
        memberPhoneVerify = await res.json();
        console.log(memberPhoneVerify);

        $("#memberStatus").removeClass("error");
        $("#memberStatus").addClass("success");
        $("#memberStatus").text(`驗證成功！會員姓名：${memberPhoneVerify.data.memberName}`);
        

        $("#verifyPhoneNumberBtn").disabled = false;
        return memberPhoneVerify;
    }catch(error){
        
        $("#memberStatus").removeClass("success");
        $("#memberStatus").addClass("error");
        $("#memberStatus").text("查無會員！");
        $("#pointUseForm").removeClass("show");
        $("#showMemberPoints").removeClass("show");
        $("#verifyPhoneNumberBtn").disabled = false;
        console.log(error);
        return [];
        

    }

}

async function getMemberDiscPts(memberId, restId){
    const url = "/rest/points/getPointsByMemberAndRest"
    let data = {
        memberId: memberId,
        restId: restId
    }

    try{
        let res = await fetch(url, {
            method: "POST",
            body: JSON.stringify(data),
            headers:{
                "Content-Type": "application/json; charset=UTF-8"
            }
        });
        if (!res.ok){
            let errorData = await res.json();
            throw new Error(errorData.message || "error occured");
            // return {
            //     success: false,
            //     status: res.status,
            //     message: errorData.message || "error occured",
            //     errorData: errorData
            // }
        }

        let memberDiscPts = await res.json();
        console.log(memberDiscPts);
        $("#showMemberPoints").addClass("show");
        $("#showMemberPoints").text(`可用會員點數：${memberDiscPts.data.availablePoints}`);
        // showPointUseForm();
        $("#pointUseForm").addClass("show");
        $("input[name='memberId']").val(memberDiscPts.data.memberId);

        
        // console.log(memberPoints);

        return memberDiscPts;

    } catch (e){
        console.log(e);
        // $("#showMemberPoints").removeClass("success");

        // if (e == 'Failed to get member points'){
            $("#showMemberPoints").addClass("show");
            $("#showMemberPoints").text("查無會員點數！");
            $("#pointUseForm").removeClass("show");
        // }
        // return [];
    }
}

$('#verifyDiscPtsUse').on('click', async function(e){
    e.preventDefault();
    let memberPointStatusEle = $(".member-discpts-status");
    let checkOutBtnEle = $('.checkout-btn');

    let verifyPointUse = await verifyPointUseFetch();
    console.log(verifyPointUse);
    if (verifyPointUse.code == 200){
        memberPointStatusEle.addClass("success");
        memberPointStatusEle.removeClass("error");
        memberPointStatusEle.text("確認使用點數：" + verifyPointUse.data.usePoints + "點");
        checkOutBtnEle.removeClass("disabled");
        console.log("success");

        $(".subtotal").text("$ " + verifyPointUse.data.subTotalPrice);
        $("#discountAmount").text("$ " + verifyPointUse.data.discounts);
        $("#totalPrice").text("$ " + verifyPointUse.data.totalPrice);

    } else if (verifyPointUse.code >= 400){
        memberPointStatusEle.addClass("error");
        memberPointStatusEle.removeClass("success");
        checkOutBtnEle.addClass("disabled");

        // console.log(orderMaster);

        $(".subtotal").text("$ " + orderMaster.subtotalPrice);
        $("#discountAmount").text("$ " + 0);
        $("#totalPrice").text("$ " + orderMaster.subtotalPrice);

        if (verifyPointUse.code == 403){
            // 會員點數不足
            memberPointStatusEle.text("點數不足！");
        } else if (verifyPointUse.code == 422){
            // 使用過量點數使結帳金額 < 0
            memberPointStatusEle.text("使用點數超過上限！");
            
        } else if(verifyPointUse.code == 400){
            // 使用點數填入負數
            memberPointStatusEle.text("使用點數不可為負！");
        } else {
            memberPointStatusEle.text("使用點數錯誤！請重新輸入。");
        }
    } 
    

})


async function verifyPointUseFetch(){
    let url = "/rest/points/verifyUsage"
    let data = {
        usePoints: $("input[name='usePoints']").val(),
        memberId: $("input[name='memberId']").val(),
        orderId: $("input[name='orderId']").val()
    }
    try{
        let res = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(data),
        })
        if (!res.ok){
            let errorData = await res.json();
            return {
                success: false,
                code: res.status,
                message: errorData.message || "error occured",
                errorData: errorData
            }
            // throw new Error(JSON.stringify(errorData));
        }
        let verifyPoints = await res.json();
        console.log(verifyPoints);
        submitUsePoints = verifyPoints.data.usePoints;
        return verifyPoints;
    }catch(error){
        try{
            const erroeObj = JSON.parse(error.message);
            console.log(errorObj)
        } catch(e){
            console.log("convert failed, " + error.message);
        }
    }
}


document.getElementById("memberPhoneForm").addEventListener("submit", function (e) {
    e.preventDefault(); 
});


async function main(){
    await getOrderMaster();
}

$(".checkout-btn").on("click", function(e){
    e.preventDefault();

    const checkoutForm = $("#checkoutSubmitForm");
    checkoutForm.append(`<input name="orderId" value="${orderMaster.orderId}" type="hidden"></input>`)
    checkoutForm.append(`<input name="memberId" value="${memberPhoneVerify.data.memberId}" type="hidden"></input>`)
    checkoutForm.append(`<input name="usePoints" value="${submitUsePoints}" type="hidden" />`);

    checkoutForm.submit();

})

verifyPhoneNumber();

main();