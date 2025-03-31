let popRankingData = [];
let cityPopulation = [];
let workData = [];
let ageChart;
let workChart;
let cityPopChart;
let locationData ;
let aptLocationCode;
const years  = [2023,2022];
let userAddr = 'http://15.164.133.223:8082';
let aptAddr = 'http://15.164.133.223:8086';
const provinceSelect = document.getElementById("province");
const citySelect = document.getElementById("city");
const townSelect = document.getElementById("town");

document.addEventListener("DOMContentLoaded", function () {
    loadProvinces();
    loadProvincesForApt();
    //loadYears();
    showTab("populationTab"); // 페이지 로딩 시 기본 탭 설정
    showTab("top5table");
    //  **이벤트 리스너 추가**
    provinceSelect.addEventListener("change", loadCities);
    citySelect.addEventListener("change", loadTowns);

    // 페이지 로드 시 Province 목록 초기화
    document.addEventListener("DOMContentLoaded", loadProvinces);
});

let isRealEstateLoaded = false;

function showTab(tabId) {
    const tabElement = document.querySelector(`[data-tab='${tabId}']`);
    if (!tabElement) return;

    const group = tabElement.getAttribute("data-tab-group");

    // 같은 그룹 내 모든 탭 숨기기
    document.querySelectorAll(`.tab-content[data-tab-group='${group}']`).forEach(tab => tab.classList.add("hidden"));
    document.getElementById(tabId).classList.remove("hidden");

    // 모든 탭 버튼에서 active 제거 후 선택한 탭에 active 추가
    document.querySelectorAll(`.nav-link[data-tab-group='${group}']`).forEach(link => link.classList.remove("active"));
    document.querySelector(`[data-tab='${tabId}']`).classList.add("active");

    // 아파트 실거래가 탭을 처음 클릭하면 table.html 동적 로딩
    if (tabId === 'realEstateTab' && !isRealEstateLoaded) {
        loadAptHistory();
        isRealEstateLoaded = true;
    }
}


//Province(광역시) 목록 로드
function loadProvinces() {
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
    });
}

// **City(구) 목록 로드**
function loadCities() {
    citySelect.innerHTML = `<option value="">구 선택</option>`;
    townSelect.innerHTML = `<option value="">동 선택</option>`; // town 초기화

    const selectedProvince = locationData.find(province => province.id === provinceSelect.value);
    if (!selectedProvince) return;

    selectedProvince.cities.forEach(city => {
        const option = document.createElement("option");
        option.value = city.cityCode;
        option.textContent = city.cityName;
        citySelect.appendChild(option);
    });
}

// **Town(동) 목록 로드**
function loadTowns() {
    townSelect.innerHTML = `<option value="">동 선택</option>`;

    const selectedProvince = locationData.find(province => province.id === provinceSelect.value);
    if (!selectedProvince) return;

    const selectedCity = selectedProvince.cities.find(city => city.cityCode === citySelect.value);
    if (!selectedCity) return;

    selectedCity.towns.forEach(town => {
        const option = document.createElement("option");
        option.value = town;
        option.textContent = town;
        townSelect.appendChild(option);
    });
}

//연도 선택지 로드
function loadYears() {
   let yearSelect = document.getElementById("year");
   years.forEach(year => {
       let option = document.createElement("option");
       option.value = year;
       option.textContent = year;
       yearSelect.appendChild(option);
   });
}

//데이터 조회 

function loadData() {
   let provinceCode = document.getElementById("province").value;
   let cityCode = document.getElementById("city").value;
  // let year = document.getElementById("year").value;

   if (!provinceCode || !cityCode ) {
       alert("모든 필드를 선택해주세요.");
       return;
   }
    // 현재 활성화된 탭 확인
    let activeTab = document.querySelector(".nav-link.active").getAttribute("data-tab");

    if (activeTab === "populationTab") {
        // 지역 인구 및 경제 데이터 조회
        fetchCityData(provinceCode, cityCode, 2023);  // 도시 기초 정보
        fetchWorkData(provinceCode, cityCode);         // 직장 정보
        fetchPopInProvince(provinceCode, 2023);        // 모든 군/구 인구 정보(차트)
        fetchCityPopulation(provinceCode, cityCode);   // 도시 인구 비율 및 랭킹 정보
    } 
    else if (activeTab === "realEstateTab") {
        // 아파트 실거래가 조회
        let regionCode = findRegionCode();
        if (!regionCode) {
            alert("지역을 먼저 선택해주세요.");
            return;
        }
        loadAptHistory();
    }
}


