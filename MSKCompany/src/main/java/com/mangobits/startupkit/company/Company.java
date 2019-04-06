package com.mangobits.startupkit.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mangobits.startupkit.core.address.AddressInfo;
import com.mangobits.startupkit.core.photo.GalleryItem;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;


@JsonIgnoreProperties(value={"hibernateLazyInitializer", "handler"}, ignoreUnknown=true)
@Entity(name="company")
@Indexed
public class Company {
	
	
	@Id
	@DocumentId
	@GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
	
	
	
	private String socialName;
	
	
	
	@Field
	private String code;
	
	
	
	@Field
	private String idUser;
	
	
	
	@Field
	private String document;//cnpj
	
	
	
	@Field
	private String documentType;



	@Field
	@SortableField
	private String fantasyName;
	
	
	
	@Field
	private Double rating;
	
	
	@Field
	private String desc;
	
	
	
	private String colorImage;
	
	
	
	@Field
	@Enumerated(EnumType.STRING)
	private CompanyStatusEnum status;
	
	
	@JsonIgnore
	@Field
	private Boolean fgFeatured;



	@Field
	private Boolean fgBranch;


	private String branchName;
	
	

	@Field
	private String phone;



	@Field
	private String type;



	@Field
	private String idParent;
	
	
	
	@Field
	private Long phoneNumber;
	
	
	
	private Integer phoneCountryCode;
	
	
	
	
	@SortableField
	@Spatial	
	@IndexedEmbedded
	private AddressInfo addressInfo;
	
	
	
//	@IndexedEmbedded
//	private BankAccount bankAccount;
	
	
	
	@Field
	private String contact;
	
	
	
	private String contactPhone;
	
	
	
	private Long contactPhoneNumber;
	
	
	
	private Integer contactPhoneCountryCode;
	
	
	
	private String contactEmail;
	
	
	
	@Transient
	private Boolean fgOpen;
	
	
	
	@Transient
	private String categoriesDesc;


	private String category;
	
	
	
	
	@IndexedEmbedded
	@ElementCollection(fetch=FetchType.EAGER)
	@OrderColumn(name = "seq")
	private List<WorkingHour> businessHours;
	
	
	
	@IndexedEmbedded
	@ElementCollection(fetch=FetchType.EAGER)
	private List<ScheduleException> scheduleException;
	
	
	
//	@IndexedEmbedded(includeEmbeddedObjectId=true)
//	@ElementCollection(fetch=FetchType.EAGER)
//	private List<Category> categories;
//
//
//
//	@IndexedEmbedded
//	@ElementCollection(fetch=FetchType.EAGER)
//	private List<SaleOff> salesOff;
	
	

	@ElementCollection(fetch=FetchType.EAGER)
	private List<GalleryItem> gallery;
	
	
	
	@IndexedEmbedded
	@ElementCollection(fetch=FetchType.EAGER)
	private Map<String, String> info;
	
	
	
	private String businessHoursDesc;
	
	
	@JsonIgnore
	@Field
	private Date creationDate;
	
	
	@Transient
	private Double distance;
	
	
	private Double companyTax;
	
	
	private Double markeiTax;
	
	
	private Double salesManTax;
	
	
	
