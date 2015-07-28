package com.eclat.mcws.admin.dto;

import java.util.List;

public class UserClientDto {

	private Integer userId;
	private List<Integer> clientIds;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public List<Integer> getClientIds() {
		return clientIds;
	}

	public void setClientIds(List<Integer> clientIds) {
		this.clientIds = clientIds;
	}

}
