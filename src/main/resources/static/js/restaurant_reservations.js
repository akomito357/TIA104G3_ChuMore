// -----------------------------------
// 全域變數
// -----------------------------------
let calendar = null;
let reservations = []; // 給「歷史訂位」使用
let selectedHistoryDate=null;

// -----------------------------------
// data logic：getReservations()
// -----------------------------------
async function getReservations() {
    try {
        const res = await fetch("/rests/reservations");
        if (!res.ok) {
            if (res.status === 404) {
                const errorData = await res.json();
                throw new Error(JSON.stringify(errorData));
            }
        }
        return await res.json();
    } catch (error) {
        console.error(error.message);
        return [];
    }
}

// -----------------------------------
// 當日訂位
// -----------------------------------
function loadTodayBookings() {
    // 銷毀現有的 DataTables
    if ($.fn.DataTable.isDataTable('#upcomingBookingTable')) {
        $('#upcomingBookingTable').DataTable().destroy();
    }
    if ($.fn.DataTable.isDataTable('#completedBookingTable')) {
        $('#completedBookingTable').DataTable().destroy();
    }
    // 清除 DataTable 添加的樣式和屬性
    $('#upcomingBookingTable tbody').removeClass('dataTable').removeAttr('style');
    $('#upcomingBookingTable').removeClass('dataTable');
    $('#completedBookingTable tbody').removeClass('dataTable').removeAttr('style');
    $('#completedBookingTable').removeClass('dataTable');

    const today = new Date().toISOString().split('T')[0];
    const currentTime = new Date().toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' });

    // 只篩選今天
    const todayBookings = reservations.filter(b => b.reservationDate === today);


    // 接下來的訂位
    const upcomingBookings = todayBookings.filter(b =>
        String(b.reservationStatus) === '1' && b.reservationTime >= currentTime
    );
    // 已完成的訂位
    const completedBookings = todayBookings.filter(b =>
        String(b.reservationStatus)  === '2' ||
        String(b.reservationStatus)  === '0' ||
        (String(b.reservationStatus)  === '1' && b.reservationTime < currentTime)
    );

    // 填充「接下來」表格
    const upcomingTbody = $('#upcomingBookingTable tbody');
    upcomingTbody.empty();
    if (upcomingBookings.length > 0) {
        upcomingBookings.sort((a, b) => a.reservationTime.localeCompare(b.reservationTime))
            .forEach(booking => {
                const row = $('<tr>').attr('data-id', booking.reservationId);
                row.html(`
                        <td><span class="badge bg-info time-badge">${booking.reservationTime}</span></td>
                        <td>${booking.memberName}</td>
                        <td>${booking.phoneNumber}</td>
                        <td>${booking.guestCount}</td>
                        <td><span class="badge status-badge ${getStatusClass(booking.reservationStatus)}">${getStatusText(booking.reservationStatus)}</span></td>
                        <td>${getActionButtons(booking)}</td>
                    `);
                upcomingTbody.append(row);
            });
    }

    // 填充「已完成」表格
    const completedTbody = $('#completedBookingTable tbody');
    completedTbody.empty();
    if (completedBookings.length > 0) {
        completedBookings.sort((a, b) => b.reservationTime.localeCompare(a.reservationTime))
            .forEach(booking => {
                const row = $('<tr>').attr('data-id', booking.reservationId);
                row.html(`
                        <td><span class="badge bg-info time-badge">${booking.reservationTime}</span></td>
                        <td>${booking.memberName}</td>
                        <td>${booking.phoneNumber}</td>
                        <td>${booking.guestCount}</td>
                        <td><span class="badge status-badge ${getStatusClass(booking.reservationStatus)}">${getStatusText(booking.reservationStatus)}</span></td>
                        <td>${getActionButtons(booking)}</td>
                    `);
                completedTbody.append(row);
            });
    }

    // 初始化 DataTables
    const tableConfig = {
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/zh-HANT.json'
        },
        responsive: true,
        pageLength: 5,
        lengthMenu: [[5, 10, 25, -1], [5, 10, 25, '全部']],
        dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>rtip'
    };
    $('#upcomingBookingTable').DataTable({
        ...tableConfig,
        language: {
            ...tableConfig.language,
            emptyTable: "沒有接下來的訂位"
        },
        order: [[0, 'asc']],       // 訂位時間由早到晚
        columnDefs: [{ orderable: false, targets: 5 }]
    });
    $('#completedBookingTable').DataTable({
        ...tableConfig,
        language: {
            ...tableConfig.language,
            emptyTable: "沒有已完成的訂位"
        },
        order: [[0, 'desc']],     // 已完成的訂位依時間反向排序
        columnDefs: [{ orderable: false, targets: 5 }]
    });
}

