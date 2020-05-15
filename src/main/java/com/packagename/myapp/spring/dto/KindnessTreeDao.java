package com.packagename.myapp.spring.dto;

import java.util.List;

public interface KindnessTreeDao {

    List<Integer> getOrganizationIdsByType(int i);

    List<Integer> getActiveBidIds();
}
