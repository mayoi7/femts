package com.xidian.femts.entity;

import com.xidian.femts.constants.UserState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table ( name ="user" , schema = "")
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

	private static final long serialVersionUID =  4557493787299247218L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   	@Column(name = "id" )
	private Long id;

	/**
	 * 用户名，可以为中文（建议为员工名称+编号的形式）
	 */
   	@Column(name = "username" )
	private String username;

   	@Column(name = "password" )
	private String password;

	/**
	 * 工号
	 */
   	@Column(name = "job_id" )
	private Long jobId;

   	@Column(name = "email" )
	private String email;

   	@Column(name = "phone" )
	private String phone;

	/**
	 * 用户状态，0：被锁定；1：未激活；2：普通用户；3：普通管理员；4：超级管理员
	 */
   	@Column(name = "state" )
	@Builder.Default
	private UserState state = UserState.INACTIVATED;

	/**
	 * 权限级别（0...N：级别依次递增（权限级别等同于公司内部管理级别））
	 */
	@Column(name = "level" )
	@Builder.Default
	private Integer level = 0;

   	@Column(name = "created_at" )
	private Date createdAt;

   	@Column(name = "modified_at" )
	private Date modifiedAt;

	@Override
	public String toString() {
		return "{" +
					"\"id\":" + id +
					",\"username\":\"" + username +
					"\",\"password\":\"" + password +
					"\",\"jobId\":\"" + jobId +
					"\",\"email\":\"" + email +
					"\",\"phone\"\":" + phone +
					"\",\"state\"\":" + state +
					"\",\"createdAt\":" + createdAt +
					",\"modifiedAt\":" + modifiedAt +
				'}';
	}

}
