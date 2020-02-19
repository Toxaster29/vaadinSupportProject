package com.packagename.myapp.spring.ui.subscription;

import com.packagename.myapp.spring.dto.InsertDao;
import com.packagename.myapp.spring.entity.subscription.Subscription;
import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Route("annulment")
public class AnnulmentLayout extends VerticalLayout {

    private InsertDao insertDao;

    public AnnulmentLayout(@Autowired InsertDao insertDao) {
        this.insertDao = insertDao;
        TextArea textArea = new TextArea();
        Button button = new Button("Ok");
        button.addClickListener(click -> {
            getTreatment(textArea.getValue());
        });
        add(textArea, button);
    }

    private void getTreatment(String value) {
        String[] lines = value.split("\n");
        List<TreatmentEntity> treatmentEntities = new ArrayList<>();
        List<Subscription> subscriptions = new ArrayList<>();
        for (String line : lines) {
            String[] values = line.split("\t");
            treatmentEntities.add(new TreatmentEntity(Integer.parseInt(values[0]), Integer.parseInt(values[1])));
        }
        treatmentEntities.forEach(entity -> {
            subscriptions.addAll(insertDao.getSubscriptionWithoutAnnulment(entity));
        });
        writeToFile(subscriptions);
        System.out.println("Annul");
    }

    private void writeToFile(List<Subscription> subscriptions) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("C:\\Users\\assze\\Desktop\\tut.txt"));
            for (Subscription subscription : subscriptions) {
                pw.write(subscription.getStartDate().toString() + "\t" +
                        subscription.getPublicationCode() + "\t" +
                        subscription.getMsp() + "\t" +
                        subscription.getAlloc1() + "\t" +
                        subscription.getAlloc2() + "\t" +
                        subscription.getAlloc3() + "\t" +
                        subscription.getAlloc4() + "\t" +
                        subscription.getAlloc5() + "\t" +
                        subscription.getAlloc6() + "\t" +
                        subscription.getAlloc7() + "\t" +
                        subscription.getAlloc8() + "\t" +
                        subscription.getAlloc9() + "\t" +
                        subscription.getAlloc10() + "\t" +
                        subscription.getAlloc11() + "\t" +
                        subscription.getAlloc12() + "\t" +
                        subscription.getAllocSum() + "\t" +
                        subscription.getDeliveryType() + "\t" +
                        subscription.getPostCode() + "\t" +
                        subscription.getRegion() + "\t" +
                        subscription.getArea() + "\t" +
                        subscription.getCity() + "\t" +
                        subscription.getCity1() + "\t" +
                        subscription.getStreet() + "\t" +
                        subscription.getHouse() + "\t" +
                        subscription.getHousing() + "\t" +
                        subscription.getBuilding() + "\t" +
                        subscription.getFlat() + "\t" +
                        subscription.getSurname() + "\t" +
                        subscription.getName() + "\t" +
                        subscription.getPatronymic() + "\t" +
                        subscription.getOrgName() + "\n");
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

