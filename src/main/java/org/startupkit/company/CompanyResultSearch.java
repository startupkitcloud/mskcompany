package org.startupkit.company;

import java.util.List;

public class CompanyResultSearch {

    private List<Company> list;
    private long totalAmount;
    private long pageQuantity;

    public CompanyResultSearch() {
    }


    public long getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getPageQuantity() {
        return this.pageQuantity;
    }

    public void setPageQuantity(long pageQuantity) {
        this.pageQuantity = pageQuantity;
    }

    public List<Company> getList() {
        return list;
    }

    public void setList(List<Company> list) {
        this.list = list;
    }
}
