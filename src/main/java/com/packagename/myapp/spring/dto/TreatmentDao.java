package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;

import java.util.List;

public interface TreatmentDao {

    List<Integer> getTreatmentByParams(TreatmentEntity treatmentEntity);

}
