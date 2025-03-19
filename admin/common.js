function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function formatKoreanNumber(num) {
    if (num >= 10000) {
        let man = Math.floor(num / 10000);
        let cheon = Math.floor((num % 10000) / 1000);
        return `약 ${man}만${cheon > 0 ? cheon + "천" : ""}명`;
    }
    return num + "명";
}

function updateNumberDisplay(elementId,value) {
    let formattedNumber = formatNumber(value);
    let formattedKorean = formatKoreanNumber(value);
    document.getElementById(elementId).textContent = `${formattedNumber} (${formattedKorean})`;
}

function fetchData(url, callback) {
    fetch(url)
        .then(response => {
            if (response.status === 204) {
                return null; // No Content 처리
            }
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return response.json(); // JSON 응답일 때
            } else {
                return response.text(); // 텍스트 응답일 때
            }
        })
        .then(data => {
            if (callback) callback(data);
        })
        .catch(error => console.error(`데이터 요청 중 오류 발생 (${url}):`, error));
}
