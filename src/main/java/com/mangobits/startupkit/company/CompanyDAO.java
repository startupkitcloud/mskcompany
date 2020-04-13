package com.mangobits.startupkit.company;

import com.mangobits.startupkit.core.dao.AbstractDAO;

public class CompanyDAO extends AbstractDAO<Company> {
	

	public CompanyDAO() {
		super(Company.class);
	}


	@Override
	public Object getId(Company obj) {
		return obj.getId();
	}
}