// 返回「當日訂位」
function returnToTodayBookings() {
    $('#todaySearchName').val('');
    $('#todaySearchPhone').val('');
    $('#startTime').val('00:00');
    $('#endTime').val('23:59');
    $('#todayStatusFilter').val('all');

    $('#searchResultTable').hide();
    $('#todayBookingsSection').show();
    loadTodayBookings();
}

function refreshHistoryBookings() {
    if ($("#history").hasClass('show')) {
        // 如果有選定的歷史日期，將篩選條件設為該日期
        if (selectedHistoryDate) {
            $('#historyStartDate').val(selectedHistoryDate);
            $('#historyEndDate').val(selectedHistoryDate);

            // 重置其他篩選條件
            $('#historySearchName').val('');
            $('#historySearchPhone').val('');
            $('#historyStartTime').val('');
            $('#historyEndTime').val('');
            $('#historyStatusFilter').val('all');
        }
        applyHistoryFilters();
    }
}

// 當日搜尋 & 篩選
function applyTodayFilters() {
    const searchName = $('#todaySearchName').val().trim();
    const searchPhone = $('#todaySearchPhone').val().trim();
    const startTime = $('#startTime').val();
    const endTime = $('#endTime').val();
    const status = $('#todayStatusFilter').val();

    const hasFilters = (
        searchName !== '' ||
        searchPhone !== '' ||
        startTime !== '' ||
        endTime !== '' ||
        status !== 'all'
    );

    if (hasFilters) {
        // 顯示搜尋結果表格
        $('#searchResultTable').show();
        $('#todayBookingsSection').hide();

        // 銷毀舊 DataTable
        if ($.fn.DataTable.isDataTable('#todaySearchTable')) {
            $('#todaySearchTable').DataTable().destroy();
        }
        $('#todaySearchTable tbody').removeClass('dataTable').removeAttr('style');
        $('#todaySearchTable').removeClass('dataTable');

        // 只篩選「今天」的 mockBookings
        const today = new Date().toISOString().split('T')[0];


        let filteredBookings = reservations.filter(b => b.reservationDate === today);

        if (searchName) {
            filteredBookings = filteredBookings.filter(b => b.memberName.includes(searchName));
        }
        if (searchPhone) {
            filteredBookings = filteredBookings.filter(b => b.phoneNumber.includes(searchPhone));
        }
        if (startTime) {
            filteredBookings = filteredBookings.filter(b => b.reservationTime >= startTime);
        }
        if (endTime) {
            filteredBookings = filteredBookings.filter(b => b.reservationTime <= endTime);
        }
        if (status !== 'all') {
            filteredBookings = filteredBookings.filter(b => String(b.reservationStatus) === status);
        }

        const tbody = $('#todaySearchTable tbody');
        tbody.empty();

        if (filteredBookings.length > 0) {
            filteredBookings.sort((a, b) => a.reservationTime.localeCompare(b.reservationTime));
            filteredBookings.forEach(booking => {
                const row = $('<tr>').attr('data-id', booking.reservationId);
                row.html(`
                        <td><span class="badge bg-info time-badge">${booking.reservationTime}</span></td>
                        <td>${booking.memberName}</td>
                        <td>${booking.phoneNumber}</td>
                        <td>${booking.guestCount}</td>
                        <td><span class="badge status-badge ${getStatusClass(booking.reservationStatus)}">${getStatusText(booking.reservationStatus)}</span></td>
                        <td>${getActionButtons(booking)}</td>
                    `);
                tbody.append(row);
            });
        }

        // 初始化搜尋結果 DataTable
        $('#todaySearchTable').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/zh-HANT.json',
                emptyTable: "無符合條件的訂位紀錄"
            },
            responsive: true,
            order: [[0, 'asc']],   // 時間排序
            columnDefs: [{ orderable: false, targets: 5 }],
            pageLength: 5,
            lengthMenu: [[5, 10, 25, -1], [5, 10, 25, '全部']],
            dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>rtip'
        });
    } else {
        // 顯示原始當日訂位
        $('#searchResultTable').hide();
        $('#todayBookingsSection').show();
        loadTodayBookings();
    }
}

