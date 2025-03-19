
let cityAddr = 'http://15.164.133.223:8085';
let aptAddr = 'http://15.164.133.223:8086';
let userAddr = 'http://15.164.133.223:8082';

const tabs = document.querySelectorAll('.tab');
const contents = document.querySelectorAll('.tab-content');
const provinceSelect = document.getElementById("province");
const regionSelect = document.getElementById("region");

tabs.forEach(tab => {
    tab.addEventListener('click', () => {
        tabs.forEach(t => t.classList.remove('active'));
        contents.forEach(c => c.classList.remove('active'));

        tab.classList.add('active');
        document.getElementById(tab.dataset.tab).classList.add('active');
    });
});

document.addEventListener("DOMContentLoaded", function () {
    loadCityDistrictCodeCount();
    loadAptRegionCodeCount();
    loadCityProvinces();
    loadProvincesForApt();
});
function sendCityDistrictCode() {
    fetchData(cityAddr + '/districts/temp', data => {
        console.log("완료");
    });
}

// city 코드 업로드 (엑셀)
function uploadCityDistrictCode() {
    const fileInput = document.getElementById('cityFile');
    if (!fileInput.files.length) {
        alert('파일을 선택해주세요.');
        return;
    }
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    fetch(cityAddr+'/district', {
        method: 'POST',
        body: formData
    }).then(() => {
        alert('City 코드 업로드 완료');
        loadCityDistrictCodeCount();
    });
}

// APT 코드 txt 업로드
function uploadAptRegionCode() {
    const fileInput = document.getElementById('aptFile');
    if (!fileInput.files.length) {
        alert('txt 파일을 선택해주세요.');
        return;
    }
    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    fetch(aptAddr+ '/district', {
        method: 'POST',
        body: formData
    }).then(() => {
        alert('APT 코드 업로드 완료');
        loadAptCount();
    });
}
// count 불러오기
function loadCityDistrictCodeCount() {
    fetchData(cityAddr + '/districts/count', data => {
        let num = formatNumber(data);
        document.getElementById('cityCount').innerText = `로드된 데이터: ${num}건`;
    });
}
function loadAptRegionCodeCount() {
    fetchData(aptAddr + '/districts/count', data => {
        let num = formatNumber(data);
        document.getElementById('aptCount').innerText = `로드된 데이터: ${num}건`;
    });
}


//배치 예약
//Province(광역시) 목록 로드
function loadCityProvinces() {
    let url = userAddr + '/districts';
    fetchData(url, data => {
        locationData = data;
        provinceSelect.innerHTML = `<option value="">시/도 선택</option>`;
        locationData.forEach(province => {
            const option = document.createElement("option");
            option.value = province.id;
            option.textContent = province.provinceName;
            provinceSelect.appendChild(option);
        });
    });
}

function loadProvincesForApt() {
    let url =  aptAddr + '/regions';

    fetchData(url, data => {
        aptLocationCode = data;
        regionSelect.innerHTML = `<option value="">시/도 선택</option>`;
        aptLocationCode.forEach(province => {
            const option = document.createElement("option");
            option.value = province.code;
            option.textContent = province.name;
            regionSelect.appendChild(option);
        });
    });
}

function startRegionBatch() {
    const regionCode = document.getElementById('province').value;
    const year = document.getElementById('year').value;
    const selectedBatch = Array.from(document.querySelectorAll('.checkbox-group input:checked')).map(c => c.value);

    if (selectedBatch.length === 0) {
        alert('배치 종류를 하나 이상 선택해주세요.');
        return;
    }

    // batchType 별 URL 매핑
    const urlMap = {
        '지역정보': cityAddr + '/batch/city',
        '인구비율': cityAddr + '/batch/population',
        '직장정보': cityAddr + '/batch/city/employ'
    };

    // 선택된 모든 배치 각각 호출
    selectedBatch.forEach(batchType => {
        const url = urlMap[batchType];
        if (!url) {
            console.error('알 수 없는 배치 타입:', batchType);
            return;
        }

        fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                regionCode: regionCode,
                year: year
            })
        }).then(() => {
            console.log(`${batchType} 배치 시작 완료`);
        }).catch(err => {
            console.error(`${batchType} 배치 호출 실패`, err);
        });
    });

    alert('선택한 배치가 모두 예약되었습니다.');
}

function startAptBatch() {
    const regionCode = document.getElementById('region').value;
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!startDate || !endDate) {
        alert('시작/종료 날짜를 선택해주세요.');
        return;
    }

    // YYYY-MM 형식을 YYYYMM 으로 변환
    const formattedStartDate = startDate.replace('-', '');
    const formattedEndDate = endDate.replace('-', '');

    const url = `${aptAddr}/apt/trade?regionCode=${regionCode}&startDate=${formattedStartDate}&endDate=${formattedEndDate}`;

    fetch(url, {
        method: 'POST'
    }).then(() => {
        alert('아파트 거래 정보 배치가 예약되었습니다.');
    });
}
