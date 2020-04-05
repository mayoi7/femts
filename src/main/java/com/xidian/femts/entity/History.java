package com.xidian.femts.entity;

import com.xidian.femts.constants.Operation;
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
@Table ( name ="history" , schema = "")
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

	/**
	 * 0：创建；1：更新；2：删除；3：下载；4：上传
	 */
   	@Column(name = "operation" )
	private Operation operation;

   	/** 操作类型，false：用户，true：文档 */
   	private Boolean type;

	/**
	 * 操作对象id
	 */
	@Column(name = "object_id" )
	private Long objectId;

   	/** 备注信息，当OptionType为5时表示修改前后的数据差别 */
   	@Column(name = "remark")
	private String remark;

	@Column(name = "time" )
	private Date time;


}
