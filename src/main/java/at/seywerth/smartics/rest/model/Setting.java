package at.seywerth.smartics.rest.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 * entity for accessing settings.
 * 
 * @author Raphael Seywerth
 *
 */
@Entity
@Table(name = "setting")
public class Setting implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(nullable = false, unique = true)
	private String name;

	private Timestamp updateTime;
	private String value;
	private String description;

	@SuppressWarnings("unused")
	private Setting() {
	}

	public Setting(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	@PrePersist
	@PreUpdate
	private void preUpdate() {
		this.updateTime = new Timestamp(System.currentTimeMillis());
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Setting other = (Setting) obj;
		if (name == null) {
			if (other.name != null)	return false;
		} else {
			if (!name.equals(other.name)) return false;
		}
		if (value == null) {
			if (other.value != null) return false;
		} else {
			if (!value.equals(other.value)) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Settings [name=" + name + ", value=" + value + ", description="
				+ description + "]";
	}

}