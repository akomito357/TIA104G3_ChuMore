let citiesData = [];
let cuisineTypesData = [];
let currentRests = [];

$(document).ready(async function() {
    await initializePage();
});

/**
 * ==========================================
 *               初始化頁面
 * ==========================================
 */
async function initializePage() {
    // 載入縣市清單 & 料理類型
    await loadCities();
    await loadCuisineTypes();

    // 渲染縣市、料理類型 checkbox
    renderCityOptions(citiesData);
    renderCuisineTypeOptions(cuisineTypesData);

    // 綁定事件
    bindInitialEvents();

    // 初始化頂部搜尋欄 (日期、時段、人數) 的選項
    initializeTopSearchBar();

    // 檢查 URL Query 參數，如有則進行「頂部搜尋」的初始查詢
    await initialSearchByUrlParams();
}

/**
 * ==========================================
 *         綁定事件 & 初始化邏輯
 * ==========================================
 */
function bindInitialEvents() {
    // 綁定「縣市勾選」事件
    $(document).on('change', '.city-checkbox', function() {
        updateDistrictOptions();
    });

    // 綁定「送出查詢」按鈕事件（側邊欄搜尋）
    $('#submitFilter').click(function() {
        handleSideBarSearch();
    });

    // 綁定「頂部搜尋」按鈕 & Enter 鍵事件
    $('#topSearchBtn').click(handleTopSearch);
    $('#topSearchKeyword').keypress(function(e) {
        if (e.which === 13) {
            handleTopSearch();
        }
    });

    // 綁定「排序」下拉選單事件
    $('#sortSelect').change(function() {
        const sortType = $(this).val();
        if (!currentRests || currentRests.length <= 1) {
            return;
        }
        let sorted = [...currentRests];

        switch (sortType) {
            case "rating":
                sorted.sort((a, b) => (b.restStars || 0) - (a.restStars || 0));
                break;
            case "review":
                sorted.sort((a, b) => (b.restReviewers || 0) - (a.restReviewers || 0));
                break;
            case "price_high":
                sorted.sort((a, b) => (b.avgCost || 0) - (a.avgCost || 0));
                break;
            case "price_low":
                sorted.sort((a, b) => (a.avgCost || 0) - (b.avgCost || 0));
                break;
            default:
                break;
        }
        renderRests(sorted);
    });

    // 綁定「頂部縣市下拉」事件，切換縣市時自動勾選左側縣市
    $('#topCitySelect').change(function() {
        const selectedCity = $(this).val();
        $('.city-checkbox').prop('checked', false);
        if (selectedCity) {
            $('.city-checkbox').each(function() {
                if ($(this).val() === selectedCity) {
                    $(this).prop('checked', true);
                }
            });
            updateDistrictOptions();
        }
    });
}

// 初始化「頂部搜尋欄」的下拉選單
function initializeTopSearchBar() {
    // 預設日期
    const today = new Date().toISOString().split('T')[0];
    $('#topDateSelect').val(today);

    // 時間下拉
    const timeSelect = $('#topTimeSelect');
    const timeSlots = [];
    Array.from({length: 24}, (_, i) => i).forEach(hour => { // 包含 00:00
        const formattedHour = hour.toString().padStart(2, '0');
        timeSlots.push(`${formattedHour}:00`);
    });
    timeSlots.forEach(time => {
        timeSelect.append(`<option value="${time}">${time}</option>`);
    });

    // 人數下拉
    const guestCountSelect = $('#topGuestCountSelect');
    for (let i = 1; i <= 10; i++) {
        guestCountSelect.append(`<option value="${i}">${i}位</option>`);
    }
    guestCountSelect.append('<option value="more">10位以上</option>');

    // 關鍵字
    $('#topSearchKeyword').val("");
}

