package com.packagename.myapp.spring.ui.other;

import com.packagename.myapp.spring.entity.ufps.UfpsEntity;
import com.packagename.myapp.spring.service.ResourceService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("ufps")
public class ufpsLayout extends VerticalLayout {

    private ResourceService resourceService;

    public ufpsLayout(@Autowired ResourceService resourceService) {
        this.resourceService = resourceService;
        TextArea area = new TextArea("Set text");
        TextArea outArea = new TextArea();
        Button button = new Button("Ok");
        button.addClickListener(click -> {
            String outText = getUfpsFromText(area.getValue());
            outArea.setValue(outText);
        });
        Button groupByMacro = new Button("Group by macro");
        groupByMacro.addClickListener(click -> {
            String outText = groupUfps(area.getValue());
            outArea.setValue(outText);
        });
        add(area, button, groupByMacro, outArea);
    }

    private String groupUfps(String value) {
        List<UfpsEntity> ufpsEntities = resourceService.getUfpsEntityList();
        String[] lines = value.split("\n");
        for (String line : lines) {
            String[] words = line.split("\t");
            try {
                String id = ufpsEntities.stream().filter(c -> c.getDescription().toUpperCase().equals(words[0].toUpperCase())).findFirst().get().getId();
                System.out.println(words[1] + "\t" + id);
            } catch (Exception e) {
                System.out.println(words[0]);
            }
        }
        return "";
    }

    private String getUfpsFromText(String value) {
        String out = "";
        List<UfpsEntity> ufpsEntities = resourceService.getUfpsEntityList();
        String[] lines = value.split("\n");
        for (String line : lines) {
            if (line.indexOf("–") > 0) {
                String ufpsName = line.substring(0, line.indexOf("–") - 1).trim();
                out += ufpsEntities.stream().filter(ufpsEntity -> ufpsEntity.getDescription().toUpperCase()
                        .equals(ufpsName.toUpperCase())).findFirst().orElse(new UfpsEntity(line, null, null, null, null)).getId() + "\n";
            } else out += line + "\n";
        }
        return out;
    }
}