function fetchCityPopulation(provinceCode,cityCode){
    let url =  userAddr + `/city/pop/province/${provinceCode}/city/${cityCode}`;
    fetchData(url,data => {
        popRankingData = data.popRankingData;
        cityPopulation = data.cityPopulation;
        createAgeChart();
        populateTop5Table();
        populateRankingTable();
    });
}

function renderWorkDataTable(data) {
    if (!data || data.length === 0) return;

    const tableBody = document.getElementById("workDataTableBody");
    tableBody.innerHTML = ""; // 기존 내용 초기화

    data.forEach(entry => {
        const townName = entry.townName || "-";
        const employCnt = entry.employCnt ? formatNumber(entry.employCnt) : "-";
        const corpCnt = entry.corpCnt ? formatNumber(entry.corpCnt) : "-";

        if(!entry.townCode) {
            document.getElementById("workplaces").textContent = employCnt;
            document.getElementById("employees").textContent = corpCnt;
        } else {
            // 테이블 행 추가
            const row = `<tr>
            <td>${townName}</td>
            <td>${employCnt}</td>
            <td>${corpCnt}</td>
            </tr>`;
            tableBody.innerHTML += row;
        }
    });

}

function fetchCityData(provinceCode,cityCode,year){
    let url =  userAddr + `/city?provinceCode=${provinceCode}&cityCode=${cityCode}&year=${year}`;
    fetchData(url,data =>{
        if(data.cityBasicInfo == null || data.cityRankingData == null) {
            alert('데이터가 존재하지 않습니다.');
            return;
        }
        updateNumberDisplay("population", data.cityBasicInfo.totalPopulation);
        updateNumberDisplay("households", data.cityBasicInfo.householdCount);
        document.getElementById("avgHouseholds").textContent = data.cityBasicInfo.averageHouseholdSize;
        document.getElementById("popDensity").textContent = data.cityBasicInfo.populationDensity;
        document.getElementById("averageAge").textContent = data.cityBasicInfo.averageAge;
        document.getElementById("agingIndex").textContent = data.cityBasicInfo.agingChildIndex;

        document.getElementById("populationRank").textContent = data.cityRankingData.totalPopRank;
        document.getElementById("popDensityRank").textContent = data.cityRankingData.popDensityRank;
        document.getElementById("agingRank").textContent = data.cityRankingData.agedChildIdxRank;
        document.getElementById("householdRank").textContent = data.cityRankingData.familyCntRank;
        document.getElementById("workplaceRank").textContent = data.cityRankingData.corpCntRank;
        document.getElementById("employeeRank").textContent = data.cityRankingData.employCntRank;
        
    });
}

function fetchWorkData(provinceCode,cityCode,year){
    let cityUrl =  userAddr + `/work/province/${provinceCode}/city/${cityCode}`;
    fetchData(cityUrl, data => {
        renderWorkDataTable(data);
    });

    let url = userAddr + `/work/province/${provinceCode}`;
    fetchData(url, data=>{
        createWorkChart(data);
    })
}

