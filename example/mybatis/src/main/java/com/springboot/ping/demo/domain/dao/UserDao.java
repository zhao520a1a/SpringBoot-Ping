package com.springboot.ping.demo.domain.dao;


import java.util.List;

import com.springboot.ping.demo.domain.User;
import com.springboot.ping.mybatis.extend.dao.BaseCURDDao;

public interface UserDao extends BaseCURDDao<User> {
	public List<String> findUsernames();
}
