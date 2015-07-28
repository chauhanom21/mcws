package com.eclat.mcws.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.eclat.mcws.common.logger.Log;
import com.eclat.mcws.dto.UserProfile;
import com.eclat.mcws.persistence.entity.Users;
import com.eclat.mcws.service.UserService;
import com.eclat.mcws.util.rest.Response;
import com.eclat.mcws.util.rest.Response.ResponseStatus;
import com.eclat.mcws.util.rest.ResponseBuilder;

@Controller
@RequestMapping("/user")
public class UserController {

	private static final String USER_PROFILE = "userProfile";
	@Log
	private Logger logger;
	
	@Autowired
	private UserService userService;

	@Autowired
	@Qualifier("sessionRegistry")
	private SessionRegistry sessionRegistry;
	
	/**
	 * 
	 * @param request
	 * @return UserProfile
	 */
	@RequestMapping( value = "/userdetails", method = RequestMethod.GET )
	@ResponseBody
	public Response<UserProfile> loadUserDetails(HttpServletRequest request) {
		Response<UserProfile> response = ResponseBuilder.buildOkResponse();
		try {
			final UserProfile profile = userService.getUserProfile();
			if(profile != null) {
				response.setPayLoad(profile);
			} else {
				response.setStatus(ResponseStatus.ERROR);
				response.setMessage("User's Details Not Found in Session");
			} 
		} catch (Exception e) {
			logger.error("Exception occurred while Loading User Profile ", e);
			response.setStatus(ResponseStatus.ERROR);
			response.setMessage("User Session Expired");
		}
		return response;
	}
	
	@RequestMapping("/accessdenied")
	public ModelAndView accessDenied() {
		return new ModelAndView(new RedirectView("/access_denied.html", true));
	}
	
	@RequestMapping("/login")
	public ModelAndView loginpage() {
		/* Display count of logged-in user*/
		getLoggedinUsersDetail();
		return new ModelAndView(new RedirectView("/login.html", true));
	}
	
	@RequestMapping("/home")
	public ModelAndView getAuditTasks(HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		/* Display count of logged-in user*/
		getLoggedinUsersDetail();
		
		/**
		 * Remove UserProfile object from session if session invalidated.
		 */
		if(session.getAttribute(USER_PROFILE) != null){
			session.removeAttribute(USER_PROFILE);
		}

		/**
		 * Once User successfully logged-in setting UserProfile instance to session
		 * This instance uses for further communication.
		 */
		final UserProfile profile = userService.getUserProfile();
		if(profile != null) {
			session.setAttribute(USER_PROFILE, profile);
		}
		final Users user = userService.getLoggedInUserDetails();
		return getUserHomePageByRole(user);
	}

	/**
	 * 
	 * @param user
	 * @return Logged-in user home page
	 */
	private ModelAndView getUserHomePageByRole(final Users user) {
		if(user != null) {
			final String role = user.getUserRole().getRoles();
			
			if (role.equalsIgnoreCase("coder")) {

				return new ModelAndView(new RedirectView("/coderhome.html", true));

			} else if (role.equalsIgnoreCase("qa")) {

				return new ModelAndView(new RedirectView("/qahome.html", true));

			} else if (role.equalsIgnoreCase("supervisor")) {

				return new ModelAndView(new RedirectView("/supervisorhome.html", true));

			} else if (role.equalsIgnoreCase("admin")) {

				return new ModelAndView(new RedirectView("/admin_home.html", true));
			}
		}

		return new ModelAndView(new RedirectView("/index.html", true));
	}
	
	private void getLoggedinUsersDetail(){
		List<String> usersNamesList = new ArrayList<String>();
		List<Object> principals = sessionRegistry.getAllPrincipals();
		logger.info("#########################################");
		for (Object principal: principals) {
		    if (principal instanceof User) {
		        usersNamesList.add(((User) principal).getUsername());
		        logger.info(":::::" + ((User) principal).getUsername());
		    }
		    
		}
		logger.info(" Total Logged-in Users :::" + usersNamesList.size());
		logger.info("########################################");
	}
	
}
