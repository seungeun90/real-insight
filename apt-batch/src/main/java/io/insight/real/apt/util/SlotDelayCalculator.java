package io.insight.real.apt.util;

import java.time.LocalDateTime;

public class SlotDelayCalculator {

    private static final int SLOT_SIZE = 5; // 동시에 허용할 작업 수 (slot)
    private static final long DELAY_UNIT_MINUTES = 10; // slot 초과 시 지연할 기본 단위 (10분)

    /**
     * slot 기반으로 자동 delay 시간 계산
     *
     * @param activeJobCount 현재 활성화된 READY + IN_PROGRESS job 수
     * @return LocalDateTime 스케줄링할 예약 시간
     */
    public static LocalDateTime calculateScheduledTime(int activeJobCount) {
        LocalDateTime now = LocalDateTime.now();

        if (activeJobCount < SLOT_SIZE) {
            return now; // 즉시 실행
        }

        // 초과된 slot 개수만큼 delay (5개 초과시마다 10분씩 증가)
        int exceededSlot = activeJobCount / SLOT_SIZE;
        return now.plusMinutes(exceededSlot * DELAY_UNIT_MINUTES);
    }
}
