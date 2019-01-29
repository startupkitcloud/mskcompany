package com.mangobits.startupkit.company;

import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import java.util.List;

public class CompanyResultSearch {

    @IndexedEmbedded
    @ElementCollection(
            fetch = FetchType.EAGER
    )
    private List<Company> list;
    private int totalAmount;
    private int pageQuantity;

    public CompanyResultSearch() {
    }


    public int getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getPageQuantity() {
        return this.pageQuantity;
    }

    public void setPageQuantity(int pageQuantity) {
        this.pageQuantity = pageQuantity;
    }

    public List<Company> getList() {
        return list;
    }

    public void setList(List<Company> list) {
        this.list = list;
    }
}