// -----------------------------------
// 歷史訂位
// -----------------------------------

async function applyHistoryFilters() {
    const filters = {
        name: $('#historySearchName').val().trim(),
        phone: $('#historySearchPhone').val().trim(),
        startDate: $('#historyStartDate').val(),
        endDate: $('#historyEndDate').val(),
        startTime: $('#historyStartTime').val(),
        endTime: $('#historyEndTime').val(),
        status: $('#historyStatusFilter').val()
    };



    // 銷毀舊 DataTable
    if ($.fn.DataTable.isDataTable('#bookingTable')) {
        $('#bookingTable').DataTable().destroy();
    }
    $('#bookingTable tbody').removeClass('dataTable').removeAttr('style');
    $('#bookingTable').removeClass('dataTable');

    $('.selected-date-bookings').show();

    let filtered = reservations.slice();

    // 日期範圍
    if (filters.startDate) {
        filtered = filtered.filter(b => b.reservationDate >= filters.startDate);
    }
    if (filters.endDate) {
        filtered = filtered.filter(b => b.reservationDate <= filters.endDate);
    }
    // 時間範圍
    if (filters.startTime) {
        filtered = filtered.filter(b => b.reservationTime >= filters.startTime);
    }
    if (filters.endTime) {
        filtered = filtered.filter(b => b.reservationTime <= filters.endTime);
    }


    // 狀態篩選
    if (filters.status !== 'all') {
        filtered = filtered.filter(b => String(b.reservationStatus) === filters.status);
    }


    // 姓名 & 電話
    if (filters.name) {
        filtered = filtered.filter(b => b.memberName.includes(filters.name));
    }
    if (filters.phone) {
        filtered = filtered.filter(b => b.phoneNumber.includes(filters.phone));
    }

    // 排序 (先日期再時間)
    filtered.sort((a, b) => {
        if (a.reservationDate !== b.reservationDate) {
            return a.reservationDate.localeCompare(b.reservationDate);
        }
        return a.reservationTime.localeCompare(b.reservationTime);
    });


    const tbody = $('#bookingTable tbody');
    tbody.empty();

    if (filtered.length > 0) {
        filtered.forEach(b => {
            const row = $('<tr>').attr('data-id', b.reservationId);
            row.html(`
                    <td>${b.reservationDate}</td>
                    <td><span class="badge bg-info time-badge">${b.reservationTime}</span></td>
                    <td>${b.memberName}</td>
                    <td>${b.phoneNumber}</td>
                    <td>${b.guestCount}</td>
                    <td><span class="badge status-badge ${getStatusClass(b.reservationStatus)}">${getStatusText(b.reservationStatus)}</span></td>
                    <td>${getActionButtons(b)}</td>
                `);
            tbody.append(row);
        });
    }

    let titleText = '訂位列表';
    const hasAnyFilters = Object.values(filters).some(v => v && v !== 'all');
    if (hasAnyFilters) {
        const conditions = [];
        if (filters.startDate && filters.endDate) {
            conditions.push(`${filters.startDate} 至 ${filters.endDate}`);
        } else if (filters.startDate) {
            conditions.push(`${filters.startDate} 之後`);
        } else if (filters.endDate) {
            conditions.push(`${filters.endDate} 之前`);
        }
        if (filters.status !== 'all') {
            conditions.push(getStatusText(filters.status));
        }
        if (filters.name) {
            conditions.push(`姓名包含 "${filters.name}"`);
        }
        if (filters.phone) {
            conditions.push(`電話包含 "${filters.phone}"`);
        }
        if (conditions.length > 0) {
            titleText = `搜尋條件: ${conditions.join('、')}`;
        }
    }
    $('.selected-date-title').text(titleText);

    // 重新初始化 DataTable
    $('#bookingTable').DataTable({
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/zh-HANT.json',
            emptyTable: "無符合條件的訂位紀錄"
        },
        responsive: true,
        order: [[0, 'asc'], [1, 'asc']],
        columnDefs: [{ orderable: false, targets: 6 }],
        pageLength: 10,
        lengthMenu: [[5, 10, 25, 50, -1], [5, 10, 25, 50, '全部']],
        dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>rtip'
    });
}

