package mobile.rest.api.repository;

import org.springframework.data.repository.CrudRepository;

import mobile.rest.api.domain.GroupTask;
import mobile.rest.api.domain.GroupTaskCompositeKey;

public interface GroupTaskRepository extends CrudRepository<GroupTask, GroupTaskCompositeKey> {
	public Iterable<GroupTask> findByGtck_groupName(String groupName);
}
