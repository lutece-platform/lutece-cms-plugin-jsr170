package fr.paris.lutece.plugins.jcr.business;

import java.util.List;

import fr.paris.lutece.plugins.jcr.authentication.JsrUser;
import fr.paris.lutece.plugins.jcr.business.admin.AdminJcrHome;
import fr.paris.lutece.plugins.jcr.business.admin.AdminWorkspace;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;

public class RepositoryFileHomeTest extends LuteceTestCase {

	public void testGetRepositoryFileListAdminWorkspaceJsrUser() {
		RepositoryFileHome instance = RepositoryFileHome.getInstance();
		Plugin plugin = PluginService.getPlugin("jsr170");
		AdminWorkspace workspace = AdminJcrHome.getInstance().findWorkspaceById(2, plugin);
		JsrUser user = new JsrUser(new AdminUser());
		List<IRepositoryFile> list = instance.getRepositoryFileList(workspace, user);
		
		assertTrue(list.size() != 0);
		
		list = instance.getPathToFile(workspace, list.get(0).getAbsolutePath(), user);
		assertTrue(list.size() != 0);
//		fail("Not yet implemented");
	}

	public void testGetRepositoryFileById() {
		fail("Not yet implemented");
	}

	public void testGetRepositoryFileListAdminWorkspaceStringJsrUser() {
		fail("Not yet implemented");
	}

}
