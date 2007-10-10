package edu.csus.ecs.pc2.core.model;


/**
 * Listener for all Group Events.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public interface IGroupListener {

    /**
     * New Group.
     * @param event
     */
    void groupAdded(GroupEvent event);

    /**
     * Group information has changed.
     * @param event
     */
    void groupChanged(GroupEvent event);

    /**
     * Group has been removed.
     * @param event
     */
    void groupRemoved(GroupEvent event);
}
