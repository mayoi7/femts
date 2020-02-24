package com.xidian.femts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 公司管理级别权限表（区别与普通用户、管理员这样的级别）
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table ( name ="permission" , schema = "")
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Permission implements Serializable {

	private static final long serialVersionUID =  3234242532203712312L;

	@Id
   	@Column(name = "user_id" )
	private Long userId;

	/**
	 * 权限级别（0...N：级别依次递减（权限级别等同于公司内部管理级别））
	 */
   	@Column(name = "level" )
	private Integer level;

	/**
	 * 授权人id
	 */
   	@Column(name = "modified_by" )
	private Long modifiedBy;

   	@Column(name = "modified_at" )
	private Date modifiedAt;

	@Override
	public String toString() {
		return "{" +
					"userId='" + userId + '\'' +
					"level='" + level + '\'' +
					"modifiedBy='" + modifiedBy + '\'' +
					"modifiedAt='" + modifiedAt + '\'' +
				'}';
	}

}