function fetchPopInProvince(provinceCode,year){
    let url = userAddr + `/cities/pop?provinceCode=${provinceCode}&year=${year}`;
    fetchData(url, data =>{
        createPopChart(data);
    });
}
function createPopChart(data){
    if (!data || data.length <= 0) return;
    const ctx = document.getElementById("populationChart").getContext("2d");
    if (cityPopChart) {
        cityPopChart.destroy();
    }
    const labels = data.map(entry => entry.cityName); // 구 이름 목록
    const datasets = [
        {
            label: "총 인구 수",
            data: data.map(entry => parseInt(entry.totalPopulation, 10)), // 숫자로 변환
            backgroundColor: "rgba(54, 162, 235, 0.7)", // 파란색
            borderColor: "rgba(54, 162, 235, 1)", // 테두리 색상
            borderWidth: 1
        }
    ];
    var options = {
        responsive: true,
        scales: {
            y: {
                ticks: {
                    callback: function(value) {
                        return value.toLocaleString(); // 천 단위 콤마 추가
                    }
                }
            }
        },
        scales: {
            x: { stacked: true },
            y: {
                stacked: true,
                type: "logarithmic", // 로그 스케일 적용
                min: 10000,  // 최소값 설정
                max: 1000000, // 최대값 설정
                ticks: {
                    callback: function(value) {
                        const values = [30000, 100000, 300000, 500000, 700000, 900000];
                        return values.includes(value) ? value.toLocaleString() : ""; // 특정 값만 표시
                    }
                }
            }
        }
    };
    cityPopChart = createChart(
        cityPopChart,
        "populationChart",
        "bar",
        {labels, datasets},
        {options}
    )
}
function createWorkChart(data){
    if (!data || data.length <= 0) return;

    const ctx = document.getElementById("employmentChart").getContext("2d");
    if (workChart) {
        workChart.destroy();
    }
    const labels = data.map(entry => entry.townName);
    const datasets = [
        {
            label: "종사자 수",
            data: data.map(entry => entry.employCnt || 0),
            backgroundColor: "rgba(54, 162, 235, 0.7)", // 파란색
            yAxisID: "yLarge"
        },
        {
            label: "사업체 수",
            data: data.map(entry => entry.corpCnt || 0),
            backgroundColor: "rgba(255, 99, 132, 0.7)", // 빨간색
            yAxisID: "ySmall"
        }
    ];
    const tooltipConfig = {
        getValue: (dataset, index) => dataset.data[index] ? Number(dataset.data[index]).toLocaleString() : "-"
    };

    var options = {
        responsive: true,
        plugins: { 
            tooltip: { enabled: false, external: context => customTooltipHandler(context, tooltipConfig) }
            ,legend: { position: "top" } 
        },
        scales:{
            ySmall: {
                type: "linear",
                position: "left",
                suggestedMin: 20000, // 최소값 2만
                suggestedMax: 120000, // 최대값 11만
                title: {
                    display: true,
                    text: "사업체",
                    position: "left"
                },
                ticks: {
                    callback: value => value.toLocaleString() // 천 단위 콤마
                }
            },
            yLarge: {
                type: "linear",
                position: "right",
                suggestedMin: 70000, // 최소값 7만
                suggestedMax: 800000, // 최대값 80만
                title: {
                    display: true,
                    text: "종사자",
                    position: "right"
                },
                ticks: {
                    callback: value => value.toLocaleString() // 천 단위 콤마
                },
                grid: { drawOnChartArea: false } // ✅ 오른쪽 y축 그리드 제거
            }
        }
        // scales: {
        //     //x: { stacked: true },
        //     y: {
        //         //stacked: true,
        //         type: "logarithmic", // 로그 스케일 적용
        //         min: 3000,  // 최소값 설정
        //         max: 1000000, // 최대값 설정
        //         ticks: {
        //             callback: function(value) {
        //                 const values = [3000, 10000, 30000, 50000, 70000, 90000];
        //                 return values.includes(value) ? value.toLocaleString() : ""; // 특정 값만 표시
        //             }
        //         }
        //     }
        // } 
    }
    workChart = createChart(
        workChart,
        "employmentChart",
        "bar",
        {labels, datasets},
        options
    )
    
}
//인구 비율 차트 그리기
function createAgeChart() {
    let data = cityPopulation;
    if (!data || data.length <= 0) return;

    const ctx = document.getElementById("ageRatioChart").getContext("2d");
    if (ageChart) {
        ageChart.destroy();
    }

    const labels = data.map(entry => entry.townName);
    const datasets = [
        { label: "0-10세", data: data.map(entry => entry.teenageLessThanPer), cnt: data.map(entry => entry.teenageLessThanCnt), backgroundColor: "rgba(255, 0, 0, 1)" },
        { label: "10대", data: data.map(entry => entry.teenagePer), cnt: data.map(entry => entry.teenageCnt), backgroundColor: "rgba(0, 0, 255, 1)" },
        { label: "20대", data: data.map(entry => entry.twentyPer), cnt: data.map(entry => entry.twentyCnt), backgroundColor: "rgba(255, 165, 0, 1)" },
        { label: "30대", data: data.map(entry => entry.thirtyPer), cnt: data.map(entry => entry.thirtyCnt), backgroundColor: "rgba(0, 128, 0, 1)" },
        { label: "40대", data: data.map(entry => entry.fortyPer), cnt: data.map(entry => entry.fortyCnt), backgroundColor: "rgba(128, 0, 128, 1)" },
        { label: "50대", data: data.map(entry => entry.fiftyPer), cnt: data.map(entry => entry.fiftyCnt), backgroundColor: "rgba(255, 192, 203, 1)" },
        { label: "60대", data: data.map(entry => entry.sixtyPer), cnt: data.map(entry => entry.sixtyCnt), backgroundColor: "rgba(0, 150, 150, 1)" },
        { label: "70세 이상", data: data.map(entry => entry.seventyMoreThanPer), cnt: data.map(entry => entry.seventyMoreThanCnt), backgroundColor: "rgba(128, 128, 128, 1)" }
    ];
    
    const tooltipConfig = {
        getValue: (dataset, index) => dataset.cnt[index] ? dataset.cnt[index].toLocaleString() : "-"
    };

    var options = {
        responsive: true,
        plugins: { 
            tooltip: { enabled: false, external: context => customTooltipHandler(context, tooltipConfig) }
            ,legend: { position: "top" }
            ,datalabels: {
                anchor: "center", // 글자가 막대 중앙에 위치
                align: "center", // 막대 내부 정렬
                formatter: value => `${value}%`, // 퍼센트 값만 표시
                color: "white", // 글씨 색상 (배경과 구분되도록 조절 가능)
                
                font: { weight: "bold", size: 10 }
            }
        },
        scales: {
            x: { stacked: true },
            y: {
                stacked: true,
                min: 0,  //  최소값 0%
                max: 100, //  최대값 100% 
                ticks: { callback: value => `${value}%` }
            }
        }
    }

    ageChart = createChart(
        ageChart,
        "ageRatioChart",
        "bar",
        {labels, datasets},
        options,
        true
    )
    
}

