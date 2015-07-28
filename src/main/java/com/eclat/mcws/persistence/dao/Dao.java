package com.eclat.mcws.persistence.dao;

import java.util.List;

import com.eclat.mcws.persistence.entity.Entity;

public interface Dao<T extends Entity, I> {

	List<T> findAll();

	T find(I id);

	T save(T newsEntry);
	
	Boolean saveAll(List<T> newsEntries);

	void delete(I id);
	

}
