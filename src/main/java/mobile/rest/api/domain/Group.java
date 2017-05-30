package mobile.rest.api.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="groups")
public class Group {
	
	@Id
	@Length(min=5, max=100)
	@NotEmpty
	@Pattern(regexp="^[^.]*$")
	private String groupName;
	private String username;
	
	public Group(String groupName, String username) {
		this.groupName = groupName;
		this.username = username;
	}
	
	public Group() {
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
