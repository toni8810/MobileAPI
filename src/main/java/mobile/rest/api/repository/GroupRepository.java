package mobile.rest.api.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import mobile.rest.api.domain.Group;

public interface GroupRepository extends CrudRepository<Group, String> {
	public Iterable<Group> findByUsername(String username);
	
	@Modifying
	@Query("update Group g set g.groupName = ?2 where g.groupName = ?1")
	public int updateGroupName(String oldGroupName, String newGroupName);
	
}
