package mobile.rest.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import mobile.rest.api.domain.GroupShoppingList;
import mobile.rest.api.domain.GroupShoppingListCompositeKey;
import mobile.rest.api.repository.GroupShoppingListRepository;
import mobile.rest.api.repository.GroupTaskRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MobileApiApplicationTests {
	
	@Autowired
	GroupShoppingListRepository gslr;
	@Autowired
	GroupTaskRepository gtr;

	@Test
	public void contextLoads() throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		GroupShoppingList gsl = new GroupShoppingList();
		gsl.setGslck(new GroupShoppingListCompositeKey("myGroup", "bread"));
		System.out.println(this.getClass().getDeclaredField("gslr"));
	}

}