// -----------------------------------
// 日曆 (使用 reservations)
// -----------------------------------
function initializeCalendar() {
    const calendarEl = document.getElementById('calendar');
    if (!calendarEl) return;

    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'zh-tw',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: ''
        },
        buttonText: { today: '今天' },
        dayMaxEvents: 3,
        eventContent: arg => ({
            html: `
                    <div class="fc-event-title">
                        <span class="dot" style="background-color: ${getStatusColor(arg.event.extendedProps.reservationStatus)}"></span>
                        ${arg.event.title}
                    </div>
                `
        }),
        events: (fetchInfo, successCallback) => {
            // 將 reservations 轉為 FullCalendar 事件
            const events = reservations.map(res => ({
                title: `${res.memberName} (${res.guestCount}人)`,
                start: `${res.reservationDate}T${res.reservationTime}`,
                extendedProps: res,
                color: getStatusColor(res.reservationStatus)
            }));
            successCallback(events);
        },
        dateClick: info => showSelectedDateBookings(info.date),
        datesSet: function(info) {
            // 檢查是否是通過今天按鈕觸發的
            const today = new Date();
            if (info.view.currentStart <= today && today <= info.view.currentEnd) {
                const todayStr = today.toISOString().split('T')[0];
                const currentDateStr = calendar.getDate().toISOString().split('T')[0];
                if (todayStr === currentDateStr) {
                    // 如果切換到今天，顯示今天的訂位記錄
                    showSelectedDateBookings(today);
                }
            }
        }
    });
    calendar.render();
}

