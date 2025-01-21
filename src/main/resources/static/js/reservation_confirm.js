$(document).ready( async function() {
    // 儲存原始值
    let originalDate = '';
    let originalGuestCount = '';
    let hasLoadedTimes = false;  // 新增：追蹤是否已載入時間選項

    let params = new URLSearchParams(window.location.search);
    let images = await fetchEnvImages(params.get("restId"));
    $(".card-img-top").attr("src", images[0]);


    // Modal 開啟時儲存原始值
    $('#editModal').on('show.bs.modal', function () {
        originalDate = $('#reservationDate').val();
        originalGuestCount = $('#guestCount').val();
        hasLoadedTimes = false;  // 重設載入狀態
    });

    // 監聽日期和人數的變更
    $('#reservationDate, #guestCount').change(function() {
        const currentDate = $('#reservationDate').val();
        const currentGuestCount = $('#guestCount').val();

        if (currentDate !== originalDate || currentGuestCount !== originalGuestCount) {
            updateAvailableTime();
        }
    });

    // 新增：監聽時間選單的點擊事件
    $('#reservationTime').on('mousedown focus', function(e) {
        if (!hasLoadedTimes) {
            e.preventDefault();  // 防止下拉選單立即開啟
            updateAvailableTime().then(() => {
                hasLoadedTimes = true;
                $(this).trigger('click');  // 手動觸發下拉選單
            });
        }
    });

    async function fetchEnvImages(restId) {
        let imgUrlList = [];
        try {
            let res = await fetch(`/envImg/images/${restId}`);
            if (!res.ok) {
                throw new Error(`Failed to fetch image IDs: ${res.status}`);
            }
            let imagesIds = await res.json();

            for (const id of imagesIds) {
                imgUrlList.push(`/envImg/image/${id}`);
            }

            return imgUrlList;
        } catch (error) {
            console.error(error);
            return [];
        }
    }

    async function updateAvailableTime(){
        const reservationDate = $('#reservationDate').val();
        const guestCount = $('#guestCount').val();
        const restId = $('input[name="restId"]').val();
        const currentTime = $('#reservationTime').val();  // 保存當前選擇的時間

        if(!reservationDate || !guestCount) return;

        const timeSlot = $('#reservationTime');
        timeSlot.empty().append('<option value="">載入中...</option>').prop('disabled', true);

        try {
            let availableTablesData = await getAvailableTablesData(restId, guestCount, reservationDate);
            timeSlot.empty();

            // 如果當前時間仍然可用，則加入當前時間作為第一個選項
            if (currentTime) {
                timeSlot.append(
                    $('<option>', {
                        value: currentTime,
                        text: currentTime,
                        selected: true
                    })
                );
            }

            availableTablesData.forEach((tables, index) => {
                if (tables > 0) {
                    const hour = index.toString().padStart(2, '0');
                    const time = `${hour}:00`;
                    if (time !== currentTime) {  // 避免重複加入當前時間
                        timeSlot.append(
                            $('<option>', {
                                value: time,
                                text: time
                            })
                        );
                    }
                }
            });
            timeSlot.prop('disabled', false);
        } catch(error) {
            console.error(error);
            timeSlot.empty()
                .append('<option value="">發生錯誤，請重試</option>')
                .prop('disabled', false);
        }
    }

    async function getAvailableTablesData(restId,guestCount,reservedDate){
        try{
            const url = `/dailyReservations/availableTables?restId=${restId}&guestCount=${guestCount}&reservedDate=${reservedDate}`;
            const res = await fetch(url);
            if(!res.ok){
                if(res.status === 404){
                    errorData = await res.json();
                    throw new Error(JSON.stringify(errorData));
                }
            }

            let availableTablesData = await res.json();
            return availableTablesData;
        }catch(error){
            throw new Error(JSON.stringify(errorData));
        }
    }


    // 更新左側卡片的訂位資訊
    function updateDisplayInfo(date, time, guestCount) {
        // 格式化日期顯示，包含星期
        const dateObj = new Date(date);
        const weekDay = ['日', '一', '二', '三', '四', '五', '六'][dateObj.getDay()];
        const formattedDate = `${date.replace(/-/g, '/')} (${weekDay})`;

        $('#displayDate').text(formattedDate);
        $('#displayTime').text(time);
        $('#displayGuestCount').text(guestCount + '位');
    }

    // 修改表單提交處理
    $('#editForm').on('submit', function(e) {
        e.preventDefault();

        // 獲取表單的值
        const newDate = $('#reservationDate').val();
        const newTime = $('#reservationTime').val();
        const newGuestCount = $('#guestCount').val();

        // 更新左側卡片顯示
        updateDisplayInfo(newDate, newTime, newGuestCount);

        // 關閉 Modal
        $('#editModal').modal('hide');

        // 更新原始值，避免不必要的重新載入
        originalDate = newDate;
        originalGuestCount = newGuestCount;
        hasLoadedTimes = false;
    });


    $('#bookingForm').submit(addReservation);


    async function addReservation(e){
        e.preventDefault();
        let newReservation = {
            reservationDate : $('#reservationDate').val(),
            reservationTime : $('#reservationTime').val(),
            guestCount : $('#guestCount').val()
        }

        let reservationStatus;

        try{
            let restId = $('#restId').val();
            let memberId = $('#memberId').val();
            console.log(restId);
            console.log(memberId);
            const url = `/reservations/reservation?restId=${restId}&memberId=${memberId}`;
            const res = await fetch(url,{
                method:'POST',
                headers:{
                    'Content-Type':'application/json'
                },
                body: JSON.stringify(newReservation)
            });


            if(!res.ok){
                if(res.status.toString().startsWith('4') || res.status.toString().startsWith('5')){
                    errorData = await res.json();
                    throw new Error(JSON.stringify(errorData));
                }
            }
            reservationStatus = "success";
            newReservation = await res.json();
            alert("訂位確認信已寄出！\n");
            await transferToReservationStatusPage(reservationStatus);
        }catch(error){
            reservationStatus = "error";
            await  transferToReservationStatusPage(reservationStatus);
        }

    }

    async function transferToReservationStatusPage(reservationStatus){
        let url = `/member/reservations/reservation/${reservationStatus}`;
        try{
            const res = await fetch(url);
            if(!res.ok){
                errorData = await res.text();
                throw new Error('提交失敗！：' + errorData);
            }
            const html = await res.text();
            document.open();
            document.write(html);
            document.close();
        }catch(error){
            console.log(error.message);
            alert('提交失敗，請稍後再試！');
        }
    }


    $('#back').click(function() {
        history.back();
    });

    $('.terms-text a').click(function(e) {
        e.preventDefault();
        alert('開啟條款內容');
    });
});
