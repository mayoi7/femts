package com.xidian.femts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 文档需要显示的的html内容
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table ( name ="content" , schema = "")
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Content implements Serializable {

	private static final long serialVersionUID =  4169884643757259108L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   	@Column(name = "id" )
	private Long id;

   	@Column(name = "content" )
	private String content;

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"content='" + content + '\'' +
				'}';
	}

}