// 進入頁面時，自動判斷 URL 上是否有參數；如有，則自動執行搜尋
async function initialSearchByUrlParams() {
    try {
        const params = new URLSearchParams(window.location.search);
        const keyword = params.get('keyword');
        const reservedDate = params.get('reservedDate');
        const reservedTime = params.get('reservedTime');
        const guestCount = parseInt(params.get('guestCount'), 10);

        // 檢查是否有任何參數存在
        if (!keyword && !reservedDate && !reservedTime && !guestCount) {
            // 沒有任何參數就不進行搜尋
            return;
        }

        // 將參數帶入「頂部搜尋」欄位
        if (keyword) $('#topSearchKeyword').val(keyword);
        if (reservedDate) $('#topDateSelect').val(reservedDate.toString());
        if (reservedTime) $('#topTimeSelect').val(reservedTime);
        if (guestCount && !isNaN(guestCount)) $('#topGuestCountSelect').val(guestCount);

        // 執行搜尋
        await handleTopSearch();
    } catch (error) {
        console.error("URL 參數處理時發生錯誤：", error);
    }
}

/**
 * ==========================================
 *          搜尋相關核心邏輯
 * ==========================================
 */
// 頂部搜尋
async function handleTopSearch() {
    const topSearchData = {
        keyword: $('#topSearchKeyword').val(),
        city: $('#topCitySelect').val(),
        date: $('#topDateSelect').val(),
        time: $('#topTimeSelect').val(),
        guestCount: parseInt($('#topGuestCountSelect').val(), 10)
    };

    // 顯示載入的spinner
    $('#restResults').empty();
    $('#loadingSpinner').removeClass('d-none');

    try {
        // 取得符合搜尋條件的餐廳 ID
        const restIds = await searchingResults(
            topSearchData.keyword,
            topSearchData.date,
            topSearchData.time,
            topSearchData.guestCount
        );

        if (restIds.length === 0) {
            $('#loadingSpinner').addClass('d-none');
            $('#restResults').append(`
                    <div class="col-12 text-center">
                        <p class="text-muted">沒有找到符合條件的餐廳</p>
                    </div>
                `);
            // 呼叫 renderRests 來設置 searchCount
            renderRests([]);
            return;
        }

        // 2. 取得詳細資料並封裝
        let finalRests = await restWrapper(
            restIds,
            topSearchData.date,
            topSearchData.time,
            topSearchData.guestCount
        );

        // 3. 渲染
        renderRests(finalRests);
        // 不再在此處設置 searchCount，讓 renderRests 處理

    } catch (error) {
        console.error("發生錯誤：", error);
        $('#restResults').empty();
        $('#restResults').append(`
                <div class="col-12 text-center">
                    <p class="text-danger">發生錯誤，請稍後再試。</p>
                </div>
            `);
        // 呼叫 renderRests 來設置 searchCount
        renderRests([]);
    } finally {
        $('#loadingSpinner').addClass('d-none');
    }
}

// 側邊欄搜尋
async function handleSideBarSearch() {
    // 1. 取得所有勾選的篩選條件
    const selectedCities = $('.city-checkbox:checked').map(function() {
        return $(this).val();
    }).get();

    const selectedDistricts = $('.district-checkbox:checked').map(function() {
        return $(this).val();
    }).get();

    const selectedCuisineTypes = $('.cuisine-type-checkbox:checked').map(function() {
        return $(this).val();
    }).get();

    const reservedDate = $('#topDateSelect').val();
    const reservedTime = $('#topTimeSelect').val();
    const guestCount = parseInt($('#topGuestCountSelect').val(), 10);

    const criteria = {
        cities: selectedCities,
        districts: selectedDistricts,
        cuisineTypeIds: selectedCuisineTypes,
    };

    // 顯示載入
    $('#restResults').empty();
    $('#loadingSpinner').removeClass('d-none');

    try {
        // 先拿到符合篩選條件的餐廳 ID
        let restIds = await getAvailableRestsByOptionalFields(criteria, reservedDate, reservedTime, guestCount);

        if (!Array.isArray(restIds) || restIds.length === 0) {
            $('#loadingSpinner').addClass('d-none');
            $('#restResults').append(`
                    <div class="col-12 text-center">
                        <p class="text-muted">沒有找到符合條件的餐廳</p>
                    </div>`);
            // 呼叫 renderRests 來設置 searchCount
            renderRests([]);
            return;
        }

        // 取得餐廳資料並封裝
        let finalRests = await restWrapper(
            restIds,
            reservedDate,
            reservedTime,
            guestCount
        );

        // 渲染
        renderRests(finalRests);
        // 不再在此處設置 searchCount，讓 renderRests 處理
    } catch (error) {
        console.error("發生錯誤：", error);
        $('#restResults').empty();
        $('#restResults').append(`
                <div class="col-12 text-center">
                    <p class="text-danger">發生錯誤，請稍後再試。</p>
                </div>`);
        // 呼叫 renderRests 來設置 searchCount
        renderRests([]);
    } finally {
        $('#loadingSpinner').addClass('d-none');
    }
}

