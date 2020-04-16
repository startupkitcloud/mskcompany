package com.mangobits.startupkit.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangobits.startupkit.admin.userb.UserB;
import com.mangobits.startupkit.admin.userb.UserBService;
import com.mangobits.startupkit.core.address.AddressUtils;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.dao.OperationEnum;
import com.mangobits.startupkit.core.dao.SearchBuilder;
import com.mangobits.startupkit.core.dao.SearchProjection;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.exception.DAOException;
import com.mangobits.startupkit.core.photo.GalleryItem;
import com.mangobits.startupkit.core.utils.BusinessUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.spatial.DistanceSortField;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import java.util.*;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CompanyServiceImpl implements CompanyService {


	private final int COMPANIES_PAGE = 10;


	@EJB
	private UserBService userBService;
	
	
	@EJB
	private ConfigurationService configurationService;
	
	
//	@EJB
//	private ServiceService serviceService;
	
	
	
	@New
	@Inject
	private CompanyDAO companyDAO;
	
	
	
	
	@Override
	public List<Company> listAll() throws Exception {
		return companyDAO.listAll();
	}
	
	
	
	@Override
	public List<CompanyCard> listActiveCards() throws Exception {
		
		List<CompanyCard> listCompanyCard = null;
		List<Company> listComp = listActives();
			
		if(listComp != null){
			listCompanyCard = new ArrayList<>();

			for(Company company : listComp){

				CompanyCard card = createCompanyCard(company);

				listCompanyCard.add(card);
			}
		}

		return listCompanyCard;
	}
	
	
	@Override
	public List<Company> listActives() throws Exception {
		return companyDAO.search(companyDAO.createBuilder()
				.appendParamQuery("status", CompanyStatusEnum.ACTIVE)
				.build());
	}
	
	
	@Deprecated
	@Override
	public void save(Company company) throws Exception {

		boolean newUser = false;
		Company companyBase = null;

		if(company.getId() == null){

			company.setCreationDate(new Date());
			company.setStatus(CompanyStatusEnum.ACTIVE);

			newUser = true;
		}
		else{
			companyBase = companyDAO.retrieve(company);
		}

		if(company.getRating() == null){
			company.setRating(2.5);
		}


		if(company.getBusinessHours() != null){
			company.setBusinessHoursDesc(WorkingHourUtils.businessHourDesc(company.getBusinessHours()));
		}

		if(company.getAddressInfo() != null){
			new AddressUtils().geocodeAddress(company.getAddressInfo());
		}

		if(!newUser && companyBase != null){

			UserB userBBase = userBService.retrieveByEmail(companyBase.getContactEmail());

			ObjectMapper mapper = new ObjectMapper();
			String resultStr = mapper.writeValueAsString(userBBase);
			UserB userB = mapper.readValue(resultStr, UserB.class);
			userB.setPassword(null);

			userB.setEmail(company.getContactEmail());
			userB.setName(company.getContact());

			userBService.updateUser(userB);
		}

		new BusinessUtils<>(companyDAO).basicSave(company);

		if(newUser){

			//cria um novo usuario
			UserB userB = new UserB();
			userB.setEmail(company.getContactEmail());
			userB.setName(company.getContact());
			userB.setRole(userBService.retrieveRole("company"));
			userB.setInfo(new HashMap<>());
			userB.getInfo().put("idCompany", company.getId());
			userB.setColor("#5c7bd2");

			userBService.createNewUser(userB);
		}
	}
	
	
	@Override
	public void saveCompany (Company company) throws Exception {

		if (company.getDocument() != null){

			if (company.getId() == null){
				SearchBuilder builder = new SearchBuilder();
				builder.appendParam("document", company.getDocument());
				List<Company> listComp = companyDAO.search(builder.build());
				if (listComp != null && listComp.size() > 0){
					throw new BusinessException("document_already_used");
				}
			}else {
				Company companyBase = retrieve(company.getId());
				if (companyBase.getDocument() != null && !companyBase.getDocument().equals(company.getDocument())){
					throw new BusinessException("document_cannot_be_changed");
				}

			}

		}

		if(company.getId() == null){

			company.setCreationDate(new Date());
			company.setStatus(CompanyStatusEnum.ACTIVE);
		}

		if(company.getRating() == null){
			company.setRating(2.5);
		}

		if(company.getBusinessHours() != null){
			company.setBusinessHoursDesc(WorkingHourUtils.businessHourDesc(company.getBusinessHours()));
		}


		new BusinessUtils<>(companyDAO).basicSave(company);
	}
	

	@Override
	public Company retrieve(String id) throws Exception {
		return companyDAO.retrieve(new Company(id));
	}

	
	
	@Override
	public Company retrieveByCode(String code) throws Exception {
		return companyDAO.retrieve(companyDAO.createBuilder()
				.appendParamQuery("code", code)
				.build());
	}


	@Override
	public Company loadByField(String field, String value) throws Exception {
		return companyDAO.retrieve(companyDAO.createBuilder()
				.appendParamQuery(field, value)
				.build());
	}
	
	
	
	@Override
	public String pathFilesCompany(String idCompany) throws Exception {
		return configurationService.loadByCode(ConfigurationEnum.PATH_BASE)
				.getValue() + "/company/" + idCompany;
	}



	@Override
	public void addPhoto(String idCompany, String idPhoto) throws Exception {
		Company company = companyDAO.retrieve(new Company(idCompany));

		if(company.getGallery() == null){
			company.setGallery(new ArrayList<>());
		}

		company.getGallery().add(new GalleryItem(idPhoto));
		companyDAO.update(company);
	}



	@Override
	public void removePhoto(String idCompany, String idPhoto) throws Exception {
		Company company = companyDAO.retrieve(new Company(idCompany));

		if(company.getGallery() != null){
			company.getGallery().remove(idPhoto);
		}

		companyDAO.update(company);
	}
	
	@Override
	public Company update(Company company){

		companyDAO.update(company);

		return company;
	}

	
	
	@Override
	public void changeStatus(String id) throws Exception {
		Company company = retrieve(id);

		if(company.getStatus() != null && company.getStatus().equals(CompanyStatusEnum.ACTIVE)){
			company.setStatus(CompanyStatusEnum.BLOCKED);
		}
		else{
			company.setStatus(CompanyStatusEnum.ACTIVE);
		}

		companyDAO.update(company);
	}
	

	@Override
	public List<CompanyCard> search(CompanySearch search) throws Exception{
		
		List<CompanyCard> list = null;

		SearchBuilder builder = companyDAO.createBuilder();
		builder.appendParamQuery("status", CompanyStatusEnum.ACTIVE);

		if(search.getIdCategory() != null){
			builder.appendParamQuery("category.id", search.getIdCategory());
		}

		if (search.getIdParent() != null) {
			builder.appendParamQuery("idParent", search.getIdParent());
		}

		if(search.getQueryString() != null && StringUtils.isNotEmpty(search.getQueryString().trim())){
			builder.appendParamQuery("fantasyName|addressInfo.street|addressInfo.district|addressInfo.city", search.getQueryString(), OperationEnum.OR_FIELDS);
		}

		if(search.getIdCompanyIn() != null && !search.getIdCompanyIn().isEmpty()){
			builder.appendParamQuery("id", search.getIdCompanyIn(), OperationEnum.IN);
		}

		if(search.getLatitude() != null){
			builder.setSort(new Sort(new DistanceSortField(search.getLatitude(), search.getLongitude(), "addressInfo")));
			builder.setProjection(new SearchProjection(search.getLatitude(), search.getLongitude(), "addressInfo", "distance"));
		}

		builder.setFirst(COMPANIES_PAGE * (search.getPage() - 1));

		builder.setMaxResults(COMPANIES_PAGE);

		List<Company> listComp = companyDAO.search(builder.build());

		if(listComp != null){

			list = new ArrayList<>();

			for(Company company : listComp){
				CompanyCard card = createCompanyCard(company);
				list.add(card);
			}
		}
		
		return list;
	}


	@Override
	public CompanyCard createCompanyCard(Company company) throws Exception {
		
		CompanyCard card = null;
		

			card = new CompanyCard();
			
			try {
				card.setDistance((Double)companyDAO.forceGet(company, "distance"));
			}
			catch(Exception e){
				//nao faz nada
			}

			card.setAddress(AddressUtils.textualAddress(company.getAddressInfo()));
			card.setId(company.getId());
			card.setName(company.getFantasyName());
			card.setRating(company.getRating());
			card.setAddressInfo(company.getAddressInfo());
			
			if(company.getInfo() != null){
				card.setInfo(company.getInfo());
			}
		
		return card;
	}

	@Override
	public CompanyCard createCompanyCard(String idCompany) throws Exception {
		CompanyCard card;
		Company company = companyDAO.retrieve(new Company(idCompany));
		card = createCompanyCard(company);
		return card;
	}

	@Override
	public List<CompanyCard> listByIdParent(String idParent) throws Exception {

		List<CompanyCard> list = null;

		SearchBuilder builder = companyDAO.createBuilder();
		builder.appendParamQuery("idParent", idParent);
		builder.appendParamQuery("status", CompanyStatusEnum.ACTIVE);

		Sort sort = new Sort(new SortField("fantasyName", SortField.Type.STRING, true));
		builder.setSort(sort);

		List<Company> listComps = companyDAO.search(builder.build());

		if(listComps != null){
			list = new ArrayList<>();

			for(Company company : listComps){
				list.add(createCompanyCard(company));
			}
		}

		return list;
	}

	@Override
	public CompanyResultSearch searchAdmin(CompanySearch search) throws BusinessException {

		CompanyResultSearch companyResultSearch = null;

		try {

			SearchBuilder builder = companyDAO.createBuilder();
			builder.appendParamQuery("status", CompanyStatusEnum.ACTIVE);

			if(search.getIdCategory() != null){
				builder.appendParamQuery("category.id", search.getIdCategory());
			}

			if (search.getIdParent() != null) {
				builder.appendParamQuery("idParent", search.getIdParent());
			}


			if(search.getQueryString() != null && StringUtils.isNotEmpty(search.getQueryString().trim())){
				builder.appendParamQuery("fantasyName|addressInfo.street|addressInfo.district|addressInfo.city|category", search.getQueryString(), OperationEnum.OR_FIELDS);
			}

			if(search.getIdCompanyIn() != null && !search.getIdCompanyIn().isEmpty()){
				builder.appendParamQuery("id", search.getIdCompanyIn(), OperationEnum.IN);
			}

			if(search.getLatitude() != null){
				builder.setSort(new Sort(new DistanceSortField(search.getLatitude(), search.getLongitude(), "addressInfo")));
				builder.setProjection(new SearchProjection(search.getLatitude(), search.getLongitude(), "addressInfo", "distance"));
			}

			builder.setFirst(COMPANIES_PAGE * (search.getPage() - 1));

			builder.setMaxResults(COMPANIES_PAGE);

			companyResultSearch = new CompanyResultSearch();

			companyResultSearch.setList(companyDAO.search(builder.build()));

			companyResultSearch.setTotalAmount(totalAmount(builder));

			companyResultSearch.setPageQuantity(pageQuantity(COMPANIES_PAGE, companyResultSearch.getTotalAmount()));

		} catch (Exception e){
			throw new BusinessException("Got an error searching Companies", e);
		}

		return companyResultSearch;
	}

	private int pageQuantity(int numberOfItensByPage, int totalAmount) throws Exception {

		int pageQuantity;

		if (totalAmount % numberOfItensByPage != 0) {
			pageQuantity = (totalAmount / numberOfItensByPage) + 1;
		} else {
			pageQuantity = totalAmount / numberOfItensByPage;
		}

		return pageQuantity;
	}

	private Integer totalAmount(SearchBuilder builder) throws DAOException {
		return companyDAO.count(builder.build());
	}
}
