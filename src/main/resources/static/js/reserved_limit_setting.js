$(document).ready(function(){
    const today = new Date().toISOString().split('T')[0];
    $('#queryDate').val(today).trigger('change');
    $('#adjustDate').val(today).trigger('change');


    const form  = $('#adjustmentForm');
    form.on('submit',adjustTables);

    queryTables();
})

// 開始時間是否小於結束時間的檢查處理
$('#startTime','#endTime').on('change',function(){
    let time = $(this).val();
    if (time) {
        // 確保時間是整點格式
        time = adjustToHour(time);
        $(this).val(time); // 更新輸入框的值

        const startTime = $('#startTime').val();
        const endTime = $('#endTime').val();
        // 確保開始時間小於結束時間
        if (startTime && endTime && startTime >= endTime) {
            alert('開始時間必須小於結束時間');
            $(this).val(''); // 清空錯誤的時間
        }
    }
})

function adjustToHour(time) {
    const [hour, minute] = time.split(':').map(Number); // 拆分小時和分鐘
    return `${String(hour).padStart(2, '0')}:00`; // 保持兩位數格式，並將分鐘設為 00
}


// 調整可訂位上限
async function adjustTables(e){
    e.preventDefault(); // 不以表單而以JSON的方式送出

    const adjustment = {
        reservedDate: $('#adjustDate').val(),
        startTime: $('#startTime').val(),
        endTime: $('#endTime').val(),
        tableType: $('#tableType').val(),
        operation: $('#operation').val(),
        adjustmentQuantities: $('#adjustmentQuantities').val()
    }


    try{
        const url = `/rests/dailyReservations/reservedLimits`
        const res = await fetch(url,{
            method:'POST',
            headers:{
                'Content-Type':'application/json'
            },
            body: JSON.stringify(adjustment)
        });

        if(!res.ok){
            if(res.status.toString().startsWith('4') || res.status.toString().startsWith('5')){
                errorData = await res.json();
                throw new Error(JSON.stringify(errorData));
            }
        }
        let reservedLimits = await res.json();
        reservedLimits = transformData(reservedLimits);
        updateTable(reservedLimits);
        showAdjustmentRecord(adjustment);
    }catch(error){
        console.log(error.message);
        showErrorMessage(error);
        return [];
    }


}

function showAdjustmentRecord(adjustment){

    const queryRecord = $('#queryRecord');
    queryRecord.addClass('d-none').find('.record-content').text('');


    const record = `[調整結果] 日期：${adjustment.reservedDate}，時間：${adjustment.startTime || '00:00'} - ${adjustment.endTime || '23:59'}，` +
        `${adjustment.tableType}人桌 ${adjustment.operation === 'increase' ? '+' : '-'}${adjustment.adjustmentQuantities}`;

    const recordElement = $('#adjustmentRecord');
    recordElement.removeClass('d-none')
        .find('.record-content')
        .text(record);


    // 添加視覺效果
    recordElement.hide().fadeIn().css('background-color', '#e8f5e9')
        .delay(200)
        .queue(function(next){
            $(this).css('background-color', '#e9ecef');
            next();
        });
}

function showErrorMessage(error){
    const errorMessage = `[錯誤] ${error.status}:${error.error}`;

    $('#errorDisplay').removeClass('d-none')
        .find('.error-content')
        .text(message);
}


function showQueryRecord(date){
    const record = `[查詢結果] 目前顯示 ${date} 的訂位開放狀況`;
    const adjustmentRecord = $('#adjustmentRecord');
    adjustmentRecord.addClass('d-none').find('.record-content').text('');

    $('#queryRecord').removeClass('d-none')
        .find('.record-content')
        .text(record);
}


// 查詢可訂位上限
async function queryTables(){
    // 取得日期
    const date = $('#queryDate').val();
    // 取得資料
    let reservedLimits = await getReservedLimit(date);
    // 更新表格
    reservedLimits = transformData(reservedLimits);
    // 顯示查詢記錄
    showQueryRecord(date);
    updateTable(reservedLimits);
}

// 更新表格
async function updateTable(data){
    const tbody = $('#tableBody');
    tbody.empty();
    data.forEach( dailyReservation =>{
            const row = $('<tr>');
            row.append(`<td class="time-cell">${dailyReservation.time}</td>`);
            row.append(`<td>${dailyReservation.tables['1-2']}</td>`);
            row.append(`<td>${dailyReservation.tables['3-4']}</td>`);
            row.append(`<td>${dailyReservation.tables['5-8']}</td>`);
            tbody.append(row);
        }
    );
}


// 將資料轉換成表格所需格式
function transformData(data) {
    const times = [];
    for (let hour = 0; hour < 24; hour++) {
        // 格式化時間，補0以確保兩位數格式
        const startHour = hour.toString().padStart(2, '0');      // 補0的開始時間
        const endHour = (hour + 1).toString().padStart(2, '0');  // 補0的結束時間
        const table2 = data['2'][hour];
        const table4 =  data['4'][hour];
        const table8 = data['8'][hour];
        if( table2===0 && table4===0 && table8===0 ){
            continue;
        }

        times.push({
            time: `${startHour}:00-${endHour}:00`, // 使用補0格式化時間
            tables: {
                '1-2': table2,
                '3-4': table4,
                '5-8': table8
            }
        });
    }
    return times;
}


//透過 fetch 取得訂位上限資料
async function getReservedLimit(date){
    try{
        const url = `/rests/dailyReservations?reservedDate=${date}`;
        const res = await fetch(url);

        if(!res.ok){
            if(res.status === 404){
                errorData = await res.json();
                throw new Error(JSON.stringify(errorData));
            }
        }
        const reservedLimits = await res.json();
        return reservedLimits;
    }catch(error){
        console.error(error.message);
        return [];
    }
}
