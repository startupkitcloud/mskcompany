package com.mangobits.startupkit.company;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;

import com.mangobits.startupkit.core.address.AddressInfo;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.SortableField;
import org.hibernate.search.annotations.Spatial;

public class CompanyCard {
	
	private String id;
	
	
	private String name;
	
	
	private String address;
	
	
	private Double rating;
	
	
	private String serviceFeatured;
	
	
	private Double priceFeatured;
	
	
	private Double distance;


	@SortableField
	@Spatial
	@IndexedEmbedded
	private AddressInfo addressInfo;
	
	
	@IndexedEmbedded
	@ElementCollection(fetch=FetchType.EAGER)
	private Map<String, String> info;
	


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public Double getRating() {
		return rating;
	}


	public void setRating(Double rating) {
		this.rating = rating;
	}


	public String getServiceFeatured() {
		return serviceFeatured;
	}


	public void setServiceFeatured(String serviceFeatured) {
		this.serviceFeatured = serviceFeatured;
	}


	public Double getPriceFeatured() {
		return priceFeatured;
	}


	public void setPriceFeatured(Double priceFeatured) {
		this.priceFeatured = priceFeatured;
	}


	public Double getDistance() {
		return distance;
	}


	public void setDistance(Double distance) {
		this.distance = distance;
	}


	public Map<String, String> getInfo() {
		return info;
	}


	public void setInfo(Map<String, String> info) {
		this.info = info;
	}


	public AddressInfo getAddressInfo() {
		return addressInfo;
	}

	public void setAddressInfo(AddressInfo addressInfo) {
		this.addressInfo = addressInfo;
	}
}
