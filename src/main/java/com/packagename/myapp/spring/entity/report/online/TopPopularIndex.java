package com.packagename.myapp.spring.entity.report.online;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TopPopularIndex implements Comparable<TopPopularIndex> {

    private String index;
    private String title;
    private Integer totalCount;
    private Integer orderCount;
    private Double totalSum;

    @Override
    public int compareTo(TopPopularIndex o) {
        return this.getTotalSum().compareTo(o.getTotalSum());
    }

    public String toString() {
        return getIndex() + "\t" + getTitle() + "\t" + getTotalCount() + "\t" + getOrderCount() + "\t" + getTotalSum();
    }

}
