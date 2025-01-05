
console.log("aaaa");

function startOrder(){
    const startOrderBtn = document.getElementById("startOrderBtn");
    const orderForm = $(".orderForm");
    orderForm.on("submit", (e)=>{
        e.preventDefault();
        // console.log("start order");

        // const orderMasterJson = {
        //     orderTableId: $("input#orderTable").val(),
        //     restId: $("input#rest").val(),
        //     orderStatus: $("input#orderStatus").val(),
        //     servedDatetime: $("input#servedDatetime").val(),
        // }
        // console.log(orderMasterJson);

        // insertOrder(orderMasterJson);
        getData();
        orderForm.submit();

    })
    orderForm.on("submit", (e)=>{
        getData();
    })
}

function getData(){
    fetch("/orders/findOneFromSession").then((res)=>{
        if(res.ok){
            console.log(res)
            return res.json();
        }}).then((data)=>{
            console.log(data);
    });
}

// $(document).ready(function(){
//     startOrder();
// });

function insertOrder(orderMasterJson){
    $.ajax({
        url: "/orders/insert",
        type: "POST",
        data: JSON.stringify(orderMasterJson),
        contentType: "application/json; charset=utf-8",
        success: function(res){
            console.log(res.data);
        },
        error: function(error){
            console.log("Error:", error);
        }
    })
}