/**
 * ==========================================
 *           AJAX / API 呼叫區塊
 * ==========================================
 */
// 取得符合搜尋條件的 restId 列表
async function searchingResults(keyword = "", reservedDate, reservedTime, guestCount) {
    try {
        let url = `/search?keyword=${encodeURIComponent(keyword)}&reservedDate=${reservedDate}&reservedTime=${reservedTime}:00&guestCount=${guestCount}`;
        let res = await fetch(url);
        if (!res.ok) {
            if (res.status === 404) {
                let errorData = await res.json();
                throw new Error(JSON.stringify(errorData));
            }
        }
        let restIds = await res.json();
        return restIds;
    } catch (error) {
        console.log(error.message);
        return [];
    }
}

// 透過 restId 列表搜尋餐廳資料
async function getRestsByRestIds(restIds) {
    try {
        let url = `/restaurants`;
        let res = await fetch(url, {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(restIds)
        });

        if (!res.ok) {
            if (res.status === 404) {
                return {
                    "code": 404,
                    "status": "Not Found",
                    "message": "沒有符合條件的餐廳"
                };
            }
        }
        let rests = await res.json();
        return rests;
    } catch (error) {
        console.log(error);
        return [];
    }
}

// 取得可用的桌種列表
async function getAvailableTablesListByRests(restIds, reservedDate, guestCount) {
    try {
        let url = `/dailyReservations/availableTablesList?reservedDate=${reservedDate}&guestCount=${guestCount}`;
        let res = await fetch(url, {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(restIds)
        });
        if (!res.ok) {
            if (res.status === 404) {
                let errorData = await res.json();
                throw new Error(JSON.stringify(errorData));
            }
        }
        let availableTablesList = await res.json();
        return availableTablesList;
    } catch (error) {
        console.log(error.message);
        return [];
    }
}

// 取得 ±2 小時的時段座位數
async function getNearbyTimeSlot(restIds, reservedDate, reservedTime, guestCount) {
    let availableTablesList = await getAvailableTablesListByRests(restIds, reservedDate, guestCount);
    let result = {};

    for (let restId in availableTablesList) {
        if (availableTablesList.hasOwnProperty(restId)) {
            let tables = availableTablesList[restId];
            let startHour = Math.max(Number(reservedTime.substring(0, 2)) - 2, 0);
            let endHour = Math.min(Number(reservedTime.substring(0, 2)) + 2, 23);

            result[restId] = {}; // 初始化餐廳的結果

            for (let hour = startHour; hour <= endHour; hour++) {
                let timeSlot = `${hour.toString().padStart(2, '0')}:00`;
                result[restId][timeSlot] = tables[hour] || 0; // 若無資料則預設為0
            }
        }
    }
    return result;
}

// 側邊欄專用：取得篩選欄位符合的 restId
async function getRestIdsByOptionalFields(criteria) {
    let url = `/restaurants/search?`;
    const params = new URLSearchParams();

    if (criteria.cities && criteria.cities.length > 0) {
        criteria.cities.forEach(c => params.append('city', c));
    }
    if (criteria.districts && criteria.districts.length > 0) {
        criteria.districts.forEach(d => params.append('district', d));
    }
    if (criteria.cuisineTypeIds && criteria.cuisineTypeIds.length > 0) {
        criteria.cuisineTypeIds.forEach(ct => params.append('cuisineTypeId', ct));
    }

    try {
        url += params.toString();
        let res = await fetch(url);
        if (!res.ok) {
            if (res.status === 404) {
                let errorData = await res.json();
                throw new Error(JSON.stringify(errorData));
            }
        }
        let restIds = await res.json();
        return restIds;
    } catch (error) {
        console.log(error);
        return [];
    }
}

