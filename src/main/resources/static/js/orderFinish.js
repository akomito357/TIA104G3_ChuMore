console.log("orderFinish.js");

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

async function main(){
    await getOrderMaster();
}