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

/**
 * 文档浏览和下载次数记录表
 *
 * @date 16:56:18 2020-02-04 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table( name ="record", schema = "")
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Record implements Serializable {

	private static final long serialVersionUID =  810299737331480227L;

	/**
	 * 文档id
	 */
	@Id
   	@Column(name = "id" )
	private Long id;

	/**
	 * 浏览次数
	 */
   	@Column(name = "browse" )
	private Integer browse;

	/**
	 * 下载次数
	 */
   	@Column(name = "download" )
	private Integer download;


	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"browse='" + browse + '\'' +
					"download='" + download + '\'' +
				'}';
	}

}
