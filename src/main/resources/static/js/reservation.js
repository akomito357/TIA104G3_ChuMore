$(document).ready(function() {
    // 初始化日期選擇器
    $('#reservedDate').datepicker({
        format: 'yyyy-mm-dd',
        startDate: 'today',
        endDate: '+30d',
        autoclose: true,
        language: 'zh-TW'
    });


    const images = {
        environment: [
            'https://placehold.co/800x600?text=環境1',
            'https://placehold.co/800x600?text=環境2',
            'https://placehold.co/800x600?text=環境3',
            'https://placehold.co/800x600?text=環境4',
            'https://placehold.co/800x600?text=環境5',
            'https://placehold.co/800x600?text=環境6',
            'https://placehold.co/800x600?text=環境7',
            'https://placehold.co/800x600?text=環境8',
        ],
        menu: [
            'https://placehold.co/800x600?text=菜單1',
            'https://placehold.co/800x600?text=菜單2',
            'https://placehold.co/800x600?text=菜單3',
            'https://placehold.co/800x600?text=菜單4',
            'https://placehold.co/800x600?text=菜單5',
            'https://placehold.co/800x600?text=菜單6',
            'https://placehold.co/800x600?text=菜單7',
            'https://placehold.co/800x600?text=菜單8',
        ]
    };


    let currentTab = 'environment';


    // 取得request param 並初始化值

    const urlParams = new URLSearchParams(window.location.search);

    const restId = urlParams.get("restId");
    const reservationDate = urlParams.get("reservationDate");
    const reservationTime = urlParams.get("reservationTime");
    const guestCount = urlParams.get("guestCount");

    if (reservationDate) {
        $('#reservedDate').val(reservationDate);
    }

    if (guestCount) {
        $('#guestCount').val(guestCount);
    }

    if(restId){
        loadRestInfo(restId);
        initializeImageGrid(restId);
        // 初始化時段按鈕
        updateTimeSlots(restId);
    }

    // utilities
    function isOpen(hours,indices){
        return indices.some(index => hours[index] === 1);
    }


    // 訂位 ui logic
    async function generateTimeSlots(restId,guestCount, reservedDate){
        try {
            // 取得 daily Reservation 資料
            let availableTablesData = await getAvailableTablesData(restId,guestCount, reservedDate);
            let businessHours = await getRestBusinessHours(restId);
            let container = $('#timeSlots'); // 確認選擇器是否正確，假設 id 為 'timeSlots'

            console.log(businessHours);

            // 清空容器以避免重複生成
            container.empty();

            const isOpenSections = {
                "0000-0400": isOpen(businessHours, Array.from({ length: 5 }, (_, i) => i + 3)), // 0-3
                "0500-1000": isOpen(businessHours, Array.from({ length: 6 }, (_, i) => i + 4)), // 4-10
                "1100-1600": isOpen(businessHours, Array.from({ length: 7 }, (_, i) => i + 10)), // 11-16
                "1700-2300": isOpen(businessHours, Array.from({ length: 7 }, (_, i) => i + 16)), // 17-23
            };

            console.log(isOpenSections);

            for(let slotSection in isOpenSections){
                let sectionIsOpen = isOpenSections[slotSection];
                switch (slotSection){
                    case "0000-0400":
                        if(sectionIsOpen) {
                            generateTimeSlot("宵夜時段", "0000-0400", "midnightSlots", container);
                            generateTimeSlotsButton("midnightSlots", 0, 4, availableTablesData || {});
                        }
                        break;
                    case "0500-1000":
                        if(sectionIsOpen) {
                            generateTimeSlot("早餐時段", "0500-1000", "morningSlots", container);
                            generateTimeSlotsButton("morningSlots", 5, 10, availableTablesData || {});
                        }
                        break;
                    case "1100-1600":
                        if(sectionIsOpen) {
                            generateTimeSlot("午餐&下午茶時段", "1100-1600", "noonSlots", container);
                            generateTimeSlotsButton("noonSlots", 11, 16, availableTablesData|| {});
                        }
                        break;
                    case "1700-2300":
                        if(sectionIsOpen){
                            generateTimeSlot("晚餐時段", "1700-2300", "eveningSlots", container);
                            generateTimeSlotsButton("eveningSlots", 17, 23, availableTablesData|| {});
                        }
                        break;
                }
            }
        } catch (error) {
            console.error("生成時段失敗:", error);
        }
    }

    function generateTimeSlot(sectionName, slotSection, sectionId, container){
        let timeSlot =
            `<div class="time-slot-section">
            <h5>${sectionName} (${slotSection})</h5>
            <div class="time-slots-grid" id="${sectionId}">
                <!-- 動態生成時段按鈕 -->
            </div>
        </div>`;
        container.append(timeSlot);
    }


    function generateTimeSlotsButton(containerId,startHour,endHour,availableTablesData){
        const container = $(`#${containerId}`);

        for(let hour = startHour; hour <= endHour; hour++){
            const time = `${hour.toString().padStart(2,'0')}:00`;
            const availableTables =  parseInt(availableTablesData[hour])
            const isAvailable =  availableTables ? true:false;
            if(availableTables <= 3){
                const button = $('<button>')
                    .addClass('btn time-slot-btn')
                    .addClass(isAvailable ? 'btn-outline-primary':'disabled-slot')
                    .prop('disabled',!isAvailable)
                    .data('time',time)
                    .html(`${time}<br>${isAvailable ? `剩餘${availableTables}桌`:'已滿'}`);
                container.append(button);
            }else{
                const button = $('<button>')
                    .addClass('btn time-slot-btn')
                    .addClass(isAvailable ? 'btn-outline-primary':'disabled-slot')
                    .prop('disabled',!isAvailable)
                    .data('time',time)
                    .html(`${time}`);
                container.append(button);
            }

        }
    }

    async function updateTimeSlots(restId) {
        const date = $('#reservedDate').val();
        const guests = $('#guestCount').val();

        if (date && guests) {
            await generateTimeSlots(restId,guests, date);
            // 重新綁定點擊事件
            $('.time-slot-btn').off('click').on('click', function() {
                if (!$(this).hasClass('disabled-slot')) {
                    $('.time-slot-btn').removeClass('active');
                    $(this).addClass('active');
                    updateBookingSummary();
                }
            });
        }
    }

    // 修改樣式
    $('<style>')
        .text(`
                    .time-slot-btn {
                        width: 100px;
                        margin: 5px;
                        font-size: 0.9rem;
                        border-radius: 8px;
                        text-align: center;
                        line-height: 1.2;
                    }
                    .time-slot-btn.disabled-slot {
                        opacity: 0.6;
                        cursor: not-allowed;
                        background-color: #f8f9fa;
                        border-color: #dee2e6;
                        color: #6c757d;
                    }
                `)
        .appendTo('head');

    // 綁定事件
    $('#guestCount, #reservedDate').change(updateTimeSlots.bind(null,restId));




    // 更新訂位資訊
    function updateBookingSummary() {
        const guestCount = $('#guestCount').val();
        const reservationDate = $('#reservedDate').val();
        const reservationTime = $('.time-slot-btn.active').data('time');

        let summaryText = '請選擇訂位資訊';
        let isDisabled = true;

        if (guestCount && reservationDate && reservationTime) {
            summaryText = `訂位資訊：${reservationDate} ${reservationTime} - ${guestCount}人`;
            isDisabled = false;
        }

        // 更新浮動欄和固定區域的內容
        $('#bookingSummary, .booking-summary-fixed').html(summaryText);
        $('#confirmBooking, #confirmBookingFixed').prop('disabled', isDisabled);
    }

    // 綁定事件
    $('#guestCount, #reservedDate').change(updateBookingSummary);

    // 確認訂位
    $('#confirmBooking, #confirmBookingFixed').click(confirmReservation.bind(null,restId));

    // 訂位 data logic

    async function getAvailableTablesData(restId,guestCount,reservedDate){
        try{
            const url = `http://localhost:8080/test/dailyReservations/availableTables?restId=${restId}&guestCount=${guestCount}&reservedDate=${reservedDate}`;
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
            console.log(error.message);
            return [];
        }
    }

    async function getRestData(restId){
        try{
            let url = `http://localhost:8080/test/rests/rest/${restId}`;
            const res = await fetch(url);
            if(!res.ok){
                if(res.status === 404){
                    errorData = await res.json();
                    throw new Error(JSON.stringify(errorData));
                }
            }
            let data = await res.json();
            const filteredData = {
                restId: data.restId,
                restName: data.restName,
                restCity: data.restCity,
                restDist: data.restDist,
                restAddress: data.restCity+data.restDist+data.restAddress,
                restRegist: data.restRegist,
                restPhone: data.restPhone,
                cuisineTypeName: data.cuisineTypeName,
                restIntro: data.restIntro,
                merchantName: data.merchantName,
                merchantIdNumber: data.merchantIdNumber,
                merchantEmail: data.merchantEmail,
                merchantPassword: data.merchantPassword,
                phoneNumber: data.phoneNumber,
                businessStatus: data.businessStatus,
                weeklyBizDays: data.weeklyBizDays,
                orderTableCount: data.orderTableCount,
                restStars: data.restStars,
                restReviewers: data.restReviewers,
                approvalStatus: data.approvalStatus,
                registerDatetime: data.registerDatetime,
                createdDatetime: data.createdDatetime,
                updatedDatetime: data.updatedDatetime,
            };
            return filteredData;
        }catch(error){
            console.log(error.message);
            return [];
        }
    }

    async function getRestFormattedBusinessHours(restId){
        try{
            let url = `http://localhost:8080/test/rests/rest/${restId}/formattedBusinessHours`;
            const res = await fetch(url);
            if(!res.ok){
                if(res.status === 404){
                    errorData = await res.json();
                    throw new Error(JSON.stringify(errorData));
                }
            }
            let businessHours = await res.json();
            return businessHours;
        }catch(error){
            console.log(error.message);
            return [];
        }
    }

    async function getRestBusinessHours(restId){
        try{
            let url = `http://localhost:8080/test/rests/rest/${restId}/businessHours`;
            const res = await fetch(url);
            if(!res.ok){
                if(res.status === 404){
                    errorData = await res.json();
                    throw new Error(JSON.stringify(errorData));
                }
            }
            let businessHours = await res.json();
            return businessHours;
        }catch(error){
            console.log(error.message);
            return [];
        }
    }


    async function confirmReservation(restId) {
        const reservationInfo = {
            reservationDate: $('#reservedDate').val(),
            reservationTime: $('.time-slot-btn.active').data('time'),
            guestCount: $('#guestCount').val()
        };

        //按下確認訂位後，跳轉至確認訂位頁面
        try{
            const url = `http://localhost:8080/test/reservations/reservation/confirm?memberId=1072&restId=${restId}`;
            const res = await fetch(url,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(reservationInfo)
                }
            );
            if(!res.ok){
                throw new Error('提交失敗！狀態碼：' + res.status);
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


    async function loadRestInfo(restId){
        let restData = await getRestData(restId);
        let businessHours = await getRestFormattedBusinessHours(restId);

        let restNameEl = $('#restName');
        let restAddressEl = $('#restAddress');
        let restPhoneEl = $('#restPhone');
        let restBusinessHoursEl = $('#restBusinessHours');
        let cuisineTypeEl = $('#cuisineType');
        let navButtonEl = $('#navButton');
        let callButtonEl = $('#callButton');
        let mapIframeEl = $('#mapIframe');

        let googleMapsUrl = `https://maps.google.com/?q=${encodeURIComponent(restData.restAddress)}`;
        navButtonEl.attr('href', googleMapsUrl);

        let telUrl = `tel:${restData.restPhone}`;
        callButtonEl.attr('href', telUrl);

        let mapIframeSrc = `https://maps.google.com/maps?q=${encodeURIComponent(restData.restAddress)}&z=16&output=embed`
        mapIframeEl.attr('src', mapIframeSrc);

        restNameEl.text(restData.restName);
        restAddressEl.text(restData.restAddress);
        restPhoneEl.text(restData.restPhone);
        cuisineTypeEl.text(restData.cuisineTypeName);
        let businessHoursText = businessHours.join(', ');
        restBusinessHoursEl.text(businessHoursText);
    }



    // 訂位資訊顯示

    // 添加滾動監聽
    let lastScrollPosition = 0;
    const $floatingBar = $('.floating-bar');
    const $window = $(window);

    function isNearBottom() {
        const windowHeight = $window.height();
        const documentHeight = $(document).height();
        const scrollPosition = $window.scrollTop();

        // 當距離底部 50px 內時，視為接近底部
        return (documentHeight - (scrollPosition + windowHeight)) <= 50;
    }

    $window.scroll(function() {
        const currentScroll = $window.scrollTop();

        if (isNearBottom()) {
            $floatingBar.addClass('hidden');
        } else {
            $floatingBar.removeClass('hidden');
        }

        lastScrollPosition = currentScroll;
    });



    // 圖片


    // 取得環境圖片
    async function fetchEnvImages(restId){
        let imgUrlList = [];
        try {
            let res = await fetch(`http://localhost:8080/envImg/images/${restId}`);
            if (!res.ok) {
                throw new Error(`Failed to fetch image IDs: ${res.status}`);
            }
            let imagesIds = await res.json();

            for (const id of imagesIds) {
                imgUrlList.push(`http://localhost:8080/envImg/image/${id}`);
            }

            return imgUrlList;
        } catch (error) {
            console.error(error);
            return [];
        }
    }

    // 取得菜單圖片
    async function fetchMenuImages(restId){
        let imgUrlList = [];
        try {
            let res = await fetch(`http://localhost:8080/menuImg/images/${restId}`);
            if (!res.ok) {
                throw new Error(`Failed to fetch image IDs: ${res.status}`);
            }
            let imagesIds = await res.json();

            for (const id of imagesIds) {
                imgUrlList.push(`http://localhost:8080/menuImg/image/${id}`);
            }

            return imgUrlList;
        } catch (error) {
            console.error(error);
            return [];
        }
    }


    async function loadImagesUrl(restId){
        images["environment"] = await fetchEnvImages(restId);
        images["menu"] = await fetchMenuImages(restId);
    }


    async function populateImageGrid(restId){
        await loadImagesUrl(restId);

        // 更新輪播圖
        const carouselInner = document.querySelector('#heroCarousel .carousel-inner');
        carouselInner.innerHTML = '';

        // 添加環境照片
        if (images.environment && images.environment.length > 0) {
            const envDiv = document.createElement('div');
            envDiv.className = 'carousel-item active';
            envDiv.innerHTML = `
                    <img src="${images.environment[0]}" class="d-block w-100" alt="餐廳環境">
                    <div class="carousel-caption">
                        <h3>精緻用餐環境</h3>
                        <p>享受無與倫比的美食體驗</p>
                    </div>
                `;
            carouselInner.appendChild(envDiv);
        }

        // 添加菜單照片
        if (images.menu && images.menu.length > 0) {
            const menuDiv = document.createElement('div');
            menuDiv.className = 'carousel-item';
            menuDiv.innerHTML = `
                    <img src="${images.menu[0]}" class="d-block w-100" alt="主廚特餐">
                    <div class="carousel-caption">
                        <h3>主廚特製料理</h3>
                        <p>每日精選新鮮食材</p>
                    </div>
                `;
            carouselInner.appendChild(menuDiv);
        }

        // 原有的圖片格狀排列代碼
        const imageGrid = document.querySelector('.image-grid .row.g-2');
        imageGrid.innerHTML = '';

        const environmentImages = images.environment;

        if(!environmentImages || environmentImages.length === 0 ){
            imageGrid.innerHTML = `
                    <div class="col-6">
                        <img src="https://placehold.co/400x300" class="img-fluid w-100 rounded"
                             alt="圖片2" style="object-fit: cover; cursor: pointer; height: 200px;>
                    </div>
                    <div class="col-6">
                        <img src="https://placehold.co/400x300" class="img-fluid w-100 rounded"
                             alt="圖片3" style="object-fit: cover; cursor: pointer; height: 200px;>
                    </div>
                    <div class="col-6">
                        <img src="https://placehold.co/400x300" class="img-fluid w-100 rounded"
                             alt="圖片4" style="object-fit: cover; cursor: pointer; height: 200px;>
                    </div>
                    <div class="col-6 position-relative">
                        <img src="https://placehold.co/400x300" class="img-fluid w-100 rounded"
                             alt="更多圖片" style="object-fit: cover; cursor: pointer; height: 200px;">
                        <div class="more-images">
                            <span>+12張</span>
                        </div>
                    </div>
                `;
            return ;
        }

        const mainImageSrc = environmentImages[0];
        const smallImages = environmentImages.slice(1,4);
        const remainingCount = environmentImages.length - 4;

        // 大圖
        const mainImageCol = document.createElement('div');
        mainImageCol.className = `col-md-6`;
        mainImageCol.innerHTML = `
                <div class="main-image">
                <img src="${mainImageSrc}" class="img-fluid w-100 h-100 rounded"
                     alt="主圖" style="object-fit: cover; cursor: pointer;"
                     onclick="openImageViewer(0)">
                </div>
            `;
        imageGrid.appendChild(mainImageCol);

        // 小圖
        const smallImagesCol = document.createElement('div');
        smallImagesCol.className = 'col-md-6';
        const smallImagesRow = document.createElement('div');
        smallImagesRow.className = 'row g-2';
        smallImagesCol.appendChild(smallImagesRow);
        imageGrid.appendChild(smallImagesCol);

        smallImages.forEach((imgSrc,index)=>{
            const col = document.createElement('div');
            col.className = 'col-6';
            col.innerHTML = `
                    <img src="${imgSrc}" class="img-fluid w-100 rounded"
                     alt="圖片${index + 2}" style="object-fit: cover; cursor: pointer; height: 200px;"
                     onclick="openImageViewer(${index + 1})">
                `
            smallImagesRow.appendChild(col);
        })

        // 如果有更多圖片，增加「更多圖片」覆蓋層
        if (remainingCount > 0) {
            const moreCol = document.createElement('div');
            moreCol.className = 'col-6 position-relative';
            moreCol.innerHTML = `
                    <img src="${environmentImages[4]}" class="img-fluid w-100 rounded"
                         alt="更多圖片" style="object-fit: cover; cursor: pointer; height: 200px;"
                         onclick="openImageViewer(4)">
                    <div class="more-images" onclick="openImageViewer(4)">
                        <span>+${remainingCount}張</span>
                    </div>
        `;
            smallImagesRow.appendChild(moreCol);

        }

    }


    async function initializeImageGrid(restId){
        await loadImagesUrl(restId);
        await populateImageGrid(restId);
    }


    // 為所有圖片和 more-images 添加點擊事件
    $('.image-grid img, .more-images').click(function(e) {
        e.preventDefault();
        let index = 0;

        // 如果是圖片被點擊
        if ($(this).is('img')) {
            // 找出被點擊的圖片在所有圖片中的索引
            index = $('.image-grid img').index(this);
        }
        // 如果是 more-images 被點擊，直接從第一張開始顯示

        openImageViewer(index);
    });

    window.openImageViewer = function(index) {
        const modal = new bootstrap.Modal(document.getElementById('imageViewerModal'));
        updateCarouselImages(currentTab);

        // 確保輪播圖重置到正確的圖片
        const carousel = document.getElementById('imageViewerCarousel');
        const carouselInstance = bootstrap.Carousel.getInstance(carousel) || new bootstrap.Carousel(carousel);

        // 先移除所有 active class
        carousel.querySelectorAll('.carousel-item').forEach(item => {
            item.classList.remove('active');
        });

        // 設定正確的 active item
        const items = carousel.querySelectorAll('.carousel-item');
        if (items[index]) {
            items[index].classList.add('active');
        }

        updateImageCounter(index + 1);
        modal.show();

        // 如果需要，可以手動滑動到指定圖片
        carouselInstance.to(index);
    }

    // 更新圖片資料

    window.switchTab = function(tab) {
        currentTab = tab;
        document.querySelectorAll('.btn-group .btn').forEach(btn => {
            btn.classList.remove('active');
        });
        event.target.classList.add('active');
        updateCarouselImages(tab);
        updateImageCounter(1);
    }

    function updateCarouselImages(tab) {
        const carouselInner = document.querySelector('#imageViewerCarousel .carousel-inner');
        carouselInner.innerHTML = '';

        images[tab].forEach((src, index) => {
            const div = document.createElement('div');
            div.className = 'carousel-item' + (index === 0 ? ' active' : '');
            div.innerHTML = `<img src="${src}" class="d-block mx-auto" alt="圖片${index + 1}">`;
            carouselInner.appendChild(div);
        });
    }

    function updateImageCounter(current) {
        const counter = document.querySelector('.image-counter');
        const total = images[currentTab].length;
        counter.textContent = `${current}/${total}`;
    }

    // 監聽輪播圖切換事件
    document.getElementById('imageViewerCarousel').addEventListener('slide.bs.carousel', function (event) {
        updateImageCounter(event.to + 1);
    });
});