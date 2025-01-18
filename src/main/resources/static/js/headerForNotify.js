console.log("headerForNotify.js loaded");
// const restId = restId;

socket = new WebSocket(`/ws/notify/${restId}`);

socket.onopen = function(e){
    console.log("websocket connected");
}

socket.onmessage = function(e){
    console.log("websocket message received");
    console.log(JSON.parse(e.data));
    let data = JSON.parse(e.data);

    switch(data.type){
        case "service_bell":{
            $(".servicebell-notification-list").empty();
            $(".servicebell-icon-toggle-item").addClass("new-msg");
            break;
        }
        case "checkout":{
            $(".checkout-notification-list").empty();
            $(".checkout-icon-toggle-item").addClass("new-msg");
            break;
        }
        case "reservation":{
            $(".reservation-notification-list").empty();
            $(".reservation-icon-toggle-item").addClass("new-msg");
            break;
        }
    }

}

socket.onclose = function(e){
    console.log("websocket disconnected!");
}

// 讀取服務鈴通知
$(".servicebell-notification").click(async function(){
    // console.log("servicebell-icon-toggle-item clicked");
    $(".servicebell-icon-toggle-item").removeClass("new-msg");
    let bellMsgs = await getServiceBellData();
    console.log(bellMsgs);
    let bellMsgListEle = $(".servicebell-notification-list");

    if (bellMsgs != undefined){
        bellMsgListEle.empty();
        bellMsgs = Object.values(bellMsgs);
    
        for (let i = 0 ; i < bellMsgs.length ; i++){
            console.log(JSON.parse(bellMsgs[i]));
            let bellMsgJson = JSON.parse(bellMsgs[i]);
            console.log(bellMsgJson);
            let bellMsgLi = "";
    
            bellMsgLi += `
                            <li class="notification-item dropdown-item notify-item">
                                <div class="notification-title">
                                    <h4 class="mb-1 fw-bolder">服務鈴通知</h4>
                                </div>
                                <div class="notification-content">
                                    ${bellMsgJson.content}
                                </div>
                                <div class="notification-time">${bellMsgJson.time}</div>
                            </li>`;
    
            bellMsgListEle.prepend(bellMsgLi);
        }
    }
});

// 讀取預備結帳通知
$(".checkout-notification").click(async function(){
    // console.log("checkout-icon-toggle-item clicked");
    $(".checkout-icon-toggle-item").removeClass("new-msg");
    let checkOutMsgs = await getReqCheckOutData();
    console.log(checkOutMsgs);
    let checkOutMsgListEle = $(".checkout-notification-list");
    
    if (checkOutMsgs != undefined){
        checkOutMsgListEle.empty();
        checkOutMsgs = Object.values(checkOutMsgs);
    
        for (let i = 0 ; i < checkOutMsgs.length ; i++){
            console.log(JSON.parse(checkOutMsgs[i]));
            let checkOutMsgJson = JSON.parse(checkOutMsgs[i]);
            console.log(checkOutMsgJson);
            let checkOutMsgLi = "";
    
            checkOutMsgLi += `
                            <li class="notification-item dropdown-item notify-item">
                                <div class="notification-title">
                                    <h4 class="mb-1 fw-bolder">預備結帳通知</h4>
                                </div>
                                <div class="notification-content">
                                    ${checkOutMsgJson.content}
                                </div>
                                <div class="notification-time">${checkOutMsgJson.time}</div>
                            </li>`;
    
            checkOutMsgListEle.prepend(checkOutMsgLi);
        }
    }
});

// 讀取確認訂位通知
$(".reservation-notification").click(async function(){
    // console.log("reservation-icon-toggle-item clicked");
    $(".reservation-icon-toggle-item").removeClass("new-msg");
    let msgs = await getReservationConfirmData();
    console.log(msgs);
    let msgListEle = $(".reservation-notification-list");
    
    if (msgs != undefined){
        msgListEle.empty();
        msgs = Object.values(msgs);
    
        for (let i = 0 ; i < msgs.length ; i++){
            console.log(JSON.parse(msgs[i]));
            let msgJson = JSON.parse(msgs[i]);
            console.log(msgJson);
            let msgLi = "";
    
            msgLi += `
                            <li class="notification-item dropdown-item notify-item">
                                <div class="notification-title">
                                    <h4 class="mb-1 fw-bolder">訂位確認通知</h4>
                                </div>
                                <div class="notification-content">
                                    ${msgJson.content}
                                </div>
                                <div class="notification-time">${msgJson.time}</div>
                            </li>`;
    
            msgListEle.prepend(msgLi);
        }
    }
});



async function getServiceBellData(){
    let type = "service_bell"

    // 取得所有服務鈴資料
    try{
        let res = await fetch(`/notification/${restId}/${type}`);
        if (!res.ok){
            return {
                success: false,
                status: res.status,
                message: errorData.message || "error occured",
                errorData: errorData
            }
        }
        let notifyData = await res.json();
        console.log(notifyData.data);
        return notifyData.data;

    } catch (error){
        console.log(error);
    }
}

async function getReqCheckOutData(){
    let type = "checkout"

    // 取得所有預備結帳資料
    try{
        let res = await fetch(`/notification/${restId}/${type}`);
        if (!res.ok){
            return {
                success: false,
                status: res.status,
                message: errorData.message || "error occured",
                errorData: errorData
            }
        }
        let notifyData = await res.json();
        console.log(JSON.parse(notifyData.data));
        return JSON.parse(notifyData.data);

    } catch (error){
        console.log(error);
    }
}

async function getReservationConfirmData(){
    let type = "reservation"

    // 取得所有訂位確認資料
    try{
        let res = await fetch(`/notification/${restId}/${type}`);
        if (!res.ok){
            return {
                success: false,
                status: res.status,
                message: errorData.message || "error occured",
                errorData: errorData
            }
        }
        let notifyData = await res.json();
        console.log(JSON.parse(notifyData.data));
        return JSON.parse(notifyData.data);

    } catch (error){
        console.log(error);
    }
}