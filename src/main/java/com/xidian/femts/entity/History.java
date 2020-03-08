package com.xidian.femts.entity;

import com.xidian.femts.constants.OptionType;
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
 * 操作记录表
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table ( name ="record" , schema = "")
@Data
@Builder
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class History implements Serializable {

	private static final long serialVersionUID =  7997783229530433432L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   	@Column(name = "id" )
	private Long id;

	/**
	 * 用户id
	 */
   	@Column(name = "user_id" )
	private Long userId;

   	@Column(name = "time" )
	private Date time;

	/**
	 * 0：创建；1：更新；2：删除；3：下载；4：上传
	 */
   	@Column(name = "option_type" )
	private OptionType optionType;

	/**
	 * 操作对象id（即文档id）
	 */
   	@Column(name = "object_id" )
	private Long objectId;

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"userId='" + userId + '\'' +
					"time='" + time + '\'' +
					"optionType='" + optionType + '\'' +
					"optionId='" + objectId + '\'' +
				'}';
	}

}
