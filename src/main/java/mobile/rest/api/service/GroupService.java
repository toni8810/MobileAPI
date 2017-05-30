package mobile.rest.api.service;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import mobile.rest.api.domain.GroupMembers;
import mobile.rest.api.domain.GroupShoppingList;
import mobile.rest.api.domain.GroupTask;
import mobile.rest.api.domain.GroupCompositeKey;
import mobile.rest.api.domain.Group;
import mobile.rest.api.repository.GroupRepository;
import mobile.rest.api.repository.GroupShoppingListRepository;
import mobile.rest.api.repository.GroupTaskRepository;
import mobile.rest.api.repository.GroupMembersRepository;

@Service
public class GroupService {
	
	@Autowired
	private GroupMembersRepository gmr;
	@Autowired
	private GroupRepository gr;
	@Autowired
	private GroupShoppingListRepository gslr;
	@Autowired
	private GroupTaskRepository gtr;
	
	@Autowired
	private JavaMailSender jms;
	
	
	
	//--------------GROUP SERVICES--------------------------------------
	
	//Return false if group already exists
	public boolean createGroup(GroupMembers g) {
		if (gmr.exists(g.getGck())) {
			return false;
		}
		else {
			gmr.save(g);
			return true;
		}
		
	}
	public List<Group> getAllGroups() {
		List<Group> allGroup = new ArrayList<>();
		gr.findAll().forEach(allGroup::add);
		return allGroup;
	}
	public boolean deleteGroup(String groupName) {
		//if 0 row has been deleted return false as group does not exist
		if (gmr.deleteByGck_groupName(groupName) == 0) {
			return false;
		}
		else {
			gr.delete(groupName);
			return true;
		}
	}
	public boolean deleteGroupByGroupObject(GroupMembers g) {
		int numOfGroupMembers = 0;
		if (gmr.exists(g.getGck())) {
			gmr.delete(g.getGck());
			//Check if there is any more users in the group
			Iterator<GroupMembers> i = gmr.findByGck_groupName(g.getGck().getGroup_name()).iterator();
			while(i.hasNext()) {
				i.next();
				numOfGroupMembers++;
			}
			//Check if user owns the group they are leaving
			Group go = isUserOwnGroup(g); 
			if (go != null) {
				//If so
				removeGroupOwner(go);
				if (numOfGroupMembers == 0) {
					//If there is not
					gr.delete(g.getGck().getGroup_name());
				}
			}
			else {
				if (numOfGroupMembers == 0) {
					//If there is not
					gr.delete(g.getGck().getGroup_name());
				}
			}
			
			return true;
		}
		else {
			return false;
		}
	}
	
	public List<GroupMembers> getAllGroupsThatUserIsIn(String username) {
		List<GroupMembers> returnList = new ArrayList<>();
		gmr.findByGck_username(username).forEach(returnList::add);
		return returnList;
	}
	
	public boolean setGroupOwner(Group go) {
		Group currentGroupOwner;
		currentGroupOwner = gr.findOne(go.getGroupName());
		if ((currentGroupOwner == null) || (currentGroupOwner.getUsername() == null)) {
			gr.save(go);
			GroupMembers g = new GroupMembers();
			g.setConfirmed(true);
			g.setGck(new GroupCompositeKey(go.getGroupName(), go.getUsername()));
			gmr.save(g);
			return true;
		}
		else {
			return false;
		}
		
	}
	public String joinGroup(GroupCompositeKey gck) {
		//Check if the group has owner
		Group currentGroupOwner = gr.findOne(gck.getGroup_name());
		if (currentGroupOwner == null) {
			return "No such group as "+gck.getGroup_name();
		}
		else {
			if (currentGroupOwner.getUsername() == null) {
				return "Group does not have an owner";
			}
			else {
				GroupMembers g = new GroupMembers();
				g.setGck(gck);
				g.setConfirmed(false);
				if (gmr.exists(gck)) return "User is already a member of group "+gck.getGroup_name();
				gmr.save(g);
				return "User successfully joined the group";
			}
		}
	}
	public String getOwnerOfGroup(String groupName) {
		Group go = gr.findOne(groupName);
		if (go != null) {
			return go.getUsername();
		}
		else {
			return null;
		}
		
	}
	public String confirmJoin(GroupMembers g) {
		if (gmr.exists(g.getGck())) {
			if (g.isConfirmed()) {
				gmr.save(g);
				return "User has been successfully confirmed!";
			}
			else {
				gmr.delete(g.getGck());
				return "User has been removed from the group!";
			}
		}
		else {
			return "User is not in the group!";
		}
		
	}
	public String updateGroupName(String oldGroupName, String newGroupName, String username) {
		Group go = gr.findOne(oldGroupName);
		if (go == null) return "There is no such group as "+oldGroupName;
		if (!go.getUsername().contentEquals(username)) return "User with username "+username+" is not authorized to change the group's name";
		gmr.updateGroupName(oldGroupName, newGroupName);
		gr.updateGroupName(oldGroupName, newGroupName);
		return "Groupname successfully changed from "+oldGroupName+" to "+newGroupName;
		
	}
	public boolean isUserInGroup(String groupName, String username) {
		List<GroupMembers> groups = getAllGroupsThatUserIsIn(username);
		for(GroupMembers gm: groups) {
			if (gm.getGck().getGroup_name().contentEquals(groupName)) return true;
		}
		return false;
	}
	
