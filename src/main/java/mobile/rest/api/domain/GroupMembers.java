package mobile.rest.api.domain;


import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="group_members")
public class GroupMembers {
	
	@EmbeddedId
	private GroupCompositeKey gck;
	
	private boolean confirmed;
	
	
	public GroupMembers() {
	}

	public GroupMembers(GroupCompositeKey gck) {
		this.gck = gck;
	}

	public GroupCompositeKey getGck() {
		return gck;
	}

	public void setGck(GroupCompositeKey gck) {
		this.gck = gck;
	}
	
	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	@Override
	public String toString() {
		return "Group [gck=" + gck + ", confirmed=" + confirmed + "]";
	}
	

	
	
}