function showSelectedDateBookings(date) {
    // 確保使用本地時間來處理日期
    const localDate = new Date(date.getTime() - (date.getTimezoneOffset() * 60000));
    const dateStr = localDate.toISOString().split('T')[0];
    console.log('dateStr:', dateStr);

    if ($('#bookingTable').length === 0) {
        console.log('#bookingTable 不存在於 DOM 中');
        return;
    }

    console.log("reservations："+ reservations)

    const bookings = reservations.filter(b => {
        let reservationDate = b.reservationDate;
        console.log("reservationDate："+ reservationDate)
        if (reservationDate.includes('-')) {
            const [year, month, day] = reservationDate.split('-');
            reservationDate = `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`;
        }
        return reservationDate === dateStr;
    });

    console.log('Found bookings:', bookings);
    // 銷毀現有的 DataTable（如果存在）
    if ($.fn.DataTable.isDataTable('#bookingTable')) {
        $('#bookingTable').DataTable().destroy();
        console.log('Existing DataTable destroyed');
    }

    console.log("Rendering bookings table");
    // 清空表格的 <tbody>，保留 <thead>
    const tbody = $('#bookingTable tbody');
    tbody.empty();

    if (bookings && bookings.length > 0) {
        console.log('Rendering bookings:', bookings.length);

        // 先排序
        bookings.sort((a, b) => a.reservationTime.localeCompare(b.reservationTime));

        // 逐筆渲染
        bookings.forEach(booking => {
            console.log('Rendering booking:', booking);

            try {
                const row = $('<tr>').attr('data-id', booking.reservationId);
                const rowHtml = `
                    <td>${booking.reservationDate || ''}</td>
                    <td><span class="badge bg-info time-badge">${booking.reservationTime || ''}</span></td>
                    <td>${booking.memberName || ''}</td>
                    <td>${booking.phoneNumber || ''}</td>
                    <td>${booking.guestCount || ''}</td>
                    <td><span class="badge status-badge ${getStatusClass(booking.reservationStatus)}">${getStatusText(booking.reservationStatus)}</span></td>
                    <td>${getActionButtons(booking)}</td>
                `;

                row.html(rowHtml);
                tbody.append(row);
                console.log('Row added successfully');
            } catch (error) {
                console.error('Error rendering row:', error);
            }
        });
    } else {
        console.log('No bookings found, showing empty message');
        tbody.html(`<tr><td colspan="7" class="text-center">該日無訂位紀錄</td></tr>`);
    }

    // 更新標題和顯示表格區域
    const formatted = new Date(dateStr).toLocaleDateString('zh-TW', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
    $('.selected-date-title').text(`${formatted} 訂位列表`);
    $('.selected-date-bookings').show();

    // 重新初始化 DataTable
    try {
        $('#bookingTable').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/zh-HANT.json',
                emptyTable: "無符合條件的訂位紀錄"
            },
            responsive: true,
            order: [[1, 'asc']], // 訂位時間由早到晚
            columnDefs: [{ orderable: false, targets: 6 }],
            pageLength: 10,
            lengthMenu: [[5, 10, 25, 50, -1], [5, 10, 25, 50, '全部']],
            dom: '<"row"<"col-sm-12 col-md-6"l><"col-sm-12 col-md-6"f>>rtip',
            destroy: true // 確保可以重新初始化
        });

        console.log('DataTable initialized successfully');
    } catch (error) {
        console.error('Error initializing DataTable:', error);
    }

    // 滾動到表格位置
    $('html, body').animate({
        scrollTop: $('.selected-date-bookings').offset().top
    }, 500);
}

// -----------------------------------
// 訂位操作相關函式
// -----------------------------------

async function reloadReservationsAndRefreshTables(){
    try{
        reservations = await getReservations();
        // 根據當前顯示的頁籤來決定要重新載入哪個表格
        if($('#today').hasClass('active')){
            console.log('Reloading Today Bookings');
            loadTodayBookings();
        }

        if($('#history').hasClass('active')){
            console.log('Reloading History Bookings');
            refreshHistoryBookings();
        }

        if(calendar){
            calendar.refetchEvents();
        }
        if (selectedHistoryDate) {
            console.log('Selected history date:', selectedHistoryDate);
            const date = new Date(selectedHistoryDate);
            showSelectedDateBookings(date);
        }
    }catch(error){
        console.log(error);
        return [];
    }
}


async function updateBookingStatus(id,operation) {
    let operationObj = {"operation":operation}
    try{
        const res = await fetch(`/rests/reservations/reservation/${id}/reservationStatus`,{
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(operationObj)
        });
        if(!res.ok){
            errorData = await res.json();
            throw new Error(JSON.stringify(errorData));
        }
        let updatedReservation = await res.json();
        return updatedReservation;
    }catch(error){
        console.log(error);
        return [];
    }
}


