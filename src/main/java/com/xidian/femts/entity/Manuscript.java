package com.xidian.femts.entity;

import com.xidian.femts.constants.FileType;
import com.xidian.femts.constants.SecurityLevel;
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
 * 文档表
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Data
@Table ( name ="manuscript" , schema = "")
@Builder
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Manuscript implements Serializable {

	private static final long serialVersionUID =  6564372990944440054L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
   	@Column(name = "id")
	private Long id;
	
	/** 文稿标题 */
	@Column(name = "title")
	private String title;

   	@Column(name = "content_id")
	private Long contentId;

	/**
	 * fastdfs中的文件id
	 */
	@Column(name = "file_id")
	private String fileId;

	/**
	 * 文件格式类型
	 */
	@Column(name = "type")
	private FileType type;

	/**
	 * 文档权限级别（0：仅自己可见；1：仅上级和自己可见；2：所有人可见）
	 */
   	@Column(name = "level")
	@Builder.Default
	private SecurityLevel level = SecurityLevel.PUBLIC;

   	@Column(name = "created_by")
	private Long createdBy;

   	@Column(name = "created_at")
	private Date createdAt;

   	@Column(name = "modified_by")
	private Long modifiedBy;

   	@Column(name = "modified_at")
	private Date modifiedAt;

	@Override
	public String toString() {
		return "{" +
					"id='" + id + '\'' +
					"contentId='" + contentId + '\'' +
					"fileId='" + fileId + '\'' +
					"extName='" + type + '\'' +
					"level='" + level + '\'' +
					"createdBy='" + createdBy + '\'' +
					"createdAt='" + createdAt + '\'' +
					"modifiedBy='" + modifiedBy + '\'' +
					"modifiedAt='" + modifiedAt + '\'' +
				'}';
	}

}
