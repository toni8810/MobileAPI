package mobile.rest.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mobile.rest.api.domain.User;
import mobile.rest.api.repository.GroupMembersRepository;
import mobile.rest.api.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRep;
	@Autowired
	GroupMembersRepository groupMembersRepository;
	
	public void signUpUser(User u) {
		userRep.save(u);
	}
	public boolean checkIfUserExists(User u) {
		return userRep.exists(u.getUsername());
	}
	public boolean checkUserCredentials(User u) {
		User userInDatabase = userRep.findOne(u.getUsername());
		if (userInDatabase.getPassword().equals(u.getPassword())) return true;
		else return false;
	}
	public List<User> getAllUsers() {
		List<User> returnList = new ArrayList<>();
		userRep.findAll().forEach(returnList::add);
		return returnList;
	}
	public List<User> getUsersByGroupName(String groupName) {
		List<String> usernames = new ArrayList<>();
		List<User> returnList = new ArrayList<>();
		groupMembersRepository.findByGck_groupName(groupName).forEach(groupMember -> usernames.add(groupMember.getGck().getUsername()));
		usernames.forEach(username -> {
			returnList.add(userRep.findOne(username));
		});
		return returnList;
		
	}
}
