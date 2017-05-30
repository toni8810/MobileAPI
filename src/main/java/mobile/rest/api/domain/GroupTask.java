package mobile.rest.api.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name="group_tasks")
public class GroupTask {
	
	@EmbeddedId
	private GroupTaskCompositeKey gtck;

	public GroupTask(GroupTaskCompositeKey gtck) {
		this.gtck = gtck;
	}
	public GroupTask() {
	}
	
	public GroupTaskCompositeKey getGtck() {
		return gtck;
	}
	public void setGtck(GroupTaskCompositeKey gtck) {
		this.gtck = gtck;
	}
	
	@Override
	public String toString() {
		return "GroupTask [gtck=" + gtck + "]";
	}
	
	
	
	
	
	
}
