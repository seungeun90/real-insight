let realEstateData;

function loadAptHistory() {
    let regionCode = findRegionCode();
    if(regionCode==null) return;
    let url = 'http://localhost:8086/apt/trade/region/' + regionCode +"?size="+"84";
    fetchData(url, data => {
        realEstateData = data;
        drawAptHistoryTable();
    });
}
function findRegionCode() {
    let provinceId = document.getElementById("province").value;
    let cityCode = document.getElementById("city").value;

    // 1. locationData provinceId와 cityCode로 해당 지역 찾기
    const selectedProvince = locationData.find(d => d.id === provinceId);
    if (!selectedProvince) return null;

    const selectedCity = selectedProvince.cities.find(c => c.cityCode === cityCode);
    if (!selectedCity) return null;

    // 2. 매칭된 지역 이름 생성 ("서울특별시 종로구" 형식)
    const fullName = `${selectedProvince.provinceName} ${selectedCity.cityName}`;

    // 3. aptRegionCode에서 같은 name을 가진 지역 찾기
    const region = aptLocationCode.find(r => r.name === fullName);

    return region ? region.code : null;
}

function drawAptHistoryTable() {
    const tableBody = document.querySelector("#realEstateTable tbody");
    tableBody.innerHTML = ""; 

    realEstateData.forEach(data => {
        const row = `
            <tr>
                <td>${data.umdNm}</td>
                <td>${data.aptNm}</td>
                <td>${data.buildYear}</td>
                <td>${formatNumber(data.maxTrade.price)}</td>
                <td>${data.maxTrade.excluAr} </td>
                <td>${data.maxTrade.floor} </td>
                <td>${data.maxTrade.dealDate}</td>
                <td>${formatNumber(data.minTrade.price)}</td>
                <td>${data.minTrade.excluAr} </td>
                <td>${data.minTrade.floor} </td>
                <td>${data.minTrade.dealDate}</td>
                <td>${data.profitRate.toFixed(2)}%</td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });
}
let sortDirections = [true, true, true, true];

function sortTable(index) {
    // 테이블 컬럼 순서에 맞는 키 매칭
    const keys = ["umdNm", null, "buildYear", "maxTrade.price", null,null, null, "minTrade.price", null,null, null, "profitRate"];

    if (!keys[index]) return; // 정렬할 수 없는 컬럼이면 무시

    let ascending = sortDirections[index];

    realEstateData.sort((a, b) => {
        let aValue, bValue;

        if (index === 3) {
            aValue = a.maxTrade?.price ?? 0; // 전고점
            bValue = b.maxTrade?.price ?? 0;
        } else if (index === 6) {
            aValue = a.minTrade?.price ?? 0; // 전저점
            bValue = b.minTrade?.price ?? 0;
        } else if (index === 9) {
            aValue = a.profitRate ?? 0; // 수익률
            bValue = b.profitRate ?? 0;
        } else {
            aValue = a[keys[index]];
            bValue = b[keys[index]];
        }

        if (typeof aValue === "string") {
            return ascending ? aValue.localeCompare(bValue) : bValue.localeCompare(aValue);
        } else {
            return ascending ? aValue - bValue : bValue - aValue;
        }
    });

    sortDirections[index] = !ascending; // 정렬 방향 반전

    updateSortIcons(index, sortDirections[index]); // 아이콘 업데이트

    drawAptHistoryTable();
}

function updateSortIcons(index, isAscending) {
    // 모든 정렬 가능한 th 찾기 (thead의 전체 th)
    const allThs = document.querySelectorAll("thead th");

    // 모든 정렬 아이콘을 기본값(▲)으로 초기화
    allThs.forEach(th => {
        const icon = th.querySelector(".sort-icon");
        if (icon) icon.innerHTML = "▲"; 
    });

    // 현재 클릭한 컬럼의 아이콘만 변경 (index가 범위 내에 있을 때만 실행)
    if (index >= 0 && index < allThs.length) {
        const icon = allThs[index].querySelector(".sort-icon");
        if (icon) {
            icon.innerHTML = isAscending ? "▲" : "▼"; // 오름차순이면 ▲, 내림차순이면 ▼
        }
    }
}
