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
 * 文档目录表<br>
 * 文档系统结构：<br/>
 *     目录<br/>
 *     &emsp;|-- 文档<br/>
 *     &emsp;|-- 目录<br/>
 *     &emsp;&emsp;&emsp;|-- 文档
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table ( name ="directory" , schema = "")
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Directory implements Serializable {

	private static final long serialVersionUID =  8969659038164582447L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   	@Column(name = "id" )
	private Long id;

   	@Column(name = "name" )
	private String name;

   	@Column(name = "parent" )
	private Long parent;

	/**
	 * 下级目录id列表，以逗号分隔
	 */
   	@Column(name = "subs" )
	private String subs;

	/**
	 * 当前目录下的文档id列表
	 */
   	@Column(name = "docs" )
	private String docs;

	/**
	 * 目录是否公开，true：公开。<br/>
	 * 区别于文档安全级别，当目录设置为不公开时，目录下的所有文档安全级别无论设置为什么，
	 * 均对目录所属人之外不可见；
	 * 当目录设置为公开时，则遵循文档及目录的可视级别来进行判断
	 */
	@Column(name = "visible")
	private Boolean visible;

	/** 目录创建人id */
	@Column(name = "created_by")
	private Long createdBy;

	/** 目录创建时间 */
	@Column(name = "created_at")
	private Date createdAt;
}
