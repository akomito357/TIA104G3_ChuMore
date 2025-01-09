console.log('aaamanage');
// let order = window.orderMaster;
console.log(orderMaster);
let memberPhoneVerify = {};


async function getOrderMaster(){
    let url = "http://localhost:8080/orders/findOneByOrderId";
    // let order = {
    //     orderId: orderMaster.orderId,
    // }

    fetch('http://localhost:8080/orders/findOneByOrderId?orderId=' + orderMaster.orderId).then((res) => {
        if (res.ok){
            return res.json();
        }
    }).then((data) => {
        console.log(data);
    });
}

async function verifyPhoneNumber(){
    const verifyPhoneNumberBtn = $("#verifyPhoneNumberBtn");
    verifyPhoneNumberBtn.on("click", verifyPhoneFetch);
    verifyPhoneNumberBtn.disabled = true;
    // verifyPhoneNumberBtn.disabled = false;
}


async function verifyPhoneFetch(e){
    // let url = "http://localhost:8080/member/findMemberByPhone";

    e.preventDefault();
    console.log("click verify");
    // let memberPhoneForm = $("#memberPhoneForm");
    let memberNumFormData = new FormData(document.getElementById("memberPhoneForm"));

    try{
        const url = "http://localhost:8080/member/findMemberByPhone";
        const res = await fetch(url, {
            method: "POST",
            body: memberNumFormData,
        })
        if (!res.ok){
            errorData = await res.json();
            throw new Error(JSON.stringify(errorData));
        }
        memberPhoneVerify = await res.json();
        console.log(memberPhoneVerify);




        $("#verifyPhoneNumberBtn").disabled = false;
        return memberPhoneVerify;
    }catch(error){
        console.log(errorData);
        $("#memberStatus").addClass("error");
        $("#memberStatus").text("查無會員！");
        $("#verifyPhoneNumberBtn").disabled = false;
        return [];
    }

}

async function getMemberDiscPts(){
    
}



document.getElementById("memberPhoneForm").addEventListener("submit", function (e) {
    e.preventDefault(); // 阻止默認提交行為
    // console.log("表單提交已被攔截");
});


async function main(){
    await getOrderMaster();
}

verifyPhoneNumber();

main();