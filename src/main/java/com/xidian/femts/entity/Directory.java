package com.xidian.femts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

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

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"name='" + name + '\'' +
					"parent='" + parent + '\'' +
					"subs='" + subs + '\'' +
					"docs='" + docs + '\'' +
				'}';
	}

}
