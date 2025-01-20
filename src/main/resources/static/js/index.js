// get rest info
// console.log("get rest info !");
async function getRestInfo() {
    console.log("get rest info");
    let restsInfo = [];
    let count = {
        count: 6,
    };

    try{
        let res = await fetch(`/restaurants/getRandomRest/` + count, {
            method: 'Post',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8',
            },
            body: JSON.stringify(count),
        });
        if (!res.ok){
            // return {
            //     success: false,
            //     status: res.status,
            //     errorData: errorData,
            //     message: errorData.message || "error occured",
            // }
        }       
        let rests = await res.json();
        console.log(rests);

    }catch(err){
        console.error(err);
        return [];
    }

}

getRestInfo();

// 搜尋
$('#search-button').click(function () {
    const keyword = $('#searchKeyword').val();
    const reservedDate = $('#searchDate').val();
    const reservedTime = $('#searchTime').val();
    const guestCount = $('#searchGuestCount').val();
    window.location.href = `/searchPage?keyword=${keyword}&reservedDate=${reservedDate}&reservedTime=${reservedTime}&guestCount=${guestCount}`;
});