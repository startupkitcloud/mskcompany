package com.mangobits.startupkit.company;

import com.mangobits.startupkit.core.utils.AbstractDAO;

public class CompanyDAO extends AbstractDAO<Company>{
	

	public CompanyDAO() {
		super(Company.class);
	}


	@Override
	protected Object getId(Company obj) {
		return obj.getId();
	}
}