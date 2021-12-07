package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.dto.OnlineOrderDao;
import com.packagename.myapp.spring.entity.order.OnlineOrder;
import com.packagename.myapp.spring.service.report.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {

    private OnlineOrderDao onlineOrderDao;
    private ReportService reportService;

    public void getHidForNewBuyers() {
        List<String> newHids = new ArrayList<>();
        Map<String, List<OnlineOrder>> orderMap = onlineOrderDao.getAllOnlineOrders().stream()
                .collect(Collectors.groupingBy(OnlineOrder::getHid));
        orderMap.entrySet().forEach(hid -> {
            if (hid.getValue().stream().allMatch(order -> order.getCreateDate()
                    .isAfter(LocalDateTime.of(2021, 10, 4, 0,0,0)))) {
                newHids.add(hid.getKey());
            }
        });
        reportService.writeTextToFile(newHids, "reportData.txt");
        System.out.println("vse");
    }
}
