package com.model.tool;

import javax.persistence.Column;
import javax.persistence.Id;
import com.model.page.PageDto;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @功能说明：文件存储信息
 * @作者： nfs
 * @创建日期：2019-10-25
 */
public class FileManager extends PageDto implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	//字段
	private Date time;//时间
	private String os;//操作系统
	private int pid;//父级目录ID
	private String path;//路径2
	private String src;//路径
	private String type;//目录类型
	private String name;//目录名称
	private int id;//主键ID
	
	//构造方法
	public FileManager() {
	}
	
	//get和set方法
	
	@Column( name = "TIME"  , length = 11  )
	public Date getTime() {
		return  time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	
	@Column( name = "OS"  , length = 20  )
	public String getOs() {
		return  os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	
	@Column( name = "PID"  , length = 22  )
	public int getPid() {
		return  pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	
	@Column( name = "PATH"  , length = 255  )
	public String getPath() {
		return  path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	@Column( name = "SRC"  , length = 255  )
	public String getSrc() {
		return  src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	
	@Column( name = "TYPE"  , length = 20  )
	public String getType() {
		return  type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	@Column( name = "NAME"  , length = 255  )
	public String getName() {
		return  name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	@Id @Column( name = "ID" , unique = true  , length = 22  )
	public int getId() {
		return  id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
}
