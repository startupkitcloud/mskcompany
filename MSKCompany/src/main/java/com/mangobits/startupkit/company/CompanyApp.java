package com.mangobits.startupkit.company;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mangobits.startupkit.catalogue.service.Service;
import com.mangobits.startupkit.core.photo.GalleryItem;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyApp {

	
    private String id;
	

    
	private String fantasyName;
	


	private Double rating;
	
	
	
	private String desc;
	
	
	
	private String address;
	
	
	
	private Boolean fgOpen;
	
	
	
	private String businessHoursDesc;
	
	
	
	private List<GalleryItem> gallery;
	
	
	
	
	private List<Service> featuredServices;
	
	
	
	private List<Service> services;
	
	
	
	private Double latitude;
	
	
	
	private Double longitude;



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getFantasyName() {
		return fantasyName;
	}



	public void setFantasyName(String fantasyName) {
		this.fantasyName = fantasyName;
	}



	public Double getRating() {
		return rating;
	}



	public void setRating(Double rating) {
		this.rating = rating;
	}



	public String getDesc() {
		return desc;
	}



	public void setDesc(String desc) {
		this.desc = desc;
	}



	public String getAddress() {
		return address;
	}



	public void setAddress(String address) {
		this.address = address;
	}



	public Boolean getFgOpen() {
		return fgOpen;
	}



	public void setFgOpen(Boolean fgOpen) {
		this.fgOpen = fgOpen;
	}



	public String getBusinessHoursDesc() {
		return businessHoursDesc;
	}



	public void setBusinessHoursDesc(String businessHoursDesc) {
		this.businessHoursDesc = businessHoursDesc;
	}



	public List<GalleryItem> getGallery() {
		return gallery;
	}



	public void setGallery(List<GalleryItem> gallery) {
		this.gallery = gallery;
	}



	public List<Service> getFeaturedServices() {
		return featuredServices;
	}



	public void setFeaturedServices(List<Service> featuredServices) {
		this.featuredServices = featuredServices;
	}



	public List<Service> getServices() {
		return services;
	}



	public void setServices(List<Service> services) {
		this.services = services;
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
}
