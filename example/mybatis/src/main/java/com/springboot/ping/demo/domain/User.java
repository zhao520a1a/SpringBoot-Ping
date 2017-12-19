package com.springboot.ping.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.springboot.ping.mybatis.annotation.AutoIncrement;
import com.springboot.ping.mybatis.annotation.Pk;
import com.springboot.ping.mybatis.annotation.Table;
import com.springboot.ping.mybatis.extend.entity.BaseTimeModel;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper=false)
@Data
@Table("user_test")
public class User extends BaseTimeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4770865098637170624L;
	@AutoIncrement
	@Pk
	private long user_id;
	private String username;
	private Integer age;
}
