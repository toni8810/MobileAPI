package mobile.rest.api.repository;

import org.springframework.data.repository.CrudRepository;
import mobile.rest.api.domain.User;

public interface UserRepository extends CrudRepository<User, String> {

}