// 側邊欄專用：再根據訂位時段進行篩選
async function getFilteredRestIdsByConditions(restIds, reservedDate, reservedTime, guestCount) {
    try {
        let url = `/dailyReservations/filteredRestIds?reservedDate=${reservedDate}&reservedTime=${reservedTime}&guestCount=${guestCount}`;
        let res = await fetch(url, {
            method: "POST",
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(restIds)
        });

        if (!res.ok) {
            if (res.status === 404) {
                let errorData = await res.json();
                throw new Error(JSON.stringify(errorData));
            }
        }
        let filteredRestIds = await res.json();
        return filteredRestIds;
    } catch (error) {
        console.log(error.message);
        return [];
    }
}

// 側邊欄最終符合條件的 restId
async function getAvailableRestsByOptionalFields(criteria, reservedDate, reservedTime, guestCount) {
    let restIds = await getRestIdsByOptionalFields(criteria);
    let availableRests = await getFilteredRestIdsByConditions(restIds, reservedDate, reservedTime, guestCount);

    if (!Array.isArray(availableRests) || availableRests.length === 0) {
        return [];
    }
    return availableRests;
}

// API：平均星數
async function getAvgStars(restId) {
    try {
        let url = `/reviews/averageRating/${restId}`;
        let res = await fetch(url);
        if (!res.ok) {
            let errorData = await res.json();
            throw new Error(JSON.stringify(errorData));
        }
        let avgStars = await res.json();
        return avgStars;
    } catch (error) {
        console.error('載入平均評分失敗：', error);
        return 0;
    }
}

// API：平均消費
async function getAvgCost(restId) {
    try {
        let url = `/reviews/averageCost/${restId}`;
        let res = await fetch(url);
        if (!res.ok) {
            let errorData = await res.json();
            throw new Error(JSON.stringify(errorData));
        }
        let avgCost = await res.json();

        // 四捨五入到百元
        const lastTwoDigit = avgCost % 100;
        if (lastTwoDigit < 50) {
            return Math.floor(avgCost / 100) * 100;
        } else {
            return Math.ceil(avgCost / 100) * 100;
        }
    } catch (error) {
        console.error('載入平均價位失敗：', error);
        return 0;
    }
}

// API：評論數
async function getReviewCounts(restId) {
    try {
        let url = `/reviews/reviewCounts/${restId}`;
        let res = await fetch(url);
        if (!res.ok) {
            let errorData = await res.json();
            throw new Error(JSON.stringify(errorData));
        }
        let reviewCounts = await res.json();
        return reviewCounts;
    } catch (error) {
        console.error('載入評論數失敗：', error);
        return 0;
    }
}

// 封裝餐廳資料
async function restWrapper(restIds, reservedDate, reservedTime, guestCount) {
    let availableRests = await getRestsByRestIds(restIds);
    if (!Array.isArray(availableRests) || availableRests.length === 0) {
        return [];
    }

    let timeSlot = await getNearbyTimeSlot(restIds, reservedDate, reservedTime, guestCount);
    let results = [];

    for (const rest of availableRests) {
        const restImage = (rest.envImgs && rest.envImgs[0] && rest.envImgs[0].image)
            ? `data:image/jpeg;base64,${rest.envImgs[0].image}`
            : `https://placehold.co/400x200?text=Restaurant ${rest.restId}`;

        const filteredData = {
            restId: rest.restId,
            restName: rest.restName,
            restCity: rest.restCity,
            restDist: rest.restDist,
            restAddress: rest.restCity + rest.restDist + rest.restAddress,
            cuisineTypeName: rest.cuisineTypeName,
            restStars: await getAvgStars(rest.restId),
            restReviewers: await getReviewCounts(rest.restId),
            avgCost: await getAvgCost(rest.restId),
            availableTimeSlot: timeSlot[rest.restId],
            restImage: restImage,
            isFavorite: false
        };
        results.push(filteredData);
    }

    return results;
}

/**
 * ==========================================
 *         前端渲染 / 綁定事件
 * ==========================================
 */
// 星星評分
function renderStars(rating) {
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 >= 0.5;
    const emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);

    let starHtml = '';
    for (let i = 0; i < fullStars; i++) {
        starHtml += '<span class="rating-stars">★</span>';
    }
    if (hasHalfStar) {
        starHtml += '<span class="rating-stars half">★</span>';
    }
    for (let i = 0; i < emptyStars; i++) {
        starHtml += '<span class="rating-stars empty">★</span>';
    }
    return starHtml;
}

