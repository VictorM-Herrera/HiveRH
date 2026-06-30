package com.HiveGroup.HiveRH.Features.Suspension;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuspensionScheduler {

    private final SuspensionService suspensionService;

    @Scheduled(cron = "0 0 3 * * *")
    public void reactivateEmployeesWithExpiredSuspensions() {
        suspensionService.reactivateEmployeesWithExpiredSuspensions();
    }
}
