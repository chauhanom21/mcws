package com.eclat.mcws.util;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;

import com.eclat.mcws.common.logger.Log;

@Component
public class CacheManager {

	@Log
	private Logger logger;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * Evicts all second level cache hibernate entities. This is generally only
	 * needed when an external application modifies the main database.
	 */
	public void evict2ndLevelCache() {
	    try {
	        Map<String, ClassMetadata> classesMetadata = sessionFactory.getAllClassMetadata();
	        for (String entityName : classesMetadata.keySet()) {
	            logger.info("Evicting Entity from 2nd level cache: ", entityName);
	            sessionFactory.getCache().evictEntityRegion(entityName);
	        }
	    } catch (Exception e) {
	        logger.error("Error evicting 2nd level hibernate cache entities: ", e);
	    }
	}
}
