package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.service.ContractService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route("contractParam")
public class ContractChangeParamView extends VerticalLayout {

    @Autowired
    ContractService contractService;

    public ContractChangeParamView() {
        add(new Label("Review contract params"));
        initLayout();
    }

    private void initLayout() {
        Button reviewContractWithoutScheduler = new Button("Contracts without scheduler");
        reviewContractWithoutScheduler.addClickListener(click -> {
            contractService.reviewContractForSchedulers();
        });
        add(reviewContractWithoutScheduler);
    }


}
