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
        .then(response => response.json())
        .then(data => {
            if (callback) callback(data); // 콜백 함수 실행
        })
        .catch(error => console.error(`데이터 요청 중 오류 발생 (${url}):`, error));
}

function createChart(chartVar, chartId, chartType, chartData, chartOptions,useDataLabels) {
    const ctx = document.getElementById(chartId).getContext("2d");
    
    // 기존 차트가 있으면 제거
    if (chartVar) {
        chartVar.destroy();
    }

    // 새로운 차트 생성
    return new Chart(ctx, {
        type: chartType,
        data: chartData,
        options: chartOptions,
        plugins: useDataLabels ? [ChartDataLabels] : []
    });
}

function customTooltipHandler(context, tooltipConfig) {
    let tooltipEl = getTooltipEl(); // 공통 툴팁 스타일 적용
    const tooltipModel = context.tooltip;

    if (!tooltipModel.dataPoints || tooltipModel.opacity === 0) {
        tooltipEl.style.opacity = 0;
        return;
    }

    const index = tooltipModel.dataPoints[0].dataIndex;
    const chart = tooltipModel.chart;
    const canvasRect = chart.canvas.getBoundingClientRect(); // 차트 캔버스 위치 가져오기

    let tooltipHTML = `<strong>${chart.data.labels[index]}</strong><br>`;

    // ✅ 차트별 데이터 처리 방식 적용
    chart.data.datasets.forEach(dataset => {
        const cntValue = tooltipConfig?.getValue 
            ? tooltipConfig.getValue(dataset, index) // 🟢 각 차트에서 전달된 getValue 함수 사용
            : dataset.data[index].toLocaleString();  // 기본값

        tooltipHTML += `
            <div style="display: flex; align-items: center; gap: 5px; margin-bottom: 2px;">
                <div style="width: 12px; height: 12px; background:${dataset.backgroundColor}; border-radius: 3px;"></div>
                <span style="color: ${dataset.backgroundColor}; font-weight: bold;">${dataset.label}:</span>
                <span>${cntValue}</span>
            </div>`;
    });

    tooltipEl.innerHTML = tooltipHTML;

    // 차트 내부에 툴팁이 고정되도록 위치 조정
    let left = canvasRect.left + tooltipModel.caretX;
    let top = canvasRect.top + tooltipModel.caretY;

    // 차트 영역을 벗어나지 않도록 보정
    left = Math.max(canvasRect.left + 10, Math.min(left, canvasRect.right - tooltipEl.offsetWidth - 10));
    top = Math.max(canvasRect.top + 10, Math.min(top, canvasRect.bottom - tooltipEl.offsetHeight - 10));

    tooltipEl.style.left = `${left}px`;
    tooltipEl.style.top = `${top}px`;
    tooltipEl.style.opacity = 1;
}


function getTooltipEl() {
    let tooltipEl = document.getElementById("chart-tooltip");

    // 툴팁 요소가 없으면 새로 생성
    if (!tooltipEl) {
        tooltipEl = document.createElement("div");
        tooltipEl.id = "chart-tooltip";
        tooltipEl.style.position = "fixed"; 
        tooltipEl.style.background = "rgba(255, 255, 255, 0.95)";
        tooltipEl.style.border = "1px solid #ccc";
        tooltipEl.style.borderRadius = "8px";
        tooltipEl.style.padding = "8px 12px";
        tooltipEl.style.boxShadow = "2px 2px 10px rgba(0,0,0,0.2)";
        tooltipEl.style.pointerEvents = "none";
        tooltipEl.style.transition = "opacity 0.2s ease-in-out, transform 0.2s ease-in-out";
        tooltipEl.style.opacity = 0;
        tooltipEl.style.fontSize = "14px";
        document.body.appendChild(tooltipEl);
    }

    return tooltipEl;
}
