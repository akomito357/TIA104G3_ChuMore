console.log('aaamanage');
// let order = window.orderMaster;
console.log(orderMaster);


async function getOrderMaster(){
    let url = "http://localhost:8080/orders/findOneByOrderId";
    let order = {
        orderId: orderMaster.orderId,
    }

    console.log(order);

    // fetch('http://localhost:8080/orders/findOneByOrderId?orderId=' + orderMaster.orderId).then((res) => {
    //     if (res.ok){
    //         return res.json();
    //     }
    // }).then((data) => {
    //     console.log(data);
    // });

    $.ajax({
        url: url,
        method: 'GET',
    })

}

async function main(){
    await getOrderMaster();
}

main();