package mobile.rest.api.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import mobile.rest.api.domain.GroupMembers;
import mobile.rest.api.domain.GroupCompositeKey;

public interface GroupMembersRepository extends CrudRepository<GroupMembers, GroupCompositeKey> {
	public int deleteByGck_groupName(String groupName);
	public Iterable<GroupMembers> findByGck_username(String username);
	public Iterable<GroupMembers> findByGck_groupName(String groupName);
	
	@Modifying
	@Query("update GroupMembers g set g.gck.groupName = ?2 where g.gck.groupName = ?1")
	public int updateGroupName(String oldGroupName, String newGroupName);
}
