
let reservations = [];

async function getReservations(){
    try{
        const url = `/member/reservations`;
        const res = await fetch(url);
        if(!res.ok){
            errorData = await res.json();
            throw new Error(JSON.stringify(errorData));
        }
        let reservations = await res.json();
        return reservations;
    }catch(error){
        console.error(error);
        return [];
    }
}

async function updateBookingStatus(id,operation) {
    let operationObj = {"operation":operation}
    try{
        const res = await fetch(`/member/reservations/reservation/${id}/reservationStatus`,{
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

async function cancel(id){
    if (confirm('是否確認取消訂位？')) {
        if(id!==-1){
            await updateBookingStatus(id,'cancel');
            reloadReservationAndRefreshTables();
        }
    }
}

async function reloadReservationAndRefreshTables(){
    try{
        reservations = await getReservations();
        renderCustomerTables();
    }catch(error){
        console.log(error);
        return [];
    }
}

async function initializePage(){
    reservations = await getReservations();
    renderCustomerTables();

    $('#search-general, #search-date, #search-status').on('input change', applyFilters);



    $('.sortable').on('click', function () {
        const column = $(this).data('column');
        const order = $(this).data('order');
        sortOrder = { column, order };
        $(this).data('order', order === 'asc' ? 'desc' : 'asc');
        renderCustomerTables();
    });
}



let sortOrder = { column: 'id', order: 'asc' };

function getStatusClass(status) {
    switch (status) {
        case '1': return 'status-pending';
        case '2': return 'status-completed';
        case '0': return 'status-cancelled';
        default: return '';
    }
}

function getStatusText(status) {
    switch (status) {
        case '1': return '尚未用餐';
        case '2': return '已完成';
        case '0': return '已取消';
        default: return '';
    }
}

function sortReservations(reservations, column, order) {
    return reservations.sort((a, b) => {
        const valueA = a[column];
        const valueB = b[column];

        if (typeof valueA === 'string') {
            return order === 'asc' ? valueA.localeCompare(valueB) : valueB.localeCompare(valueA);
        } else {
            return order === 'asc' ? valueA - valueB : valueB - valueA;
        }
    });
}

function renderCustomerTables(filteredReservations = reservations) {
    const sortedReservations = sortReservations(filteredReservations, sortOrder.column, sortOrder.order);
    const upcomingTbody = $('#upcoming-reservations');
    const completedTbody = $('#completed-reservations');
    upcomingTbody.empty();
    completedTbody.empty();

    sortedReservations.forEach(reservation => {
        const cancelButton = String(reservation.reservationStatus) === '1'
            ? `<button class="btn btn-danger btn-sm cancel-btn" data-id="${reservation.reservationId}" onclick="cancel(${reservation.reservationId})">取消</button>`
            : '';

        const row = `
                    <tr>
                        <td>${reservation.restName}</td>
                        <td>${reservation.reservationDate}</td>
                        <td>${reservation.reservationTime}</td>
                        <td>${reservation.guestCount}</td>
                        <td><span class="status-badge ${getStatusClass(String(reservation.reservationStatus))}">${getStatusText(String(reservation.reservationStatus))}</span></td>
                        <td>${cancelButton}</td>
                    </tr>
                `;

        if (String(reservation.reservationStatus) === '2'||String(reservation.reservationStatus) === '0') {
            completedTbody.append(row);
        } else {
            upcomingTbody.append(row);
        }
    });
}

function applyFilters() {
    const searchGeneral = $('#search-general').val().toLowerCase();
    const searchDate = $('#search-date').val();
    const searchStatus = $('#search-status').val();

    const filteredReservations = reservations.filter(reservation => {
        const matchesGeneral = reservation.restName.toLowerCase().includes(searchGeneral) ||
            reservation.reservationDate.includes(searchGeneral) ||
            reservation.reservationTime.includes(searchGeneral);

        const reservationDateFormatted = reservation.reservationDate.replace(/\//g, '-');
        const matchesDate = searchDate ? reservationDateFormatted === searchDate : true;

        const matchesStatus = searchStatus ? String(reservation.reservationStatus) === searchStatus : true;

        return matchesGeneral && matchesDate && matchesStatus;
    });

    renderCustomerTables(filteredReservations);
}

$(document).ready(async function () {
    initializePage();
});