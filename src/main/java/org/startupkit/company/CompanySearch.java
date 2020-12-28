package org.startupkit.company;

import java.util.List;

public class CompanySearch {


    private String queryString;


    private String idCategory;


    private String idUser;


    private String idParent;


    private Double latitude;


    private Double longitude;


    private String city;

    private String contacEmail;

    private Integer page;

    private Integer pageItensNumber;


    private List<String> idCompanyIn;


    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(String idCategory) {
        this.idCategory = idCategory;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageItensNumber() {
        return pageItensNumber;
    }

    public void setPageItensNumber(Integer pageItensNumber) {
        this.pageItensNumber = pageItensNumber;
    }

    public List<String> getIdCompanyIn() {
        return idCompanyIn;
    }

    public void setIdCompanyIn(List<String> idCompanyIn) {
        this.idCompanyIn = idCompanyIn;
    }

    public String getIdParent() {
        return idParent;
    }

    public void setIdParent(String idParent) {
        this.idParent = idParent;
    }

    public String getContacEmail() {
        return contacEmail;
    }

    public void setContacEmail(String contacEmail) {
        this.contacEmail = contacEmail;
    }
}
