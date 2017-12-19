package com.springboot.ping.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.ping.demo.domain.User;
import com.springboot.ping.demo.service.UserService;
import com.springboot.ping.mybatis.vo.Pagination;

@RestController
@RequestMapping(value="/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/findById",method={RequestMethod.POST,RequestMethod.GET})
	public User findById(User user){
		return this.userService.findByPk(user);
	}
	
	@RequestMapping(value="/findUsernames",method={RequestMethod.POST,RequestMethod.GET})
	public List<String> findUsernames(){
		return this.userService.findUsernames();
	}
	
	@RequestMapping(value="/findAll",method={RequestMethod.POST,RequestMethod.GET})
	public List<User> findAll(){
		return this.userService.findAll();
	}
	
	@RequestMapping(value="/findPaginate/{pageSize}/{currentPage}",method={RequestMethod.POST,RequestMethod.GET})
	public List<User> findPaginate(@PathVariable("pageSize")Integer pageSize,@PathVariable("currentPage")Integer currentPage){
		return this.userService.find(new Pagination(pageSize,currentPage)).getRows();
	}
	
	@RequestMapping(value="/add",method={RequestMethod.POST,RequestMethod.GET})
	public Object add(User user){
		this.userService.save(user);
		return "success!!";
	}
	
	@RequestMapping(value="/addBatch",method={RequestMethod.POST,RequestMethod.GET})
	public Object addBatch(){
		List<User> users = new ArrayList<>();
		for(int i=0;i<5;i++){
			users.add(User.builder().username("小"+i).age(i).build());
		}
		this.userService.saveBatch(users);
		return "success!!";
	}
	
	@RequestMapping(value="/update",method={RequestMethod.POST,RequestMethod.GET})
	public Object update(User user){
//		user.setCreate_time(this.userService.findByPk(user).getCreate_time());
		this.userService.update(user);
		return "success!!";
	}
	
	@RequestMapping(value="/delete",method={RequestMethod.POST,RequestMethod.GET})
	public Object delete(User user){
		this.userService.deleteByPk(user);
		return "success!!";
	}
}
