package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.order.OnlineOrder;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface OnlineOrderDao {

    List<OnlineOrder> getAllOnlineOrders();
}
