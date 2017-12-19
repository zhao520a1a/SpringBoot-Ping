package com.springboot.ping.demo.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.ping.demo.domain.User;
import com.springboot.ping.demo.domain.dao.UserDao;
import com.springboot.ping.mybatis.extend.service.BaseCURDService;

@Service
public class UserService extends BaseCURDService<User, UserDao> {
	@Autowired
	private UserDao userDao;
	public List<String> findUsernames(){
		return this.userDao.findUsernames();
	}
}
