package mobile.rest.api.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Embeddable
public class GroupCompositeKey implements Serializable {

	private static final long serialVersionUID = 5920684881386373193L;
	@Length(min=5, max=100)
	@NotEmpty
	@Pattern(regexp="^[^.]*$")
	private String groupName;
	@Email
	@NotEmpty
	private String username;
	
	
	
	public GroupCompositeKey() {
	}

	public GroupCompositeKey(String group_name, String username) {
		this.groupName = group_name;
		this.username = username;
	}
	
	public String getGroup_name() {
		return groupName;
	}
	public void setGroup_name(String group_name) {
		this.groupName = group_name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "GroupCompositeKey [group_name=" + groupName + ", username=" + username + "]";
	}
	
	
}
