package fr.paris.lutece.plugins.jcr.util;

import fr.paris.lutece.plugins.jcr.service.search.JcrIndexer;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.event.ResourceEventManager;

/**
 * 
 * JcrIndexerUtils
 *
 */
public class JcrIndexerUtils
{
	// Indexed resource type name
	public static final String CONSTANT_TYPE_RESOURCE = "JSR170_JCR_FILE";
	
	/**
     * Warn that a action has been done.
     * @param strIdResource the resource id
     * @param nIdTask the key of the action to do
     */
    public static void addIndexerAction( String strIdResource, int nIdTask )
    {
        ResourceEvent event = new ResourceEvent();
        event.setIdResource( String.valueOf( strIdResource ) );
        event.setTypeResource( CONSTANT_TYPE_RESOURCE );
        switch (nIdTask)
        {
        case IndexerAction.TASK_CREATE:
        	ResourceEventManager.fireAddedResource( event );
        	break;
        case IndexerAction.TASK_MODIFY:
        	ResourceEventManager.fireUpdatedResource( event );
        	break;
        case IndexerAction.TASK_DELETE:
        	ResourceEventManager.fireDeletedResource( event );
        	break;
        default:
        	break;
        }
    }
}