async function checkIn(id) {
    if (confirm('確認顧客報到？')) {
        if (id !== -1) {
            const updatedReservation = await updateBookingStatus(id, 'checkIn');
            if (updatedReservation) {
                showOperationRecord('checkIn', updatedReservation);

                // 設置 selectedHistoryDate 為被操作的訂位日期
                selectedHistoryDate = updatedReservation.reservationDate;
                console.log("selectedHistoryDate: " + selectedHistoryDate);
                reloadReservationsAndRefreshTables();
            }
        }
    }
}

async function cancel(id) {
    if (confirm('確認取消此筆訂位？')) {
        if (id !== -1) {
            const updatedReservation = await updateBookingStatus(id, 'cancel');
            if (updatedReservation) {
                showOperationRecord('cancel', updatedReservation);

                // 設置 selectedHistoryDate 為被操作的訂位日期
                selectedHistoryDate = updatedReservation.reservationDate;
                console.log("selectedHistoryDate: " + selectedHistoryDate);

                reloadReservationsAndRefreshTables();
            }
        }
    }
}

async function changeStatus(id) {
    if (confirm('確認恢復訂位？')) {
        if (id !== -1) {
            const updatedReservation = await updateBookingStatus(id, 'restore');
            if (updatedReservation) {
                showOperationRecord('restore', updatedReservation);

                // 設置 selectedHistoryDate 為被操作的訂位日期
                selectedHistoryDate = updatedReservation.reservationDate;
                console.log("selectedHistoryDate: " + selectedHistoryDate);
                reloadReservationsAndRefreshTables();
            }
        }
    }
}

// -----------------------------------
// 訂位操作相關函式
// -----------------------------------

function getActionButtons(b) {
    if (String(b.reservationStatus) === '1') {
        return `
                <button class="btn btn-sm btn-success" onclick="checkIn(${b.reservationId})">報到</button>
                <button class="btn btn-sm btn-danger" onclick="cancel(${b.reservationId})">取消</button>
            `;
    } else if (String(b.reservationStatus) === '0') {
        return `
                <button class="btn btn-sm btn-success text-white" onclick="changeStatus(${b.reservationId})">恢復訂位</button>
            `;
    }
    return '';
}

function getStatusClass(status) {
    const map = { '1': 'bg-warning', '2': 'bg-success', '0': 'bg-danger' };
    return map[status] || 'bg-secondary';
}

function getStatusText(status) {
    const map = { '1': '待報到', '2': '已報到', '0': '已取消' };
    return map[status] || '未知';
}

function getStatusColor(status) {
    const map = { '1': '#ffc107', '2': '#198754', '0': '#dc3545' };
    return map[status] || '#6c757d';
}

function clearAllFilters() {
    $('#operationRecord, #historyOperationRecord').addClass('d-none');
    $('input[type="text"]').val('');
    $('input[type="time"]').val('');
    $('input[type="date"]').val('');
    $('.form-select').val('all');

    if ($('#today').hasClass('show')) {
        returnToTodayBookings();
    } else if ($('#history').hasClass('show')) {
        applyHistoryFilters();
    }
}

function showOperationRecord(operation, reservation) {
    const statusText = {
        'checkIn': '報到',
        'cancel': '取消',
        'restore': '恢復訂位'
    };

    // 格式化日期顯示
    const formattedDate = new Date(reservation.reservationDate).toLocaleDateString('zh-TW', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });

    const record = `[操作紀錄] ${formattedDate} ${reservation.reservationTime}
                        ${reservation.memberName}（${reservation.guestCount}人）的訂位已${statusText[operation]}`;

    // 根據當前顯示的頁面決定要更新哪個記錄區域
    if ($('#today').hasClass('show')) {
        $('#operationRecord')
            .removeClass('d-none')
            .find('.record-content')
            .text(record);
    } else if ($('#history').hasClass('show')) {
        $('#historyOperationRecord')
            .removeClass('d-none')
            .find('.record-content')
            .text(record);
    }
}