	//------------------------SHOPPING LIST SERVICES-------------------------------
	
	public String addItem(GroupShoppingList gsl) {
		if (gslr.exists(gsl.getGslck())) {
			return "Item "+gsl.getGslck().getItem()+" has already been added";
		}
		else {
			gslr.save(gsl);
			return "Item "+gsl.getGslck().getItem()+" has been successfully added";
		}
	}
	
	public String removeItem(GroupShoppingList gsl) {
		if (gslr.exists(gsl.getGslck())) {
			gslr.delete(gsl.getGslck());
			return "Item "+gsl.getGslck().getItem()+" has been successfully deleted";
		}
		else {
			return "Item "+gsl.getGslck().getItem()+" does not exist";
		}
		
	}
	public List<String> getAllItems(String groupName) {
		List<String> returnList = new ArrayList<String>();
		Iterator<GroupShoppingList> i = gslr.findByGslck_groupName(groupName).iterator();
		while (i.hasNext()) returnList.add(i.next().getGslck().getItem());
		return returnList;
	}
	
	//---------------------TASK SERVICES----------------------------------------
	
	public String addTask(GroupTask gt) {
		if (gtr.exists(gt.getGtck())) {
			return "Task "+gt.getGtck().getTask()+" has already been added";
		}
		else {
			gtr.save(gt);
			return "Task "+gt.getGtck().getTask()+" has been successfully added";
		}
	}
	public String removeTask(GroupTask gt) {
		if (gtr.exists(gt.getGtck())) {
			gtr.delete(gt.getGtck());
			return "Task "+gt.getGtck().getTask()+" has been successfully deleted";
		}
		else {
			return "Task "+gt.getGtck().getTask()+" does not exist";
		}
	}
	public List<String> getAllTasks(String groupName) {
		List<String> returnList = new ArrayList<String>();
		Iterator<GroupTask> i = gtr.findByGtck_groupName(groupName).iterator();
		while (i.hasNext()) returnList.add(i.next().getGtck().getTask());
		return returnList;
	}
	
	
	//----------------------EMAIL SERVICES-----------------------------
	
	
	public boolean sendInvite(String username, String emailTo, String groupName) {
		try {
			SimpleMailMessage smm = new SimpleMailMessage();
			smm.setFrom("support@gigbuddy.org");
			smm.setSubject("Invitation");
			smm.setText("Hi! "+username+" would like you to join a group named "+groupName+" on Bodree");
			smm.setTo(emailTo);
			jms.send(smm);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	//---------------------PRIVATE METHODS----------------------------------
	
	private Group isUserOwnGroup(GroupMembers g) {
		Group go = gr.findOne(g.getGck().getGroup_name());
		//If nobody owns the group
		if (go.getUsername() == null) return null;
		if ((go != null) && (go.getUsername().contentEquals(g.getGck().getUsername()))) return go;
		else return null;
	}
	private void removeGroupOwner(Group go) {
		go.setUsername(null);
		gr.save(go);
	}
	
	
	
}
