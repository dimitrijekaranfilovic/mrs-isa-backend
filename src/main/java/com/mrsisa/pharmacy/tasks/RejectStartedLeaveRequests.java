package com.mrsisa.pharmacy.tasks;

import com.mrsisa.pharmacy.service.ILeaveDaysRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableAsync
@Component
public class RejectStartedLeaveRequests {
    private final ILeaveDaysRequestService leaveDaysRequestService;

    @Autowired
    public RejectStartedLeaveRequests(ILeaveDaysRequestService leaveDaysRequestService) {
        this.leaveDaysRequestService = leaveDaysRequestService;
    }

    @Async
    @Scheduled(cron = "0 0 0 * * ?")
    public void rejectLeaveRequests() {
        leaveDaysRequestService.rejectPendingStartedLeaveDaysRequests();
    }
}