package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.service.OrderService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("orders")
public class OrderView extends VerticalLayout {

    private OrderService orderService;

    public OrderView(OrderService orderService) {
        this.orderService = orderService;

        initMainLayout();
    }

    private void initMainLayout() {
        setSizeFull();
        Button getHidForNewBuyers = new Button("New Buyers");
        getHidForNewBuyers.addClickListener(click -> {
           orderService.getHidForNewBuyers();
        });
        add(getHidForNewBuyers);
    }

}
