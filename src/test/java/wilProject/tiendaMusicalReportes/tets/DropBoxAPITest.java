/**
 * 
 */
package wilProject.tiendaMusicalReportes.tets;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

/**
 * @author wilson
 *
 */
public class DropBoxAPITest {

	@Test
	public void test() {
		//Se configura el token de acceso a la app creada en dropbox
		String TOKEN = "sl.BWoK4sDPgV4vso98Kj5S3DnQ4yhbkPkc5fxr4r70tkeo-LEeEt1xezQ4sb90PbqfjtJPTi9hYMiL52uzrQoWoRbWpvNOAPMgsNqLJtZ3xxXOXzgYcVMRKCyKiFEH8FuIXVALhIE";
		
		DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("wilProject/test-dropbox").build();
		DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig, TOKEN);
		
		try {
			assertNotNull(dbxClientV2);
			
			// Se obtiene y se muestra la informacion de la cuenta perteneciente a la app
			FullAccount fullAccount = dbxClientV2.users().getCurrentAccount();
			System.out.println("Nombre de la cuenta: " + fullAccount.getEmail());
		} catch (DbxException e) {
			e.printStackTrace();
			
			assertNull(dbxClientV2);
		}
	}

}
