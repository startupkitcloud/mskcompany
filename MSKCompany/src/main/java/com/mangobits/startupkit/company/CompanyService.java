package com.mangobits.startupkit.company;

import java.util.List;

import javax.ejb.Local;

import com.mangobits.startupkit.catalogue.saleoff.SaleOff;
import com.mangobits.startupkit.catalogue.service.Service;
import com.mangobits.startupkit.core.exception.ApplicationException;
import com.mangobits.startupkit.core.exception.BusinessException;
import com.mangobits.startupkit.user.UserCard;


@Local
public interface CompanyService{

	List<Company> listAll() throws ApplicationException, BusinessException;
	
	
	List<Company> listActives() throws ApplicationException, BusinessException;
	
	
	void save(Company company) throws ApplicationException, BusinessException;	
	
	
	Company retrieve(String id) throws ApplicationException, BusinessException;
	
	
	Company retrieveByCode(String code) throws BusinessException, ApplicationException;
	
	
	String pathFilesCompany(String idCompany) throws BusinessException, ApplicationException;
	
	
	void addPhoto(String idCompany, String idPhoto) throws BusinessException, ApplicationException;
	
	
	void removePhoto(String idCompany, String idPhoto) throws BusinessException, ApplicationException;
	
	
	void changeStatus(String idCompany) throws BusinessException, ApplicationException;
	
	
	void changeStatusService(String idService) throws BusinessException, ApplicationException;
	
	
	List<CompanyCard> search(CompanySearch companySearch) throws BusinessException, ApplicationException;
	
	
	List<SaleOff> mainSalesOff(CompanySearch companySearch) throws BusinessException, ApplicationException;
	
	
	CompanyCard createCompanyCard(Company company) throws BusinessException, ApplicationException;
	
	
	CompanyCard createCompanyCard(String idCompany) throws BusinessException, ApplicationException;
	
	
	List<UserCard> listPros(String idService) throws BusinessException, ApplicationException;
	
	
	void processService(Service service) throws BusinessException, ApplicationException;
	
	
	CompanyApp load(String idCompany) throws BusinessException, ApplicationException;


	void saveCompany(Company company) throws ApplicationException, BusinessException;
}
