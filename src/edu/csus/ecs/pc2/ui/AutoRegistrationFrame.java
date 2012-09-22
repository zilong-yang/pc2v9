package edu.csus.ecs.pc2.ui;

import java.awt.Dimension;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;

/**
 * Auto Registration Form.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class AutoRegistrationFrame extends JFramePlugin {

    /**
     * 
     */
    private static final long serialVersionUID = -7129836991961579846L;

    private AutoRegistrationPane autoRegistrationPane = null;

    /**
     * This method initializes
     * 
     */
    public AutoRegistrationFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new Dimension(477, 284));
        this.setContentPane(getAutoRegistrationPane());
        this.setTitle("Automatic Registration Form");

        getAutoRegistrationPane().setParentFrame(this);
        FrameUtilities.centerFrame(this);
    }

    @Override
    public String getPluginTitle() {
        return "Auto Registration Frame";
    }

    /**
     * This method initializes autoRegistrationPane
     * 
     * @return edu.csus.ecs.pc2.ui.AutoRegistrationPane
     */
    private AutoRegistrationPane getAutoRegistrationPane() {
        if (autoRegistrationPane == null) {
            autoRegistrationPane = new AutoRegistrationPane();
        }
        return autoRegistrationPane;
    }
    
    @Override
    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        getAutoRegistrationPane().setContestAndController(inContest, inController);
        getAutoRegistrationPane().setParentFrame(this);
    }

    public void processCancel() {
        setVisible(false);
        getParentFrame().setVisible(true);
    }
    
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (getParentFrame() != null) {
            getParentFrame().setVisible(! b);
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
