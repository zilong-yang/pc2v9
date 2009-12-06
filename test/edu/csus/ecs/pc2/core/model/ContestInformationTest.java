package edu.csus.ecs.pc2.core.model;

import java.util.Properties;

import junit.framework.TestCase;
import edu.csus.ecs.pc2.core.scoring.DefaultScoringAlgorithm;

/**
 * Tests for Contest Information.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class ContestInformationTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsSameAs() {

        ContestInformation contestInformation1 = new ContestInformation();
        ContestInformation contestInformation2 = new ContestInformation();

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation2));

        String title = "Contest Title";
        contestInformation1.setContestTitle(title);

        assertFalse("Diff title", contestInformation1.isSameAs(contestInformation2));

        contestInformation2.setContestTitle(title);

        assertTrue("Same title", contestInformation1.isSameAs(contestInformation2));

        contestInformation1.setMaxFileSize(4000);

        assertFalse("Diff max file size", contestInformation1.isSameAs(contestInformation2));

        contestInformation2.setMaxFileSize(4000);

        assertTrue("Same max file size", contestInformation1.isSameAs(contestInformation2));

    }

    public void testPropertiesIsSameAs() {

        ContestInformation contestInformation1 = new ContestInformation();
        ContestInformation contestInformation2 = new ContestInformation();

        Properties properties = DefaultScoringAlgorithm.getDefaultProperties();
        contestInformation1.setScoringProperties(properties);
        properties = DefaultScoringAlgorithm.getDefaultProperties();
        contestInformation2.setScoringProperties(properties);

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation1));

        assertTrue("Same properties", contestInformation1.isSameAs(contestInformation2));

        properties = new Properties();
        contestInformation2.setScoringProperties(properties);
        assertFalse("Not Same properties", contestInformation1.isSameAs(contestInformation2));

    }

}