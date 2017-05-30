package mobile.rest.api.controller;
import java.util.ArrayList;
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
import mobile.rest.api.domain.GroupShoppingList;
import mobile.rest.api.domain.GroupShoppingListCompositeKey;
import mobile.rest.api.domain.GroupTask;
import mobile.rest.api.domain.GroupTaskCompositeKey;
import mobile.rest.api.domain.ResponseObject;
import mobile.rest.api.domain.GroupCompositeKey;
import mobile.rest.api.domain.Group;
import mobile.rest.api.service.GroupService;

@CrossOrigin
@RestController
@Transactional
public class GroupController {
	//spring.datasource.username=gigbud5_mobileT
	//spring.datasource.password=mobileT666
	
	@Autowired
	private GroupService gs;
	
	//-------------- GROUP OPERATIONS ---------------------------------
	
	@RequestMapping(value="/groups", method=RequestMethod.POST)
	public ResponseEntity<HttpStatus> createGroup(@Valid GroupCompositeKey gck, BindingResult br) {
		GroupMembers g = new GroupMembers();
		g.setGck(gck);
		g.setConfirmed(true);
		if (br.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (gs.createGroup(g)) {
			gs.setGroupOwner(new Group(g.getGck().getGroup_name(), g.getGck().getUsername()));
			return new ResponseEntity<>(HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
	}
	
	@RequestMapping(value="/groups", method=RequestMethod.GET)
	public List<Group> getAllGroups() {
		return gs.getAllGroups();
	}
	
	@RequestMapping(value="/groups", method=RequestMethod.DELETE)
	public ResponseEntity<HttpStatus> leaveGroup(@Valid GroupCompositeKey gck, BindingResult br) {
		GroupMembers g = new GroupMembers();
		g.setGck(gck);
		
		if (br.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (gs.deleteGroupByGroupObject(g)) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(value="/groups", method=RequestMethod.PUT)
	public ResponseEntity<HttpStatus> setGroupOwner(@Valid Group go, BindingResult br ) {
		if (br.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		//If group does not have an owner OK otherwise Conflict
		if (gs.setGroupOwner(go)) return new ResponseEntity<>(HttpStatus.OK);
		else return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	@RequestMapping(value="/groups", method=RequestMethod.PATCH)
	public ResponseEntity<ResponseObject> updateGroupName(@RequestParam String oldGroupName, @RequestParam String newGroupName, @RequestParam String username) {
		String message = gs.updateGroupName(oldGroupName,newGroupName,username);
		if (message.contains("User with username")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(false, message, 401));
		}
		else if (message.contains("There is no such group as")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false, message, 404));
		}
		else {
			return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true, message, 200));
		}
	}
	
	@RequestMapping(value="/groups/join", method=RequestMethod.POST)
	public ResponseEntity<ResponseObject> joinGroup(@Valid GroupCompositeKey gck, BindingResult br) {
		if (br.hasErrors()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false, "Validation Error", 400));
		}
		String message = gs.joinGroup(gck);
		if (message.contains("No such group as")) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false, message, 404));
		else if (message.contentEquals("Group does not have an owner")) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false, message, 404));
		else if (message.contains("User is already a member of group")) return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObject(false, message, 409));
		else return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true, message, 200));
	}
	
	@RequestMapping(value="/groups/{groupName}", method=RequestMethod.DELETE)
	public ResponseEntity<HttpStatus> deleteGroup(@PathVariable String groupName, @RequestParam String username) {
		if (!username.contentEquals(gs.getOwnerOfGroup(groupName))) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		if (gs.deleteGroup(groupName)) return new ResponseEntity<>(HttpStatus.OK); 
		else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	//Put a / at the end of the url
	@RequestMapping(value="/groups/{username}", method=RequestMethod.GET)
	public List<GroupMembers> getAllGroupsThatTheUserIsIn(@PathVariable String username) {
		return gs.getAllGroupsThatUserIsIn(username);
	}
	
	@RequestMapping(value="/groups/confirm", method=RequestMethod.PUT)
	public ResponseEntity<ResponseObject> confirmGroupJoin(@RequestParam String userThatConfirms, @RequestParam String userToConfirm, @RequestParam String groupName, @RequestParam boolean confirm) {
		String ownerOfGroup = gs.getOwnerOfGroup(groupName);
		if (ownerOfGroup == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false,"Group Does not exist", 404));
		if (ownerOfGroup.contentEquals(userThatConfirms)) {
			GroupMembers g = new GroupMembers();
			g.setGck(new GroupCompositeKey(groupName, userToConfirm));
			g.setConfirmed(confirm);
			String message = gs.confirmJoin(g);
			if (message.contentEquals("User has been successfully confirmed!")) return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true, message, 200));
			else if (message.contentEquals("User has been removed from the group!")) return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(false, message, 200));
			else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false, message, 404));
		}
		//401: User is not the owner of the group
		else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(false, "User is not the owner of the group!", 401));
	}
	
	//------------------------SHOPPING LIST OPERATIONS--------------------------------
	
	@RequestMapping(value="/groups/{groupName}/shopping-list", method=RequestMethod.POST)
	public ResponseEntity<ResponseObject> addItem(@PathVariable String groupName, @RequestParam String username, @RequestParam String item) {
		if (gs.isUserInGroup(groupName, username)) {
			GroupShoppingList gsl = new GroupShoppingList();
			gsl.setGslck(new GroupShoppingListCompositeKey(groupName, item));
			
			//Validate request parameters
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			if (!validator.validate(gsl.getGslck()).isEmpty()) {
				//400:Bad Request Validation Error
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false, "Validation Error", 400));
			}
			
			String message = gs.addItem(gsl);
			if (message.contains("has been successfully added")) return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(true, message, 201));
			else return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObject(false, message, 409));
		}
		//401: User is not the owner of the group
		else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(false, "User is not authorized to add an item as they are not in the group", 401));
	}
	
	@RequestMapping(value="/groups/{groupName}/shopping-list", method=RequestMethod.DELETE)
	public ResponseEntity<ResponseObject> removeItem(@PathVariable String groupName, @RequestParam String username, @RequestParam String item) {
		if (gs.isUserInGroup(groupName, username)) {
			GroupShoppingList gsl = new GroupShoppingList();
			gsl.setGslck(new GroupShoppingListCompositeKey(groupName, item));
			
			//Validate request parameters
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			if (!validator.validate(gsl.getGslck()).isEmpty()) {
				//400:Bad Request Validation Error
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false, "Validation Error", 400));
			}
			
			String message = gs.removeItem(gsl);
			if (message.contains("has been successfully deleted")) return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true, message, 200));
			else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false, message, 404));
		}
		//401: User is not a member of the group
		else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(false, "User is not authorized to remove an item as they are not in the group", 401));
	}
	
	@RequestMapping(value="/groups/{groupName}/shopping-list/{username}", method=RequestMethod.GET)
	public List<String> getItems(@PathVariable String groupName, @PathVariable String username) {
		if (gs.isUserInGroup(groupName, username)) {
			
			List<String> items = gs.getAllItems(groupName);
			return items;
		}
		//401: User is not a member of the group
		else return new ArrayList<String>();
	}
	
	//------------------------------TASK OPERATIONS----------------------------
	
	@RequestMapping(value="/groups/{groupName}/tasks", method=RequestMethod.POST)
	public ResponseEntity<ResponseObject> addTask(@PathVariable String groupName, @RequestParam String username, @RequestParam String task) {
		if (gs.isUserInGroup(groupName, username)) {
			GroupTask gt = new GroupTask();
			gt.setGtck(new GroupTaskCompositeKey(groupName, task));
			
			//Validate request parameters
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			if (!validator.validate(gt.getGtck()).isEmpty()) {
				//400:Bad Request Validation Error
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false, "Validation Error", 400));
			}
			
			String message = gs.addTask(gt);
			if (message.contains("has been successfully added")) return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(true, message, 201));
			else return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseObject(false, message, 409));
		}
		//401: User is not the owner of the group
		else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(false, "User is not authorized to add a task as they are not in the group", 401));
	}
	@RequestMapping(value="/groups/{groupName}/tasks", method=RequestMethod.DELETE)
	public ResponseEntity<ResponseObject> removeTask(@PathVariable String groupName, @RequestParam String username, @RequestParam String task) {
		if (gs.isUserInGroup(groupName, username)) {
			GroupTask gt = new GroupTask();
			gt.setGtck(new GroupTaskCompositeKey(groupName, task));
			
			//Validate request parameters
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			if (!validator.validate(gt.getGtck()).isEmpty()) {
				//400:Bad Request Validation Error
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(false, "Validation Error", 400));
			}
			
			String message = gs.removeTask(gt);
			if (message.contains("has been successfully deleted")) return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject(true, message, 200));
			else return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(false, message, 404));
		}
		//401: User is not the owner of the group
		else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseObject(false, "User is not authorized to remove a task as they are not in the group", 401));
	}
	
	@RequestMapping(value="/groups/{groupName}/tasks/{username}", method=RequestMethod.GET)
	public List<String> getTasks(@PathVariable String groupName, @PathVariable String username) {
		if (gs.isUserInGroup(groupName, username)) {
			
			List<String> items = gs.getAllTasks(groupName);
			return items;
		}
		//401: User is not a member of the group
		else return new ArrayList<String>();
	}
	
	//-----------------------------EMAIL OPERATIONS--------------------------------------
	
	@RequestMapping(value="/groups/invite", method=RequestMethod.POST)
	public ResponseEntity<HttpStatus> emailInvite(@RequestParam String username, @RequestParam String emailTo, @RequestParam String groupName) {
		if (gs.isUserInGroup(groupName, username)) {
			GroupMembers gm = new GroupMembers();
			gm.setGck(new GroupCompositeKey(groupName, username));
			
			//Validate request parameters
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			if (!validator.validate(gm.getGck()).isEmpty()) {
				//400:Bad Request Validation Error
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			if (gs.sendInvite(username, emailTo, groupName)) {
				return new ResponseEntity<>(HttpStatus.OK);
			}
			else {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		//401: User is not in the group
		else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
	}
}
