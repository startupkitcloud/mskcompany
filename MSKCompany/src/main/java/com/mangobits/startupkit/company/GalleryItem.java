package com.mangobits.startupkit.company;

import javax.persistence.Embeddable;


@Embeddable
public class GalleryItem {


	private String id;
	
	
	public GalleryItem(){
		
	}
	
	
	public GalleryItem(String id){
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
