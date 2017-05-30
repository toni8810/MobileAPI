package mobile.rest.api.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Embeddable
public class GroupShoppingListCompositeKey implements Serializable {

	private static final long serialVersionUID = -7763435426185456118L;
	
	@Length(min=5, max=100)
	@NotEmpty
	@Pattern(regexp="^[^.]*$")
	private String groupName;
	@Length(min=2, max=100)
	@NotEmpty
	private String item;
	
	public GroupShoppingListCompositeKey(String groupName, String item) {
		this.groupName = groupName;
		this.item = item;
	}
	public GroupShoppingListCompositeKey() {
	}

	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	@Override
	public String toString() {
		return "GroupShoppingListCompositeKey [groupName=" + groupName + ", item=" + item + "]";
	}
	
	

}
