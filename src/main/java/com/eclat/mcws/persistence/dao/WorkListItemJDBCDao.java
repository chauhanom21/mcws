package com.eclat.mcws.persistence.dao;

import java.util.List;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.eclat.mcws.persistence.entity.WorkListItem;

public class WorkListItemJDBCDao extends JdbcDaoSupport implements Dao<WorkListItem, Integer> {

	@Override
	public List<WorkListItem> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkListItem find(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkListItem save(WorkListItem newsEntry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean saveAll(List<WorkListItem> newsEntries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}

	
}
