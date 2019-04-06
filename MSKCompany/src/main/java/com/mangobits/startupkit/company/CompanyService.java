package com.mangobits.startupkit.company;

import com.mangobits.startupkit.user.UserCard;

import javax.ejb.Local;
import java.util.List;


@Local
public interface CompanyService{

	List<Company> listAll() throws Exception;
	
	
	List<Company> listActives() throws Exception;
	
	
	@Deprecated
	void save(Company company) throws Exception;
	
	
	Company retrieve(String id) throws Exception;
	
	
	Company retrieveByCode(String code) throws Exception;
	
	
	String pathFilesCompany(String idCompany) throws Exception;
	
	
	void addPhoto(String idCompany, String idPhoto) throws Exception;
	
	
	void removePhoto(String idCompany, String idPhoto) throws Exception;
	
	
	void changeStatus(String idCompany) throws Exception;
	
	
//	void changeStatusService(String idService) throws Exception;
	
	
	List<CompanyCard> search(CompanySearch companySearch) throws Exception;
	
	
//	List<SaleOff> mainSalesOff(CompanySearch companySearch) throws Exception;
	
	
	CompanyCard createCompanyCard(Company company) throws Exception;
	
	
	CompanyCard createCompanyCard(String idCompany) throws Exception;
	
	
//	List<UserCard> listPros(String idService) throws Exception;
	
	
//	void processService(Service service) throws Exception;
	
	
	CompanyApp load(String idCompany) throws Exception;


	void saveCompany(Company company) throws Exception;


	List<CompanyCard> listActiveCards() throws Exception;


	List<CompanyCard> listByIdParent(String idParent) throws Exception;


	Company loadByField(String field, String value) throws Exception;
}
