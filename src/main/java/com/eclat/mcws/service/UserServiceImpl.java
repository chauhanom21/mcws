package com.eclat.mcws.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eclat.mcws.dto.CodersQADto;
import com.eclat.mcws.dto.UserDetails;
import com.eclat.mcws.dto.UserProfile;
import com.eclat.mcws.enums.UserRoles;
import com.eclat.mcws.persistence.dao.CoderDao;
import com.eclat.mcws.persistence.dao.UserDAO;
import com.eclat.mcws.persistence.entity.Coders;
import com.eclat.mcws.persistence.entity.UserRole;
import com.eclat.mcws.persistence.entity.Users;
import com.eclat.mcws.util.CommonOperations;
import com.eclat.mcws.util.DecimalFormatUtils;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

	private final double ZERO = 0.0;

	@Autowired
	private UserDAO userDao;

	@Autowired
	private CoderDao coderDao;

	public UserProfile getUserProfile() {
		UserProfile userProfile = null;
		final Users users = getLoggedInUserDetails();
		if (users != null) {
			LOG.debug(" User Details Found In Session : " + users.getUsername());
			userProfile = new UserProfile();
			userProfile.setUserId(users.getId());
			userProfile.setName(users.getFirstname() + " " + users.getLastname());

			final Coders coderQA = coderDao.find(users.getId());
			if (coderQA != null) {
				final String role = CommonOperations.findRoleOfUser(coderDao.find(users.getId()));
				userProfile.setUserRole(role);
				Double remWorkload = 0.0;
				
				if(role.equals(UserRoles.Coder.toString())) {
					 remWorkload = coderQA.getCoderDailyWorkLoad() - coderQA.getCompleteLoad();
				} else {
					 remWorkload = coderQA.getCoderMaxWorkLoad() - coderQA.getCompleteLoad();
				}
				
				if (remWorkload > ZERO) {
					userProfile.setRemWorkload(DecimalFormatUtils.dfNum.format(remWorkload));
				} else {
					userProfile.setRemWorkload("0.0");
				}
			} else {
				userProfile.setUserRole(users.getUserRole().getRoles());
				userProfile.setRemWorkload("0.0");
			}
		}
		return userProfile;
	}

	public Users getLoggedInUserDetails() {
		Users userDetails = null;

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final Object myUser = (auth != null) ? auth.getPrincipal() : null;

		if (myUser instanceof org.springframework.security.core.userdetails.User) {
			final org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) myUser;

			userDetails = userDao.loadUserByUsername(user.getUsername());

		}
		return userDetails;
	}

	public Boolean updateUserAvailability(final CodersQADto coderQa) {
		final Integer id = coderQa.getId();
		if (id != null) {
			final String available = coderQa.getAvailable();
			boolean isAvailable = false;
			if (available != null && available.equals("Yes")) {
				isAvailable = true;
			}
			try {
				return userDao.updateUserAvailability(id, isAvailable);
			} catch (final Exception e) {
				LOG.error(" Exp on updateUserAvailability : ", e);
				return false;
			}
		}
		return false;
	}
}
