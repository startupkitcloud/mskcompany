package org.startupkit.company;

import org.startupkit.core.dao.AbstractDAO;

public class CompanyDAO extends AbstractDAO<Company> {
	

	public CompanyDAO() {
		super(Company.class);
	}


	@Override
	public Object getId(Company obj) {
		return obj.getId();
	}
}