package mobile.rest.api.controller;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mobile.rest.api.domain.GroupMembers;
import mobile.rest.api.domain.GroupCompositeKey;
import mobile.rest.api.domain.Group;
import mobile.rest.api.domain.User;
import mobile.rest.api.service.GroupService;
import mobile.rest.api.service.UserService;

@CrossOrigin
@RestController
@Transactional
public class UserController {
	
	@Autowired
	private UserService us;
	@Autowired
	private GroupService gs;
	
	@RequestMapping(value="/users", method=RequestMethod.POST)
	public ResponseEntity<HttpStatus> signUpUser(@RequestParam(name="group_name") String groupName, @RequestParam String username, @RequestParam String password) {
		
		User u = new User(username,password);
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		if (!validator.validate(u).isEmpty()) {
			//400:Bad Request Validation Error
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!groupName.isEmpty()) {
			GroupMembers g = new GroupMembers();
			g.setGck(new GroupCompositeKey(groupName, username));
			g.setConfirmed(true);
			if (!validator.validate(g.getGck()).isEmpty()) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			else {
				if (!gs.createGroup(g)) return new ResponseEntity<>(HttpStatus.CONFLICT);
				else {
					Group go = new Group(groupName,username);
					gs.setGroupOwner(go);
				}
			}
			
		}
		if (us.checkIfUserExists(u)) {
			//409: User already exist
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		else {
			us.signUpUser(u);
		}
		//201: Created
		return new ResponseEntity<>(HttpStatus.CREATED); 
	}
	
	@RequestMapping(value="/users/login", method=RequestMethod.POST)
	public ResponseEntity<Boolean> login(@Valid User u, BindingResult br) {
		
		if (br.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
		}
		
		if (!us.checkIfUserExists(u)) {
			//Code 404
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
		}
		else {
			//202: Accepted
			if (us.checkUserCredentials(u)) return ResponseEntity.status(HttpStatus.ACCEPTED).body(true);
			//401: Unathorized
			else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
		}
	}
	
	@RequestMapping(value="/users", method=RequestMethod.GET)
	public List<User> getAllUsers() {
		return us.getAllUsers();
	}
	@RequestMapping(value="/users/{groupName}")
	public List<User> getUsersByGroupName(@PathVariable String groupName) {
		return us.getUsersByGroupName(groupName);
	}
}
