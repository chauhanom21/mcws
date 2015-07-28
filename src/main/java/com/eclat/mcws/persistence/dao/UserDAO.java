package com.eclat.mcws.persistence.dao;

import java.util.List;

import com.eclat.mcws.persistence.dao.Dao;
import com.eclat.mcws.persistence.entity.UserRole;
import com.eclat.mcws.persistence.entity.Users;

public interface UserDAO extends Dao<Users, Integer> {

	/**
	 * 
	 * @param username
	 * @return
	 */
	public Users loadUserByUsername(final String username);
	
	/**
	 * 
	 * @return
	 */
	public List<Users> getAllUsers();

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public Users getUserById(final int userId);
	
	/**
	 * 
	 * @return
	 */
	public List<Users> getAllAvailableUsers();
	
	/**
	 * 
	 * @param userId
	 * @param isAvailable
	 * @return
	 */
	public Boolean updateUserAvailability(final Integer userId, final Boolean isAvailable);
	
	public Users loadUserById(final Integer userId);

	public UserRole loadUserRoleByUserId(final int userId);
	
	public void saveOrUpdateUserRole(final UserRole userRole);
}
