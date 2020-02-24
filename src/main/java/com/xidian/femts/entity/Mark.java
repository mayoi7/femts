package com.xidian.femts.entity;

import com.xidian.femts.constants.FileType;
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
 * 文件散列标记表
 *
 * @date 19:51:30 2020-01-16 
 * @author  liuhaonan
 * @email  acerola.orion@foxmail.com
 */
@Entity
@Table ( name ="mark" , schema = "")
@Data
@DynamicInsert
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor
public class Mark implements Serializable {

	private static final long serialVersionUID =  1894467589003510429L;

	@Id
   	@Column(name = "manuscript_id" )
	private Long manuscriptId;

	@Column(name = "type" )
	private FileType type;

	/**
	 * 文件散列值
	 */
   	@Column(name = "hash" )
	private String hash;

	@Override
	public String toString() {
		return "{" +
					"manuscriptId='" + manuscriptId + '\'' +
					"type='" + type + '\'' +
					"hash='" + hash + '\'' +
				'}';
	}

}