// 渲染餐廳卡片
function renderRests(restList) {
    // 確保 restList 是一個陣列
    if (!Array.isArray(restList)) {
        restList = [];
    }

    currentRests = restList.slice();
    const container = $('#restResults');
    container.empty();

    if (restList.length === 0) {
        container.append(`
                <div class="col-12 text-center">
                    <p class="text-muted">沒有找到符合條件的餐廳</p>
                </div>
            `);
        $('#searchCount').text(`共 0 間餐廳符合搜尋條件`);
        return;
    }

    restList.forEach(rest => {
        const stars = renderStars(rest.restStars);
        const timeButtons = Object.keys(rest.availableTimeSlot)
            .map(time => {
                const slotCount = rest.availableTimeSlot[time];
                let buttonClass = "btn booking-time-btn";
                let buttonContent = time;

                if (slotCount === 0) {
                    // 已滿的狀態
                    buttonClass += " btn-disabled"; // 禁用按鈕
                } else if (slotCount < 3) {
                    // 剩餘小於 3 個的狀態
                    buttonClass += " btn-outline-primary";
                    buttonContent += ` (剩餘${slotCount})`;
                } else {
                    // 充足的時段
                    buttonClass += " btn-primary";
                }

                return `<button class="${buttonClass}" data-rest-id="${rest.restId}" data-time="${time}" ${slotCount === 0 ? "disabled" : ""}>${buttonContent}</button>`;
            })
            .join('');

        const card = `
                <div class="col-12 mb-4" data-rest-id="${rest.restId}">
                    <div class="card rest-card">
                        <div class="row g-0">
                            <div class="col-md-4">
                                <a href="/restaurant_details.html?id=${rest.restId}" class="rest-image-link">
                                    <img src="${rest.restImage}" class="img-fluid h-100 w-100" alt="${rest.restName}" style="object-fit: cover;">
                                </a>
                            </div>
                            <div class="col-md-8">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <h5 class="card-title mb-0">
                                            <a href="/my_page/restaurant_details.html?id=${rest.restId}" class="rest-title">${rest.restName}</a>
                                        </h5>
                                        <i class="fas fa-heart favorite-btn ${rest.isFavorite ? 'active' : ''}"
                                           data-rest-id="${rest.restId}"></i>
                                    </div>
                                    <div class="mb-2">
                                        ${stars}
                                        <span class="text-muted">(${rest.restReviewers}則評論)</span>
                                    </div>
                                    <p class="card-text">
                                        <small class="text-muted">
                                            <i class="fas fa-map-marker-alt"></i> ${rest.restAddress}<br>
                                            <i class="fas fa-dollar-sign"></i> ${rest.avgCost}元/人<br>
                                            <i class="fas fa-utensils"></i> ${rest.cuisineTypeName}
                                        </small>
                                    </p>
                                    <div class="d-flex gap-2">
                                        ${timeButtons}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        container.append(card);
    });

    $('#searchCount').text(`共 ${restList.length} 間餐廳符合搜尋條件`);

    // 再次綁定「收藏」與「訂位時段」事件
    bindEvents();
}

// 收藏 & 訂位按鈕事件
function bindEvents() {
    // 收藏按鈕事件
    $('.favorite-btn').off('click').on('click', function() {
        const restId = $(this).data('rest-id');
        $(this).toggleClass('active');
        console.log(`切換餐廳 ${restId} 的收藏狀態`);
    });

    // 訂位時段按鈕事件
    $('.booking-time-btn').off('click').on('click', function() {
        const restId = $(this).data('rest-id');
        const time = $(this).data('time');

        const reservationDate = $('#topDateSelect').val();
        const guestCount = $('#topGuestCountSelect').val();

        const url = `/reservations/reservation?restId=${restId}&reservationDate=${reservationDate}&reservationTime=${time}&guestCount=${guestCount}`;
        window.location.href = url;
    });
}

/**
 * ==========================================
 *           載入城市 & 料理類型
 * ==========================================
 */
async function loadCities() {
    try {
        let url = `/location/findCitys`;
        let res = await fetch(url);
        let cities = await res.json();
        citiesData = cities.data;
    } catch (error) {
        console.log(error);
        return [];
    }
}

async function loadCuisineTypes() {
    try {
        let url = `/cuisineTypes/getAllCuisineType`;
        let res = await fetch(url);
        if (!res.ok) {
            let errorData = await res.json();
            throw new Error(JSON.stringify(errorData));
        }
        let data = await res.json();
        let cuisineTypes = [];

        data.forEach(cuisineType => {
            let filteredData = {
                cuisineTypeId: cuisineType.cuisineTypeId,
                cuisineTypeName: cuisineType.cuisineDescr
            };
            cuisineTypes.push(filteredData);
        });
        cuisineTypesData = cuisineTypes;
    } catch (error) {
        console.error('載入料理類型失敗：', error);
        return [];
    }
}

function renderCityOptions(cities) {
    const cityContainer = $('.city-options');
    cityContainer.empty();
    let counter = 1;
    cities.forEach(city => {
        counter++;
        const checkbox = `
                <div class="form-check">
                    <input class="form-check-input city-checkbox"
                           type="checkbox"
                           value="${city}"
                           id="cityCheckbox_${counter}">
                    <label class="form-check-label" for="cityCheckbox_${counter}">
                        ${city}
                    </label>
                </div>
            `;
        cityContainer.append(checkbox);
    });
}

function renderCuisineTypeOptions(cuisineTypes) {
    const cuisineTypeContainer = $('.cuisine-type-options');
    cuisineTypeContainer.empty();
    cuisineTypes.forEach(cuisineType => {
        const checkbox = `
                <div class="form-check">
                    <input class="form-check-input cuisine-type-checkbox"
                           type="checkbox"
                           value="${cuisineType.cuisineTypeId}"
                           id="cuisineTypeCheckbox_${cuisineType.cuisineTypeId}">
                    <label class="form-check-label" for="cuisineTypeCheckbox_${cuisineType.cuisineTypeId}">
                        ${cuisineType.cuisineTypeName}
                    </label>
                </div>`;
        cuisineTypeContainer.append(checkbox);
    });
}

// 更新鄉鎮市區選單
async function updateDistrictOptions() {
    const districtContainer = $('.district-options');
    districtContainer.empty();

    const selectedCities = $('.city-checkbox:checked').map(function () {
        return $(this).val();
    }).get();

    if (selectedCities.length === 0) {
        districtContainer.append('<div class="text-muted">請先選擇縣市</div>');
        return;
    }

    try {
        // 同時發送多個請求取得所有選擇縣市的鄉鎮市區
        const fetchPromises = selectedCities.map(async (city) => {
            try {
                const response = await fetch(`/location/findDistByCity?city=${encodeURIComponent(city)}`);
                if (!response.ok) {
                    console.error(`載入 ${city} 的鄉鎮市區失敗：HTTP ${response.status}`);
                    return { city: city, districts: [] };
                }
                const result = await response.json();
                if (result.resCode === 200) {
                    return { city: city, districts: result.data };
                } else {
                    console.error(`載入 ${city} 的鄉鎮市區失敗：`, result.resMsg);
                    return { city: city, districts: [] };
                }
            } catch (error) {
                console.error(`載入 ${city} 的鄉鎮市區失敗：`, error);
                return { city: city, districts: [] };
            }
        });

        const results = await Promise.all(fetchPromises);

        // 處理載入的資料
        results.forEach((result) => {
            const city = result.city;
            const districts = result.districts;

            if (districts.length > 0) {
                // 顯示縣市名稱作為分組標題
                districtContainer.append(`<div class="mb-2 fw-bold">${city}</div>`);

                // 產生每個鄉鎮市區的 checkbox
                districts.forEach((dist) => {
                    const checkbox = `
                            <div class="form-check">
                                <input class="form-check-input district-checkbox"
                                       type="checkbox"
                                       value="${dist}"
                                       id="districtCheckbox_${encodeURIComponent(city)}_${encodeURIComponent(dist)}"
                                       data-city="${city}">
                                <label class="form-check-label" for="districtCheckbox_${encodeURIComponent(city)}_${encodeURIComponent(dist)}">
                                    ${dist}
                                </label>
                            </div>
                        `;
                    districtContainer.append(checkbox);
                });
                districtContainer.append('<hr class="my-2">');
            }
        });
        // 移除最後一個分隔線
        districtContainer.find('hr:last').remove();
    } catch (error) {
        console.error('載入鄉鎮市區失敗：', error);
        districtContainer.append('<div class="text-danger">載入鄉鎮市區失敗，請稍後再試</div>');
    }
}