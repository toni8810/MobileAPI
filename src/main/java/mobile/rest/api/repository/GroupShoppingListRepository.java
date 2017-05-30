package mobile.rest.api.repository;


import org.springframework.data.repository.CrudRepository;

import mobile.rest.api.domain.GroupShoppingList;
import mobile.rest.api.domain.GroupShoppingListCompositeKey;

public interface GroupShoppingListRepository extends CrudRepository<GroupShoppingList, GroupShoppingListCompositeKey> {
	public Iterable<GroupShoppingList> findByGslck_groupName(String groupName);
}
