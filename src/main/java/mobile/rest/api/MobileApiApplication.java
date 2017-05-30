package mobile.rest.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;

@SpringBootApplication
public class MobileApiApplication {

	public static void main(String[] args) {
		FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream("bodree-7faa2-firebase-adminsdk-nwjsn-5e30e96eda.json");
		
		FirebaseOptions options = new FirebaseOptions.Builder()
		  .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
		  .setDatabaseUrl("https://bodree-7faa2.firebaseio.com/")
		  .build();

		FirebaseApp.initializeApp(options);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SpringApplication.run(MobileApiApplication.class, args);
	}
}
