package mobile.rest.api.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Embeddable
public class GroupTaskCompositeKey implements Serializable {
	
	private static final long serialVersionUID = -3649232785506523131L;
	
	@Length(min=5, max=100)
	@NotEmpty
	@Pattern(regexp="^[^.]*$")
	private String groupName;
	@Length(min=5, max=200)
	@NotEmpty
	private String task;
	
	public GroupTaskCompositeKey(String groupName, String task) {
		this.groupName = groupName;
		this.task = task;
	}
	public GroupTaskCompositeKey() {
	}
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	@Override
	public String toString() {
		return "GroupTaskCompositeKey [groupName=" + groupName + ", task=" + task + "]";
	}
	
	
	
	
	
}