// -----------------------------------
// 初始化
// -----------------------------------
async function initializePage() {
    // 從後端抓歷史訂位 (reservations)
    reservations = await getReservations();

    const today = new Date().toISOString().split('T')[0];
    $('#startDate, #endDate').val(today);
    $('#startTime').val('00:00');
    $('#endTime').val('23:59');

    // 載入「當日訂位」(mockBookings)
    loadTodayBookings();

    // 若預設顯示「歷史訂位」Tab，初始化日曆
    if ($('#history').hasClass('active')) {
        initializeCalendar();
    }

    // 工具提示
    $('[data-bs-toggle="tooltip"]').tooltip();
    $('input[type="date"]').val(today);
}

// -----------------------------------
// DOM Ready
// -----------------------------------
$(document).ready(function() {

    $(document).on('error.dt', function(e, settings, techNote, message) {
        console.warn('DataTable 發生錯誤：', message); // 可將錯誤訊息記錄到 console 或忽略
    });


    initializePage();


    // 日期 / 時間篩選器
    $('.search-container input, .search-container select').on('change', function() {
        let sd = $('#startDate').val();
        let ed = $('#endDate').val();
        let st = $('#startTime').val();
        let et = $('#endTime').val();

        const isTodayTab = $('#today').hasClass('active');
        const isHistoryTab = $('#history').hasClass('active');

        if(isTodayTab){
            // 確保結束日期不小於開始日期
            if (ed < sd) $('#endDate').val(sd);
            // 當起訖日期相同，確保結束時間不小於開始時間
            if (sd === ed && et < st) {
                $('#endTime').val(st);
            }
        }

        if(isHistoryTab){
            sd = $('#historyStartDate').val();
            ed = $('#historyEndDate').val();
            st = $('#historyStartTime').val();
            et = $('#historyEndTime').val();

            // 確保結束日期不小於開始日期
            if (ed < sd) $('#historyEndDate').val(sd);
            // 當起訖日期相同，確保結束時間不小於開始時間
            if (sd === ed && et < st) {
                $('#historyEndTime').val(st);
            }

        }


    });

    // 清空搜尋欄
    $('.clear-search').on('click', function() {
        const input = $(this).closest('.input-group').find('input');
        input.val('');
        if ($('#today').hasClass('show')) {
            $('#searchResultTable').hide();
            $('#todayBookingsSection').show();
            loadTodayBookings();
        }
    });

    // Tab 切換
    $('a[data-bs-toggle="tab"]').on('shown.bs.tab', function(e) {
        if ($(e.target).attr('href') === '#today') {
            loadTodayBookings();
        } else if ($(e.target).attr('href') === '#history') {
            if (calendar) {
                calendar.render();
            } else {
                initializeCalendar();
            }
        }
    });

    // 自定義 FullCalendar 事件樣式
    const eventStyles = `
            .fc-event-title .dot {
                display: inline-block;
                width: 8px;
                height: 8px;
                border-radius: 50%;
                margin-right: 4px;
            }
            .fc-event {
                background: white !important;
                border: 1px solid #ddd !important;
                color: #333 !important;
                padding: 2px 4px;
                margin-bottom: 2px;
            }
        `;
    const styleSheet = document.createElement("style");
    styleSheet.innerText = eventStyles;
    document.head.appendChild(styleSheet);

    // 在搜尋區域添加「清空所有條件」按鈕
    $('.search-container').each(function() {
        const btn = $(`
                <div class="text-end mt-2">
                    <button class="btn btn-outline-secondary btn-sm" onclick="clearAllFilters()">
                        <i class="bi bi-trash"></i> 清空所有條件
                    </button>
                </div>
            `);
        $(this).append(btn);
    });
});