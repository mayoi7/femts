package com.xidian.femts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 登陆记录表
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table ( name ="login" , schema = "")
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Login implements Serializable {

	private static final long serialVersionUID =  8128821950618616911L;

	/**
	 * INT范围可以容纳10000人的公司每人每天登陆20次持续30年的记录
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   	@Column(name = "id" )
	private Long id;

	/**
	 * 用户id
	 */
   	@Column(name = "user_id" )
	private Long userId;

   	@Column(name = "ip" )
	private String ip;

   	@Column(name = "time" )
	private Date time;

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"userId='" + userId + '\'' +
					"ip='" + ip + '\'' +
					"time='" + time + '\'' +
				'}';
	}

}