	@Field
	private String idSalesMan;
	
	
	@Field
	@SortableField
	private String idPlan;
	
	
	public Company(){
		
	}
	
	
	public Company(String id){
		this.id = id;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getSocialName() {
		return socialName;
	}


	public void setSocialName(String socialName) {
		this.socialName = socialName;
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


	public Boolean getFgFeatured() {
		return fgFeatured;
	}


	public void setFgFeatured(Boolean fgFeatured) {
		this.fgFeatured = fgFeatured;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public Long getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(Long phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public Integer getPhoneCountryCode() {
		return phoneCountryCode;
	}


	public void setPhoneCountryCode(Integer phoneCountryCode) {
		this.phoneCountryCode = phoneCountryCode;
	}



	public AddressInfo getAddressInfo() {
		return addressInfo;
	}


	public void setAddressInfo(AddressInfo addressInfo) {
		this.addressInfo = addressInfo;
	}


	public Boolean getFgOpen() {
		return fgOpen;
	}


	public void setFgOpen(Boolean fgOpen) {
		this.fgOpen = fgOpen;
	}


	public String getCategoriesDesc() {
		return categoriesDesc;
	}


	public void setCategoriesDesc(String categoriesDesc) {
		this.categoriesDesc = categoriesDesc;
	}


	public List<WorkingHour> getBusinessHours() {
		return businessHours;
	}


	public void setBusinessHours(List<WorkingHour> businessHours) {
		this.businessHours = businessHours;
	}



//	public List<Category> getCategories() {
//		return categories;
//	}
//
//
//	public void setCategories(List<Category> categories) {
//		this.categories = categories;
//	}
//
//
//	public List<SaleOff> getSalesOff() {
//		return salesOff;
//	}
//
//
//	public void setSalesOff(List<SaleOff> salesOff) {
//		this.salesOff = salesOff;
//	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public Double getDistance() {
		return distance;
	}


	public void setDistance(Double distance) {
		this.distance = distance;
	}


	public String getBusinessHoursDesc() {
		return businessHoursDesc;
	}


	public void setBusinessHoursDesc(String businessHoursDesc) {
		this.businessHoursDesc = businessHoursDesc;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getDocument() {
		return document;
	}


	public void setDocument(String document) {
		this.document = document;
	}


	public String getContact() {
		return contact;
	}


	public void setContact(String contact) {
		this.contact = contact;
	}


	public String getContactPhone() {
		return contactPhone;
	}


	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}


	public Long getContactPhoneNumber() {
		return contactPhoneNumber;
	}


	public void setContactPhoneNumber(Long contactPhoneNumber) {
		this.contactPhoneNumber = contactPhoneNumber;
	}


	public Integer getContactPhoneCountryCode() {
		return contactPhoneCountryCode;
	}


	public void setContactPhoneCountryCode(Integer contactPhoneCountryCode) {
		this.contactPhoneCountryCode = contactPhoneCountryCode;
	}


	public String getContactEmail() {
		return contactEmail;
	}


	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}


	public List<GalleryItem> getGallery() {
		return gallery;
	}


	public void setGallery(List<GalleryItem> gallery) {
		this.gallery = gallery;
	}


	public List<ScheduleException> getScheduleException() {
		return scheduleException;
	}


	public void setScheduleException(List<ScheduleException> scheduleException) {
		this.scheduleException = scheduleException;
	}


	public CompanyStatusEnum getStatus() {
		return status;
	}


	public void setStatus(CompanyStatusEnum status) {
		this.status = status;
	}


//	public BankAccount getBankAccount() {
//		return bankAccount;
//	}
//
//
//	public void setBankAccount(BankAccount bankAccount) {
//		this.bankAccount = bankAccount;
//	}


	public String getDocumentType() {
		return documentType;
	}


	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}


	


	public Double getCompanyTax() {
		return companyTax;
	}


	public void setCompanyTax(Double companyTax) {
		this.companyTax = companyTax;
	}


	public Double getMarkeiTax() {
		return markeiTax;
	}


	public void setMarkeiTax(Double markeiTax) {
		this.markeiTax = markeiTax;
	}


	public Double getSalesManTax() {
		return salesManTax;
	}


	public void setSalesManTax(Double salesManTax) {
		this.salesManTax = salesManTax;
	}


	public String getIdSalesMan() {
		return idSalesMan;
	}


	public void setIdSalesMan(String idSalesMan) {
		this.idSalesMan = idSalesMan;
	}


	public Map<String, String> getInfo() {
		return info;
	}


	public void setInfo(Map<String, String> info) {
		this.info = info;
	}


	public String getIdPlan() {
		return idPlan;
	}


	public void setIdPlan(String idPlan) {
		this.idPlan = idPlan;
	}


	public String getIdUser() {
		return idUser;
	}


	public void setIdUser(String idUser) {
		this.idUser = idUser;
	}


	public String getColorImage() {
		return colorImage;
	}


	public void setColorImage(String colorImage) {
		this.colorImage = colorImage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIdParent() {
		return idParent;
	}

	public void setIdParent(String idParent) {
		this.idParent = idParent;
	}


	public Boolean getFgBranch() {
		return fgBranch;
	}

	public void setFgBranch(Boolean fgBranch) {
		this.fgBranch = fgBranch;
	}


	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}