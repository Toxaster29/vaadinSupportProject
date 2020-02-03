package com.packagename.myapp.spring.ui.other;

import com.packagename.myapp.spring.entity.insert.EmailPhone;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Route("insert")
public class DBInsertLayout extends VerticalLayout {

    public DBInsertLayout() {
        TextArea textArea = new TextArea("Add data");
        add(textArea);
        Button insertDataButton = new Button("Insert data");
        add(insertDataButton);
        insertDataButton.addClickListener(click -> {
           String data = textArea.getValue();
           if (!data.isEmpty()) {
               insertDataToDB(data);
           }
        });
    }

    private void insertDataToDB(String data) {
        String[] lines = data.split("\n");
        List<EmailPhone> phoneList = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\t");
            EmailPhone emailPhone = new EmailPhone(parts[0], parts[1]);
            if (!emailPhone.getPhone().equals("Не указан")) {
                if (!phoneList.stream().anyMatch(entity -> entity.getEmail().equals(emailPhone.getEmail()) ||
                        entity.getPhone().equals(emailPhone.getPhone()))) phoneList.add(emailPhone);

            }
        }
        writeToFile(phoneList);
    }

    private void writeToFile(List<EmailPhone> phoneList) {
        File file = null;
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("C:\\Users\\assze\\Desktop\\outEmailPhone.txt"));
            for (EmailPhone entity : phoneList) {
                pw.write(entity.getEmail() + ";" + entity.getPhone() + "\n");
            }
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

}
