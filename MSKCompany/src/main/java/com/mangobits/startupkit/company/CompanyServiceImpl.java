package com.mangobits.startupkit.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangobits.startupkit.admin.user.UserB;
import com.mangobits.startupkit.admin.user.UserBService;
import com.mangobits.startupkit.catalogue.category.Category;
import com.mangobits.startupkit.catalogue.saleoff.SaleOff;
import com.mangobits.startupkit.catalogue.service.Service;
import com.mangobits.startupkit.catalogue.service.ServiceService;
import com.mangobits.startupkit.catalogue.status.ItemStatusEnum;
import com.mangobits.startupkit.core.address.AddressUtils;
import com.mangobits.startupkit.core.configuration.ConfigurationEnum;
import com.mangobits.startupkit.core.configuration.ConfigurationService;
import com.mangobits.startupkit.core.dao.OperationEnum;
import com.mangobits.startupkit.core.dao.SearchBuilder;
import com.mangobits.startupkit.core.dao.SearchProjection;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.core.photo.GalleryItem;
import com.mangobits.startupkit.core.utils.BusinessUtils;
import com.mangobits.startupkit.user.UserCard;
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
import java.util.stream.Collectors;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class CompanyServiceImpl implements CompanyService {

	
	private final int COMPANIES_PAGE = 10;
	
	
	@EJB
	private UserBService userBService;
	
	
	@EJB
	private ConfigurationService configurationService;
	
	
	@EJB
	private ServiceService serviceService;
	
	
	
	@New
	@Inject
	private CompanyDAO companyDAO;
	
	
	
	
	@Override
	public List<Company> listAll() throws Exception {
		
		List<Company> list = null;
		

			list = companyDAO.listAll();

		
		return list;
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
		
		List<Company> list = null;
		

			list = companyDAO.search(new SearchBuilder()
					.appendParam("status", CompanyStatusEnum.ACTIVE)
					.build());

		
		return list;
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
				UserB userB = (UserB) mapper.readValue(resultStr, UserB.class);
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
			
			if(company.getAddressInfo() != null){
				new AddressUtils().geocodeAddress(company.getAddressInfo());
			}
			
			new BusinessUtils<>(companyDAO).basicSave(company);

	}
	

	@Override
	public Company retrieve(String id) throws Exception {
		
		Company company = null;
		

			company = companyDAO.retrieve(new Company(id));
			


		return company;
	}

	
	
	@Override
	public Company retrieveByCode(String code) throws Exception {
		
		Company company = null;
		

			Map<String, Object> params = new HashMap<>();
			params.put("code", code);
			
			company = companyDAO.retrieve(params);
			

		
		return company;
	}


	@Override
	public Company loadByField(String field, String value) throws Exception {

		Company company = null;

			Map<String, Object> params = new HashMap<>();
			params.put(field, value);

			company = companyDAO.retrieve(params);

		return company;
	}
	
	
	
	@Override
	public String pathFilesCompany(String idCompany) throws Exception {
		

			return configurationService.loadByCode(ConfigurationEnum.PATH_BASE).getValue() + "/company/" + idCompany;  

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
	public void changeStatusService(String idService) throws Exception {
		

			serviceService.changeStatus(idService);
			Service service = serviceService.load(idService);
			
			processService(service);
			

	}



	@Override
	public List<CompanyCard> search(CompanySearch search) throws Exception{
		
		List<CompanyCard> list = null;

			SearchBuilder builder = companyDAO.createBuilder();
			builder.appendParamQuery("status", CompanyStatusEnum.ACTIVE);
			
			if(search.getIdCategory() != null){
				builder.appendParamQuery("categories.id", search.getIdCategory());
			}

			if(search.getQueryString() != null && StringUtils.isNotEmpty(search.getQueryString().trim())){
				builder.appendParamQuery("fantasyName|addressInfo.street|addressInfo.district|addressInfo.city|categories.name", search.getQueryString(), OperationEnum.OR_FIELDS);
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
					
					Service service = serviceService.firstFeaturedServiceByCategory(company.getId(), search.getIdCategory());
					
					if(service != null){
						
						card.setServiceFeatured(service.getName());
						card.setPriceFeatured(service.getPrice());
					}
					
					list.add(card);
				}
			}


		
		return list;
	}
	
	
	
	@Override
	public List<SaleOff> mainSalesOff(CompanySearch search) throws Exception {
		
		List<SaleOff> list = null;
		

			SearchBuilder builder = new SearchBuilder();
			
			builder.appendParam("gt:salesOff.priceBefore", 0.0);
			builder.appendParam("gt:salesOff.priceNow", 0.0);
			
			builder.setMaxResults(4);
			
			if(search.getLatitude() != null){
				builder.setSort(new Sort(new DistanceSortField(search.getLatitude(), search.getLongitude(), "addressInfo")));
			}
			
			List<Company> listComp = companyDAO.search(builder.build());
			
			list = listComp.stream()
					.filter(p -> p.getSalesOff() != null && p.getSalesOff().size() > 0)
					.map(p -> p.getSalesOff().get(0))
					.filter(p -> p.getPriceBefore() != null && p.getPriceNow() != null)
					.collect(Collectors.toList());
			
//			Collections.shuffle(list);


		
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
		
		CompanyCard card = null;
		

			Company company = companyDAO.retrieve(new Company(idCompany));
			card = createCompanyCard(company);
			

		
		return card;
	}



	@Override
	public List<UserCard> listPros(String idService) throws Exception {
		
		List<UserCard> list = null;
		

			Service service = serviceService.load(idService);
			
			list = service.getWorkers();
			

		
		return list;
	}



	@Override
	public void processService(Service service) throws Exception {
		

			Company company = companyDAO.retrieve(new Company(service.getIdCompany()));
			
			boolean update = false;
			
			if(service.getStatus().equals(ItemStatusEnum.BLOCKED)){
				
				if(company.getSalesOff() != null){
					
					SaleOff saleOff = company.getSalesOff().stream()
							.filter(p -> p.getIdProduct().equals(service.getId()))
							.findFirst()
							.orElse(null);
					
					if(saleOff != null){
						company.getSalesOff().remove(saleOff);
						update = true;
					}
				}
				
				if(company.getCategories() != null){
					
					Category category = company.getCategories().stream()
							.filter(p -> p.getId().equals(service.getCategory().getId()))
							.findFirst()
							.orElse(null);
					
					if(category != null){
						company.getCategories().remove(category);
						update = true;
					}
				}	
			}
			else{
				
				if(service.getSaleOff() != null){
					if(company.getSalesOff() == null){
						company.setSalesOff(new ArrayList<>());
					}
					
					service.getSaleOff().setIdProduct(service.getId());
					service.getSaleOff().setIdCompany(company.getId());
					service.getSaleOff().setPriceBefore(service.getPrice());
					service.getSaleOff().setTitle(service.getName());
					
					company.getSalesOff().add(service.getSaleOff());
					update = true;
				}
				
				if(company.getCategories() == null){
					company.setCategories(new ArrayList<>());
				}
				
				Category cat = company.getCategories().stream()
						.filter(p -> p.getId().equals(service.getCategory().getId()))
						.findFirst()
						.orElse(null);
				
				
				if(cat == null){
					company.getCategories().add(service.getCategory());
					update = true;
				}
			}
			
			if(update){
				companyDAO.update(company);
			}
			
	}



	@Override
	public CompanyApp load(String idCompany) throws Exception {
		
		CompanyApp companyApp = null;
		

			companyApp = new CompanyApp();
			
			Company company = companyDAO.retrieve(new Company(idCompany));

			if(company == null){
				throw new BusinessException("company_not_found");
			}
			
			companyApp.setAddress(AddressUtils.textualAddress(company.getAddressInfo()));
			
			companyApp.setBusinessHoursDesc(company.getBusinessHoursDesc());
			
			companyApp.setDesc(company.getDesc());
			
			companyApp.setFantasyName(company.getFantasyName());
			
			List<Service> featuredServices = serviceService.listFeaturedServices(idCompany);
			companyApp.setFeaturedServices(featuredServices);
			
			companyApp.setGallery(company.getGallery());
			
			companyApp.setId(company.getId());
			
			companyApp.setLatitude(company.getAddressInfo().getLatitude());
			
			companyApp.setLongitude(company.getAddressInfo().getLongitude());
			
			companyApp.setRating(company.getRating());
			
			List<Service> services = serviceService.listAllStoreServices(idCompany, true);
			companyApp.setServices(services);
			

		return companyApp;
	}

	@Override
	public List<CompanyCard> listByIdParent(String idParent) throws Exception {

		List<CompanyCard> list = null;

		SearchBuilder builder = new SearchBuilder();
		builder.appendParam("idParent", idParent);

		Sort sort = new Sort(new SortField("fantasyName", SortField.Type.DOC, true));
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

//


}