//모든 법정동 연령대별 비율 순위
function populateRankingTable() {
    let data = popRankingData;
    if(data.length <= 0) return;
    const rankingTableBody = document.getElementById("rankingTableBody");
    rankingTableBody.innerHTML = "";

    data.forEach(entry => {
        const row = `<tr>
            <td>${entry.townName || '-'}</td>
            <td>${entry.lessThanTeenPer || '-'}</td>
            <td>${entry.teenPer || '-'}</td>
            <td>${entry.twentyPer || '-'}</td>
            <td>${entry.thirtyPer || '-'}</td>
            <td>${entry.fortyPer || '-'}</td>
            <td>${entry.fiftyPer || '-'}</td>
            <td>${entry.sixtyPer || '-'}</td>
            <td>${entry.moreThanSevenPer || '-'}</td>
        </tr>`;
        rankingTableBody.innerHTML += row;
    });
}

//연령대별 법정동 top3
function populateTop5Table() {
    let data = popRankingData;
    if (!data || data.length === 0) return;

    const top5tableBody = document.getElementById("top5tableBody");
    top5tableBody.innerHTML = "";

    const ageGroups = [
        { label: "0-10세", key: "lessThanTeenPer" },
        { label: "10대", key: "teenPer" },
        { label: "20대", key: "twentyPer" },
        { label: "30~50대", key: "thirtyToFiftyPer" },
        { label: "70세 이상", key: "moreThanSevenPer" }
    ];

    // 각 연령대별 TOP 5 정렬 및 추가
    ageGroups.forEach(group => {
        // 정렬 전 데이터 변환
        const sortedData = [...data]
            .filter(entry => entry[group.key] !== undefined && entry[group.key] !== null) // 값이 있는 것만 필터링
            .map(entry => ({
                townName: entry.townName || "-",
                value: parseFloat(entry[group.key]) || "-"
            })) // 숫자로 변환
            .sort((a, b) => a.value - b.value) // 오름차순 정렬
            .slice(0, 5); // 상위 5개 선택

        let row = `<tr><td>${group.label}</td>`;
        sortedData.forEach(entry => {
            row += `<td>${entry.townName} </td>`;
        });
        row += `</tr>`;
        top5tableBody.innerHTML += row;
    });
}
