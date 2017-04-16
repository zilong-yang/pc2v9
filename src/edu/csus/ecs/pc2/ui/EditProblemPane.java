package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.IniFile;
import edu.csus.ecs.pc2.core.Utilities;
import edu.csus.ecs.pc2.core.execute.ExecuteException;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Problem.InputValidationStatus;
import edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.core.report.IReport;
import edu.csus.ecs.pc2.core.report.ProblemsReport;
import edu.csus.ecs.pc2.core.report.SingleProblemReport;
import edu.csus.ecs.pc2.imports.ccs.ContestSnakeYAMLLoader;
import edu.csus.ecs.pc2.validator.clicsValidator.ClicsValidatorSettings;
import edu.csus.ecs.pc2.validator.customValidator.CustomValidatorSettings;
import edu.csus.ecs.pc2.validator.inputValidator.InputValidatorRunner;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

/**
 * Add/Edit Problem Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditProblemPane extends JPanePlugin {

    // TODO 917 automatic check on load when external/internal data sets changed

    private static boolean debug22EditProblem = false;

    /**
     *  
     */
    private static final long serialVersionUID = -1060536964672397704L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    /**
     * The original/input problem.
     */
    private Problem problem = null; // @jve:decl-index=0:

    private JTabbedPane mainTabbedPane = null;

    private JPanel generalPane = null;

    private JPanel judgingTypePane = null;

    private JTextField problemNameTextField = null;

    private JTextField timeOutSecondTextField = null;

    private JTextField problemLetterTextField = null;

    private JCheckBox problemRequiresDataCheckBox = null;

    private JPanel dataProblemPane = null;

    private JPanel readsFromPane = null;

    private JPanel inputDataFilePane = null;

    private JRadioButton stdinRadioButton = null;

    private JRadioButton fileRadioButton = null;

    private JPanel fileNamePane = null;

    private JButton selectFileButton = null;

    private JCheckBox judgesHaveAnswerFiles = null;

    private JPanel answerFilePane = null;

    private JPanel answerFilenamePane = null;

    private JButton answerBrowseButton = null;

    private JLabel inputDataFileLabel = null;

    private JLabel answerFileNameLabel = null;

    private JLabel problemNameLabel = null;

    private JLabel timeoutLabel = null;

    /**
     * Is the form/GUI being currently populated? Used to avoid reEntry/race conditions populating GUI.
     */
    private boolean populatingGUI = true;

    /**
     * last directory where searched for files.
     */
    private String lastDirectory; // @jve:decl-index=0:

    @SuppressWarnings("unused")
    private String lastYamlLoadDirectory;

    /**
     * The current/original data files, used to compare with changes.
     */
    protected ProblemDataFiles originalProblemDataFiles;

    protected ProblemDataFiles newProblemDataFiles;

    private ButtonGroup teamReadsFrombuttonGroup = null; // @jve:decl-index=0:visual-constraint="586,61"

    private ButtonGroup judgingTypeGroup = null; // @jve:decl-index=0:visual-constraint="586,61"

    //the top-level tabbed pane holding settings for various validator options
    private JPanel outputValidatorPane = null;

    private JRadioButton useNOValidatatorRadioButton = null;

    private JRadioButton usePC2ValidatorRadioButton = null;

    private JRadioButton useCLICSValidatorRadioButton = null;

    private JRadioButton useCustomValidatorRadioButton = null;

    private JCheckBox showValidatorToJudgesCheckBox = null;

    private JCheckBox isCLICSCaseSensitiveCheckBox = null;
    
    private JCheckBox isCLICSSpaceSensitiveCheckBox = null;

    private JCheckBox showCompareCheckBox = null;

    private JCheckBox doShowOutputWindowCheckBox = null;

    private ButtonGroup validatorChoiceButtonGroup = null; // @jve:decl-index=0:visual-constraint="595,128"

    private static final String NL = System.getProperty("line.separator");

    private JRadioButton computerJudgingRadioButton = null;

    private JRadioButton manualJudgingRadioButton = null;

    private JCheckBox manualReviewCheckBox = null;

    private JCheckBox prelimaryNotificationCheckBox = null;

    private JCheckBox deleteProblemCheckBox = null;

    private boolean listenersAdded = false;

    private JButton loadButton = null;

    private ContestSnakeYAMLLoader loader = null;

    private JButton exportButton = null;

    private JButton reportButton = null;

    private MultipleDataSetPane multipleDataSetPane = null;

    private JPanel judgeTypeInnerPane = null;

    private JTextField shortNameTextfield;

    private String fileNameOne;
    
    //the panel holding the "Do not use validator" radio button
    private JPanel noValidatorPanel;

    //the panel holding the "use CLICS default validator" radio button and the corresponding options panel
    private JPanel clicsValidatorPanel = null;
    private JPanel clicsValidatorOptionsSubPanel = null;    
      private JCheckBox floatRelativeToleranceCheckBox;
      private JTextField floatRelativeToleranceTextField;
      private JCheckBox floatAbsoluteToleranceCheckBox;
      private JTextField floatAbsoluteToleranceTextField;

    //the panel holding the "use custom validator" radio button and the corresponding options panel
    private JPanel customValidatorPanel = null;
    private Component horizontalStrut_1;
    private JPanel customValidatorOptionsSubPanel;
      private JLabel customValidatorProgramNameLabel;
      private JTextField customValidatorProgramNameTextField;
      private JButton chooseValidatorProgramButton = null;
      private JLabel customValidatorCommandLineLabel = null;
      private JTextField customValidatorCommandLineTextField = null;

    
    private Component horizontalStrut;    
    private Component verticalStrut;
    private Component verticalStrut_1;
    private Component verticalStrut_2;
    private Component verticalStrut_3;
    private Component verticalStrut_4;
    private Component verticalStrut_5;
    private JPanel pc2ValidatorPanel;
    private Component horizontalStrut_2;
    private JPanel pc2ValidatorOptionsSubPanel;
      private JLabel pc2ValidatorOptionComboBoxLabel;
      private JComboBox<String> pc2ValidatorOptionComboBox;
      private JCheckBox pc2ValidatorIgnoreCaseCheckBox;

    private JLabel lblWhatsThisCLICSValidator;

    //a temporary variables to track changes in the command line
    private String localPC2InterfaceCustomValidatorCommandLine;
    private String localClicsInterfaceCustomValidatorCommandLine;
    
    private InputValidationResultsTableModel inputValidationResultsTableModel = new InputValidationResultsTableModel();
    
    private InputValidationStatus inputValidationStatus = InputValidationStatus.NOT_TESTED;

    /**
     * Constructs an EditProblemPane with default settings.
     * 
     */
    public EditProblemPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this EditProblemPane.
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(544, 681));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);
        addWindowListeners();

        getMultipleDataSetPane().setContestAndController(inContest, inController);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getLoadButton().setVisible(Utilities.isDebugMode());
                getExportButton().setVisible(Utilities.isDebugMode());
            }
        });

        if (IniFile.isFilePresent()) {
            String value = IniFile.getValue("client.debug");
            if (value != null) {
                debug22EditProblem = value.equalsIgnoreCase("true");
            }
        }

    }

    private void addWindowListeners() {

        if (listenersAdded) {
            // No need to add the listeners twice or more.
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }

                        @Override
                        public void windowOpened(WindowEvent e) {
                            getProblemNameTextField().requestFocus();
                        }

                        public void windowActivated(WindowEvent e) {
                            getProblemNameTextField().requestFocus();
                        };
                    });
                    listenersAdded = true;
                }
            }
        });
    }

    public String getPluginTitle() {
        return "Edit Problem Pane";
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getReportButton(), null);
            buttonPane.add(getLoadButton(), null);
            buttonPane.add(getExportButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProblem();
                }
            });
        }
        return addButton;
    }

    /**
     * Adds to the contest a new Problem as defined by the current GUI fields.
     * This method is invoked by pushing the "Add" button on the EditProblemPane GUI 
     * after having defined the values for a new problem being added.
     * 
     * The method also gets invoked by making GUI changes to a new problem definition,
     * then pressing "Cancel" (which displays a message "Problem Modified - Save Changes?")
     * and responding "Yes" to the message.
     * 
     */
    protected void addProblem() {
        
        if (problemNameTextField.getText().trim().length() < 1) {
            showMessage("Enter a problem name (\"General\" tab)");
            return;
        }

        if (!validateProblemFields()) {
            // new problem is invalid, just return; message issued by validateProblemFields
            if (debug22EditProblem) {
                System.err.println ("DEBUG: validateProblemFields() returned false");
            }
            return;
        }

        if (getInputValidatorProgramNameTextField().getText() == null || getInputValidatorProgramNameTextField().getText().equals("")) {
            //no input validator defined; issue a warning
            String warning = "You are attempting to specify a Problem which has no Input Data Validator." 
                    + "\n\nThis is usually not good practice because it provides no way to insure that the"
                    + "\nJudge's data files meet the Problem Specification."
                    + "\n\nAre you sure you want to do this?";
            int response = JOptionPane.showConfirmDialog(getParentFrame(), warning, "No Input Validator specified", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (!(response == JOptionPane.YES_OPTION)) {
                return;
            }
        }


        Problem newProblem = null;
        try {
            newProblemDataFiles = getProblemDataFilesFromFields();

            //get a new Problem object from the GUI fields (throws InvalidFieldValue if any GUI fields are illegal)
            newProblem = getProblemFromFields(null, newProblemDataFiles);
            
            SerializedFile sFile;
            // SOMEDAY should we loop thru the files doing the check?
            if (newProblemDataFiles.getJudgesDataFiles().length == 1) {
                sFile = newProblemDataFiles.getJudgesDataFile();
                if (sFile != null) {
                    if (checkFileFormat(sFile)) {
                        newProblemDataFiles.setJudgesDataFile(sFile);
                    }
                }
            }
            if (newProblemDataFiles.getJudgesAnswerFiles().length == 1) {
                sFile = newProblemDataFiles.getJudgesAnswerFile();
                if (sFile != null) {
                    if (checkFileFormat(sFile)) {
                        newProblemDataFiles.setJudgesAnswerFile(sFile);
                    }
                }
            }
            
            //check the output validator
            sFile = newProblemDataFiles.getOutputValidatorFile();
            if (sFile != null) {
                if (checkFileFormat(sFile)) {
                    newProblemDataFiles.setOutputValidatorFile(sFile);
                }
            }
            
            //check the input validator
            sFile = newProblemDataFiles.getInputValidatorFile();
            if (sFile != null) {
                if (checkFileFormat(sFile)) {
                    newProblemDataFiles.setInputValidatorFile(sFile);
                }
            }
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        if (!newProblem.getElementId().equals(newProblemDataFiles.getProblemId())) {
            // this is like ProblemDataFiles.copy but without overwriting the elementID & ProblemId, 
            // which is the problem we are trying to fix here
            ProblemDataFiles clone = new ProblemDataFiles(newProblem);
            clone.setSiteNumber(newProblemDataFiles.getSiteNumber());
            clone.setOutputValidatorFile(newProblemDataFiles.getOutputValidatorFile());
            clone.setInputValidatorFile(newProblemDataFiles.getInputValidatorFile());
            clone.setJudgesAnswerFiles(newProblemDataFiles.getJudgesAnswerFiles());
            clone.setJudgesDataFiles(newProblemDataFiles.getJudgesDataFiles());

            // without this the problemId is wrong in newProblemDataFiles. so the controller.getProblemDataFiles(problem) does not find it
            newProblemDataFiles = clone;
        }

        // Add next letter to problem.
        int numberProblems = getContest().getProblems().length;
        String nextLetter = Utilities.getProblemLetter(numberProblems + 1);
        newProblem.setLetter(nextLetter);
        
        //add the current input validation status to the problem (this field is not displayed in the GUI, except indirectly via
        // the "Status message" text...)
        newProblem.setInputValidationStatus(this.getInputValidationStatus());

        getController().addNewProblem(newProblem, newProblemDataFiles);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);
        
        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Enable or disable Update button based on comparison of current problem to GUI fields.
     * 
     */
    public void enableUpdateButton() {
        
//        showStackTrace();
        
        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;
        String updateToolTip = "";

        if (problem != null) {

            try {
                //get the new version of the problem from the GUI
                Problem changedProblem = getProblemFromFields(null, newProblemDataFiles);
                
                if (!problem.isSameAs(changedProblem) || getMultipleDataSetPane().hasChanged(originalProblemDataFiles)) {
                    enableButton = true;
                    updateToolTip = "Problem changed";
                }
                
                //see if the problem data files have changed; enable the Update button and update the tooltip if so
                ProblemDataFiles pdf = getContest().getProblemDataFile(problem);
                ProblemDataFiles proposedPDF = getMultipleDataSetPane().getProblemDataFiles();
                if (pdf != null) {
                    int fileChanged = 0;
                    SerializedFile[] judgesDataFiles = pdf.getJudgesDataFiles();
                    SerializedFile[] judgesDataFilesNew = null;
                    if (proposedPDF != null) {
                        judgesDataFilesNew = proposedPDF.getJudgesDataFiles();
                    }
                    if ((judgesDataFiles == null && judgesDataFilesNew != null) || (judgesDataFiles != null & judgesDataFilesNew == null)) {
                        // one was null the other was not
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges data";
                        } else {
                            updateToolTip += ", Judges data";
                        }
                        enableButton = true;
                    } else if (judgesDataFiles.length != judgesDataFilesNew.length) {  //TODO: this will throw NPE if both are null (the above only eliminates the XOR possibility)
                        fileChanged += Math.abs(judgesDataFiles.length - judgesDataFilesNew.length);
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges data";
                        } else {
                            updateToolTip += ", Judges data";
                        }
                        enableButton = true;
                    } else {
                        // compare each file
                        boolean changed = false;
                        if (judgesDataFiles != null) {
                            for (int i = 0; i < judgesDataFiles.length; i++) {
                                //get the existing serialized data file
                                SerializedFile existingSerializedDataFile = judgesDataFiles[i];
                                //get a new serialized file from disk, using the existing file's name.
                                // The "isExternal" flag is specified as true (causing the file data to not actually be loaded) because
                                // we just the SHA code -- we do not need to load the data
                                SerializedFile serializedFile2 = new SerializedFile(existingSerializedDataFile.getAbsolutePath(), true);
                                //check whether the file from disk matches the existing (stored) file
                                if (!existingSerializedDataFile.getName().equals(judgesDataFilesNew[i].getName())) {
                                    //file name has somehow changed on disk (not sure how this could happen?)
                                    fileChanged++;
                                    changed = true;
                                } else if (!existingSerializedDataFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                    // contents have changed on disk
                                    fileChanged++;
                                    changed = true;
                                }
                            }
                            if (changed) {
                                if (updateToolTip.equals("")) {
                                    updateToolTip = "Judges data";
                                } else {
                                    updateToolTip += ", Judges data";
                                }
                                enableButton = true;
                            }
                        }
                    }
                    
                    //see if the judge's answer files have changed; enable the Update button and update the tooltip if so
                    SerializedFile[] judgesAnswerFiles = pdf.getJudgesAnswerFiles();
                    SerializedFile[] judgesAnswerFilesNew = null;
                    if (proposedPDF != null) {
                        judgesAnswerFilesNew = proposedPDF.getJudgesAnswerFiles();
                    }
                    if ((judgesAnswerFiles == null && judgesAnswerFilesNew != null) || (judgesAnswerFiles != null && judgesAnswerFilesNew == null)) {
                        // one was null the other was not
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges answer";
                        } else {
                            updateToolTip += ", Judges answer";
                        }
                        enableButton = true;
                        fileChanged++;
                    } else if (judgesAnswerFiles.length != judgesAnswerFilesNew.length) {  //TODO: this will throw NPE if both are null (the above only eliminates the XOR possibility)
                        fileChanged += Math.abs(judgesAnswerFiles.length - judgesAnswerFilesNew.length);
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Judges answer";
                        } else {
                            updateToolTip += ", Judges answer";
                        }
                        enableButton = true;
                    } else {
                        // compare each file
                        boolean changed = false;
                        if (judgesAnswerFiles != null) {
                            for (int i = 0; i < judgesAnswerFiles.length; i++) {
                                //get the existing serialized answer file
                                SerializedFile existingSerializedAnswerFile = judgesAnswerFiles[i];
                                //get a new serialized file from disk, using the existing file's name.
                                // The "isExternal" flag is specified as true (causing the file data to not actually be loaded) because
                                // we just the SHA code -- we do not need to load the data
                                SerializedFile serializedFile2 = new SerializedFile(existingSerializedAnswerFile.getAbsolutePath(), true);
                                //check whether the file from disk matches the existing (stored) file
                                if (!existingSerializedAnswerFile.getName().equals(judgesAnswerFilesNew[i].getName())) {
                                    //file name has somehow changed on disk (not sure how this could happen?)
                                    fileChanged++;
                                    changed = true;
                                } else if (!existingSerializedAnswerFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                    // contents have changed on disk
                                    fileChanged++;
                                    changed = true;
                                }
                            }
                            if (changed) {
                                if (updateToolTip.equals("")) {
                                    updateToolTip = "Judges answer";
                                } else {
                                    updateToolTip += ", Judges answer";
                                }
                                enableButton = true;
                            }
                        }
                    }
                    
                    //see if the choice of which output validator (if any) to use has changed; 
                    // enable the Update button and update the tooltip if so
                    if ( problem.getValidatorType() != changedProblem.getValidatorType() ) {
                        enableButton = true;
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Output Validator";
                        } else {
                            updateToolTip += ", Output Validator";
                        }                        
                    }
                    
                    //see if the PC2 Validator options have changed; enable the Update button and update the tooltip if so
                    if (problem.getPC2ValidatorSettings()!=null && changedProblem.getPC2ValidatorSettings()!=null) {
                        PC2ValidatorSettings problemSettings = problem.getPC2ValidatorSettings();
                        PC2ValidatorSettings changedProblemSettings = changedProblem.getPC2ValidatorSettings();
                        if ( (problemSettings.getWhichPC2Validator() != changedProblemSettings.getWhichPC2Validator()) ||
                             (problemSettings.isIgnoreCaseOnValidation() != changedProblemSettings.isIgnoreCaseOnValidation()) ) {
                            enableButton = true;
                            if (updateToolTip.equals("")) {
                                updateToolTip = "PC2 Output Validator options";
                            } else {
                                updateToolTip += ", PC2 Output Validator options";
                            }
                        }
                    }

                    //see if the Clics Validator options have changed; enable the Update button and update the tooltip if so
                    if (problem.getClicsValidatorSettings()!=null && changedProblem.getClicsValidatorSettings()!=null) {
                        ClicsValidatorSettings problemSettings = problem.getClicsValidatorSettings();
                        ClicsValidatorSettings changedProblemSettings = changedProblem.getClicsValidatorSettings();
                        if ( (problemSettings.getFloatAbsoluteTolerance() != changedProblemSettings.getFloatAbsoluteTolerance()) ||
                             (problemSettings.getFloatRelativeTolerance() != changedProblemSettings.getFloatRelativeTolerance()) ||
                             (problemSettings.isFloatAbsoluteToleranceSpecified() != changedProblemSettings.isFloatAbsoluteToleranceSpecified()) ||
                             (problemSettings.isFloatRelativeToleranceSpecified() != changedProblemSettings.isFloatRelativeToleranceSpecified()) ||
                             (problemSettings.isCaseSensitive() != changedProblemSettings.isCaseSensitive()) ||
                             (problemSettings.isSpaceSensitive() != changedProblemSettings.isSpaceSensitive()) ) {
                            enableButton = true;
                            if (updateToolTip.equals("")) {
                                updateToolTip = "CLICS Output Validator options";
                            } else {
                                updateToolTip += ", CLICS Output Validator options";
                            }
                        }
                    }

                   
                    //see if the Custom Validator options have changed; enable the Update button and update the tooltip if so
                    if (problem.getCustomValidatorSettings()!=null && changedProblem.getCustomValidatorSettings()!=null) {
                        CustomValidatorSettings problemSettings = problem.getCustomValidatorSettings();
                        CustomValidatorSettings changedProblemSettings = changedProblem.getCustomValidatorSettings();
                        boolean changed = false ;
                        //check for changes in the command line
                        if ( (problemSettings.getCustomValidatorCommandLine()==null ^ changedProblemSettings.getCustomValidatorCommandLine()==null) ) {
                            //XOR=1 -> they are different(one is null, the other is not) -> something changed
                            changed = true;
                        } else if ( !(problemSettings.getCustomValidatorCommandLine()==null && changedProblemSettings.getCustomValidatorCommandLine()==null) ) {
                            //they're not BOTH null (and therefore BOTH are NON-null); something MIGHT have changed
                            if (!(problemSettings.getCustomValidatorCommandLine().equals(changedProblemSettings.getCustomValidatorCommandLine()))) {
                                changed = true;
                            }
                        }
                        //check for changes in the executable program name
                        if ( (problemSettings.getCustomValidatorProgramName()==null ^ changedProblemSettings.getCustomValidatorProgramName()==null) ) {
                            //XOR=1 -> they are different(one is null, the other is not) -> something changed
                            changed = true;
                        } else if ( !(problemSettings.getCustomValidatorProgramName()==null && changedProblemSettings.getCustomValidatorProgramName()==null) ) {
                            //they're not BOTH null (and therefore BOTH are NON-null); something MIGHT have changed
                            if (!(problemSettings.getCustomValidatorProgramName().equals(changedProblemSettings.getCustomValidatorProgramName()))) {
                                changed = true;
                            }
                        }
                        
                        //check for changes in the actual validator program file
                        String changedProblemValidatorFileName = changedProblemSettings.getCustomValidatorProgramName();
                        if (changedProblemValidatorFileName != null && changedProblemValidatorFileName.length() > 0) {
                            //TODO: there could be a problem here; pdf.getValidatorFile() seems to sometimes return null even though the problem has a validator...
                            if (!fileSameAs(pdf.getOutputValidatorFile(), changedProblemValidatorFileName)) {
                                changed = true;
                                fileChanged++;
                            }
                        }

                        //check for changes in the specified interface being used
                        if( problemSettings.isUseClicsValidatorInterface() != changedProblemSettings.isUseClicsValidatorInterface() ||
                            problemSettings.isUsePC2ValidatorInterface() != changedProblemSettings.isUsePC2ValidatorInterface() ) {
                            changed = true;
                        }
                        
                        if (changed) {
                            
                            enableButton = true;
                            if (updateToolTip.equals("")) {
                                updateToolTip = "Custom Output Validator options";
                            } else {
                                updateToolTip += ", Custom Output Validator options";
                            }
                        }
                    }
                    
                    //check for changes in Input Validator settings
                    if (!problem.getInputValidatorProgramName().equals(changedProblem.getInputValidatorProgramName())) {
                        enableButton = true;
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Input Validator";
                        } else {
                            updateToolTip += ", Input Validator";
                        }
                    }
                    
                    if (!problem.getInputValidatorCommandLine().equals(changedProblem.getInputValidatorCommandLine())) {
                        enableButton = true;
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Input Validator Command";
                        } else {
                            updateToolTip += ", Input Validator Command";
                        }
                    }
                    
                    if (!(problem.getInputValidationStatus() == this.getInputValidationStatus())) {
                        enableButton = true;
                        if (updateToolTip.equals("")) {
                            updateToolTip = "Input Validator Results";
                        } else {
                            updateToolTip += ", Input Validator Results";
                        }
                        
                    }
                    
                    if (fileChanged > 0) {
                        if (fileChanged == 1) {
                            updateToolTip += " file changed";
                        } else {
                            updateToolTip += " files changed";

                        }
                    }
                    
                } else {
                    logDebugException("No ProblemDataFiles for " + problem);
                }

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                logDebugException("Input Problem (but not saving) ", e);

                enableButton = true;
            } catch (Exception ex) {
                logDebugException("Edit Problem ", ex);
                showMessage("Error, check logs.  " + ex.getMessage());
            }

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        if (updateToolTip.equals("")) {
            // otherwise we get a sliver of a tooltip
            getUpdateButton().setToolTipText(null);
        } else {
            getUpdateButton().setToolTipText(updateToolTip);
        }
        enableUpdateButtons(enableButton);

    }

    private void logDebugException(String string) {
        if (Utilities.isDebugMode()) {
            System.err.print("Debug message " + string);
        }
        getLog().log(Log.DEBUG, string);
    }

    private void logDebugException(String string, Exception e) {

        if (Utilities.isDebugMode()) {
            System.err.print("Debug message " + string);
            e.printStackTrace(System.err);
        }
        getLog().log(Log.DEBUG, string, e);

    }

    /**
     * Displays the class, method, and line number of the method that called this method, 
     * along with the same information for the method that called THAT method.
     */
    @SuppressWarnings("unused")
    private void showStackTrace() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        
        String callingClassName = stackTraceElements[2].getClassName();
        String callingMethodName = stackTraceElements[2].getMethodName();
        int callingMethodLineNumber = stackTraceElements[2].getLineNumber();
        System.out.println ("\nIn " + callingClassName + "." + callingMethodName + "(line " + callingMethodLineNumber + ")");

        callingClassName = stackTraceElements[3].getClassName();
        callingMethodName = stackTraceElements[3].getMethodName();
        callingMethodLineNumber = stackTraceElements[3].getLineNumber();
        System.out.println ("called from " + callingClassName + "." + callingMethodName + "(line " + callingMethodLineNumber + ")");
        
    }
    
    /**
     * Compares the specified SerializedFile with the file on disk of the specified filename and
     * returns an indication of whether or not the two are identical.
     * 
     * If the specified SerializedFile is null, or the specified filename is null or the
     * empty string, returns false (since no comparison can be made and hence they cannot be "the same").  
     * If the full path file name in the SerializedFile does not match the specified filename, returns false.
     * Otherwise, compares the checksums using method {@link #needsFreshening(SerializedFile, String)}
     * and returns an indication of whether the checksums match ({@link #needsFreshening(SerializedFile, String)}
     * returns false if the files match).
     * 
     * @param storedFile the SerializedFile to compare
     * @param diskFileName the name of a file on disk to compare
     * @return true if the checksum of the SerializedFile matches that of the corresponding file on disk
     */
    private boolean fileSameAs(SerializedFile storedFile, String diskFileName) {
        if (diskFileName==null || diskFileName.trim().equals("") || storedFile==null || !storedFile.getAbsolutePath().equals(diskFileName)) {
            return false;
        } else {
            return !needsFreshening(storedFile, diskFileName);
        }
    }

    /**
     * Create a Problem from the fields in this GUI. This method assumes the data in the GUI fields
     * has already been validated (i.e., the GUI values define a legitimate, complete Problem);
     * while the method does do some sanity checking it should not be assumed to have completely
     * verified the validity of the GUI data.
     * 
     * This method also populates newProblemDataFiles for the data files.
     * 
     * @param checkProblem
     *            will update this Problem if supplied, if null creates and returns a new Problem
     * @param dataFiles
     * 
     * @return a Problem based on fields in this EditProblemPane GUI
     * 
     * @throws InvalidFieldValue if any of the fields in the GUI are incomplete or illegally set
     */
    public Problem getProblemFromFields(Problem checkProblem, ProblemDataFiles dataFiles) {
        
        SerializedFile outputValidatorSF = null ;
        
        boolean isEditingExistingProblem; 
        if (checkProblem!=null) {
            isEditingExistingProblem = true;
        } else {
            isEditingExistingProblem = false;
        }
        
        /**
         * Data file from General tab.
         */
        SerializedFile lastDataFile = null;

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 in getProblemFromFields start");
        }

        /**
         * Answer file from General Tab.
         */
        SerializedFile lastAnsFile = null;

        //check whether we've been given an existing Problem to fill
        if (!isEditingExistingProblem) {
            //we weren't give a Problem; construct a new one
            checkProblem = new Problem(getProblemNameTextField().getText());
            //check whether we already have ProblemDataFiles
            if (newProblemDataFiles == null) {
                // create a ProblemDataFiles object for the problem
                newProblemDataFiles = new ProblemDataFiles(checkProblem);
            }
        } else {
            //we were given an existing Problem (called "checkProblem"); update the critical values in the Problem
            checkProblem.setDisplayName(problemNameTextField.getText());
            checkProblem.setElementId(problem); // duplicate ElementId so that Problem key/lookup is identical
            //use the problem data files passed in
            newProblemDataFiles = dataFiles;
        }

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 in getProblemFromFields after IF");
        }

        checkProblem.setUsingExternalDataFiles(getMultipleDataSetPane().isUsingExternalDataFiles());

        checkProblem.setTimeOutInSeconds(getIntegerValue(getTimeOutTextField().getText()));

        checkProblem.setLetter(getProblemLetterTextField().getText());

        checkProblem.setActive(!getDeleteProblemCheckBox().isSelected());

        checkProblem.setShortName(getShortNameTextfield().getText());
        if (!checkProblem.isValidShortName()) {
            throw new InvalidFieldValue("Invalid problem short name");
        }

        //update problems which require data files
        if (getProblemRequiresDataCheckBox().isSelected()) {

            String fileName = inputDataFileLabel.getText();
            if (fileName == null || fileName.trim().length() == 0) {
                throw new InvalidFieldValue("Problem Requires Input Data checked, select a file ");
            }

            if (fileName.trim().length() != inputDataFileLabel.getToolTipText().length()) {
                fileName = inputDataFileLabel.getToolTipText() + "";
            }

            if (!isEditingExistingProblem) {
                SerializedFile serializedFile = new SerializedFile(fileName);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file " + fileName + " choose data file again (adding)");
                }

                checkProblem.setDataFileName(serializedFile.getName());
                lastDataFile = serializedFile;

            } else {
                //we're editing an existing problem
                if (originalProblemDataFiles.getJudgesDataFiles().length < 2) {
                    // TODO this is not MTS safe
                    SerializedFile serializedFile = originalProblemDataFiles.getJudgesDataFile();
                    if (serializedFile == null || !serializedFile.getAbsolutePath().equals(fileName)) {
                        // they've added a new file
                        serializedFile = new SerializedFile(fileName);
                        checkFileFormat(serializedFile);
                    } else {
                        serializedFile = freshenIfNeeded(serializedFile, fileName);
                    }

                    checkProblem.setDataFileName(serializedFile.getName());
                    lastDataFile = serializedFile;
                } else if (originalProblemDataFiles.getJudgesDataFiles().length > 1) {
                    lastDataFile = originalProblemDataFiles.getJudgesDataFiles()[0];
                    checkProblem.setDataFileName(lastDataFile.getName());
                }
            }
        } else {
            checkProblem.setDataFileName(null);
        }

        //update problems for which the judges have provided an Answer File
        if (judgesHaveAnswerFiles.isSelected()) {

            String fileName = answerFileNameLabel.getText();
            if (fileName == null || fileName.trim().length() == 0) {
                throw new InvalidFieldValue("Judges Have Provided Answer File checked, select a file");
            }

            if (fileName.trim().length() != answerFileNameLabel.getToolTipText().length()) {
                fileName = answerFileNameLabel.getToolTipText() + "";
            }

            if (!isEditingExistingProblem) {
                SerializedFile serializedFile = new SerializedFile(fileName);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file " + fileName + " choose answer file again (adding)");
                }

                checkProblem.setAnswerFileName(serializedFile.getName());
                // only do this if we do not already have a JudgesAnswerFile
                if (newProblemDataFiles.getJudgesAnswerFiles().length == 0) {
                    newProblemDataFiles.setJudgesAnswerFile(serializedFile);
                }
                lastAnsFile = serializedFile;
            } else {
                // we're editing an existing problem
                if (originalProblemDataFiles.getJudgesAnswerFiles().length < 2) {
                    // TODO this is not MTS safe
                    SerializedFile serializedFile = originalProblemDataFiles.getJudgesAnswerFile();
                    if (serializedFile == null || !serializedFile.getAbsolutePath().equals(fileName)) {
                        // they've added a new file
                        serializedFile = new SerializedFile(fileName);
                        checkFileFormat(serializedFile);
                    } else {
                        serializedFile = freshenIfNeeded(serializedFile, fileName);
                    }
                    lastAnsFile = serializedFile;

                    checkProblem.setAnswerFileName(serializedFile.getName());
                } else if (originalProblemDataFiles.getJudgesAnswerFiles().length > 0) {
                    lastAnsFile = originalProblemDataFiles.getJudgesAnswerFiles()[0];
                    checkProblem.setAnswerFileName(lastAnsFile.getName());
                }
            }
        } else {
            checkProblem.setAnswerFileName(null);
        }

        checkProblem.setReadInputDataFromSTDIN(getStdinRadioButton().isSelected());

        // set the flag indicating which output validator (if any) is being used in the Problem
        VALIDATOR_TYPE validatorType ;
        if (getUseNOValidatatorRadioButton().isSelected()) {
            validatorType = VALIDATOR_TYPE.NONE;
        } else if (getUsePC2ValidatorRadioButton().isSelected()) {
            validatorType = VALIDATOR_TYPE.PC2VALIDATOR;
        } else if (getUseCLICSValidatorRadioButton().isSelected()) {
            validatorType = VALIDATOR_TYPE.CLICSVALIDATOR;
        } else if (getUseCustomValidatorRadioButton().isSelected()) {
            validatorType = VALIDATOR_TYPE.CUSTOMVALIDATOR;
        } else {
            throw new InvalidFieldValue("Illegal settings in validator selection buttons");
        }
        checkProblem.setValidatorType(validatorType);

        //update settings in the Problem for each type of output validator:
        checkProblem.setPC2ValidatorSettings(getPC2ValidatorSettingsFromFields());
        checkProblem.setCLICSValidatorSettings(getCLICSValidatorSettingsFromFields());
        checkProblem.setCustomValidatorSettings(getCustomValidatorSettingsFromFields());

        //if Custom Validator is selected, make sure we have a SerializedFile for the Validator
        // (the PC2 and CLICS Validators use internal PC2 classes and don't need a separate SerializedFile)
        if (getUseCustomValidatorRadioButton().isSelected()) {

            String guiValidatorFileName = getCustomValidatorExecutableProgramTextField().getText();

            if (guiValidatorFileName == null || guiValidatorFileName.trim().length() <= 0) {
                // missing required custom validator name
                throw new InvalidFieldValue("Missing required Custom Validator program name");
            } else {
                guiValidatorFileName = guiValidatorFileName.trim();
            }

            if (!isEditingExistingProblem) {
                
                outputValidatorSF = new SerializedFile(guiValidatorFileName); 

                if (outputValidatorSF.getBuffer() == null  ||  (outputValidatorSF.getErrorMessage() != null && outputValidatorSF.getErrorMessage() != "")) {

                    String msg = "Unable to read file '" + guiValidatorFileName + "' while adding new Problem; choose validator file again";
                    if (outputValidatorSF.getErrorMessage()!=null) {
                        msg += "\n (Error Message = \"" + outputValidatorSF.getErrorMessage() + "\")";
                    }
                    throw new InvalidFieldValue( msg  );
                    
                } else {
                    
                    checkProblem.setOutputValidatorProgramName(outputValidatorSF.getAbsolutePath());
                }

            } else {

              // we're editing an existing problem
              outputValidatorSF = originalProblemDataFiles.getOutputValidatorFile();
              if (outputValidatorSF == null || !outputValidatorSF.getAbsolutePath().equals(guiValidatorFileName)) {
                  // they've added a new file
                  outputValidatorSF = new SerializedFile(guiValidatorFileName);
                  checkFileFormat(outputValidatorSF);
              } else {
                  outputValidatorSF = freshenIfNeeded(outputValidatorSF, guiValidatorFileName);
              }
            
              //put the Custom Validator SerializedFile into the Problem
              checkProblem.setOutputValidatorProgramName(outputValidatorSF.getAbsolutePath()) ;
              newProblemDataFiles.setOutputValidatorFile(outputValidatorSF);
            }
        } 
                        
        //update misc settings from GUI
        checkProblem.setShowValidationToJudges(getShowValidatorToJudgesCheckBox().isSelected());
        checkProblem.setHideOutputWindow(!getDoShowOutputWindowCheckBox().isSelected());
        checkProblem.setShowCompareWindow(getShowCompareCheckBox().isSelected());
        
        //update Input Validator settings from GUI:
        
        //Input Validator program...
        checkProblem.setProblemHasInputValidator(false);    // start with a default assumption of no Input Validator

        // get the Input Validator file name (if any) from the GUI
        String guiInputValidatorFileName = getInputValidatorProgramNameTextField().getText().trim();

        if (guiInputValidatorFileName != null && guiInputValidatorFileName.trim().length() > 0) {
            // there is an input validator file name in the GUI

            SerializedFile inputValidatorSF;
            if (!isEditingExistingProblem) {

                // we're adding a NEW problem; get a SerializedFile corresponding to the name in the GUI
                inputValidatorSF = new SerializedFile(guiInputValidatorFileName);

                // check the SerializedFile for validity (i.e. that it Serialized without error)
                if (inputValidatorSF.getBuffer() == null || (inputValidatorSF.getErrorMessage() != null && inputValidatorSF.getErrorMessage() != "")) {

                    // error constructing a SerializedFile from the specified input validator name
                    String msg = "Unable to read file '" + guiInputValidatorFileName + "' while adding new Problem; choose input validator file again";
                    if (outputValidatorSF.getErrorMessage() != null) {
                        msg += "\n (Error Message = \"" + outputValidatorSF.getErrorMessage() + "\")";
                    }
                    throw new InvalidFieldValue(msg);

                } else {
                    // we were able to build a SerializedFile from the GUI name; save the input validator
                    checkProblem.setInputValidatorProgramName(inputValidatorSF.getAbsolutePath());
                    newProblemDataFiles.setInputValidatorFile(inputValidatorSF);

                    // mark the problem as having an input validator
                    checkProblem.setProblemHasInputValidator(true);
                }

            } else {

                // we're editing an existing problem (and we know the GUI has an Input Validator name specified);
                // get the input validator currently defined in the problem (if any)
                inputValidatorSF = originalProblemDataFiles.getInputValidatorFile();

                if (inputValidatorSF == null || !inputValidatorSF.getAbsolutePath().equals(guiInputValidatorFileName)) {
                    // either there was no IV already in the problem, or they've specified a file which is different
                    inputValidatorSF = new SerializedFile(guiInputValidatorFileName);
                    checkFileFormat(inputValidatorSF);
                } else {
                    // they've specified a file in the GUI which has already been Serialized and placed in the problem;
                    // update it if necessary
                    inputValidatorSF = freshenIfNeeded(inputValidatorSF, guiInputValidatorFileName);
                }

                // when we get here, inputValidatorSF contains a SerializedFile to be put into the problem
                checkProblem.setInputValidatorProgramName(inputValidatorSF.getAbsolutePath());
                newProblemDataFiles.setInputValidatorFile(inputValidatorSF);

                // mark the problem as having an input validator
                checkProblem.setProblemHasInputValidator(true);
            }
        }

        //Input Validator Command...
        String inputValCommand = getInputValidatorCommandTextField().getText();
        checkProblem.setInputValidatorCommandLine(inputValCommand);
        
        //Input Validator "files from disk" test folder...
        String inputValFilesOnDiskFolder = getInputValidatorFilesOnDiskTextField().getText();
        checkProblem.setInputValidatorFilesOnDiskFolder(inputValFilesOnDiskFolder);
        
        //Status of running an Input Validator
        checkProblem.setInputValidationStatus(this.getInputValidationStatus());
        
        //TODO: save into checkProblem any Results from having run the input validator...
        
        
        //update Judging Type settings from GUI
        //TODO: should be using accessors instead of hard references for these buttons and checkboxes...
        checkProblem.setComputerJudged(computerJudgingRadioButton.isSelected());

        if (computerJudgingRadioButton.isSelected()) {
            checkProblem.setManualReview(manualReviewCheckBox.isSelected());
            if (manualReviewCheckBox.isSelected()) {
                checkProblem.setPrelimaryNotification(prelimaryNotificationCheckBox.isSelected());
            } else {
                checkProblem.setPrelimaryNotification(false);
            }
        } else {
            checkProblem.setManualReview(false);
            checkProblem.setPrelimaryNotification(false);
        }

        checkProblem.setExternalDataFileLocation(getMultipleDataSetPane().getLoadDirectory());

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 before populateProblemTestSetFilenames");
        }

        if (dataFiles == null) {
            if (lastAnsFile != null) {
                newProblemDataFiles.setJudgesAnswerFile(lastAnsFile);
            }

            if (lastDataFile != null) {
                newProblemDataFiles.setJudgesDataFile(lastDataFile);
            }

            checkProblem.addTestCaseFilenames(getName(lastAnsFile), getName(lastDataFile));

            if (outputValidatorSF != null) {
                newProblemDataFiles.setOutputValidatorFile(outputValidatorSF);
            }
        } else {
            populateProblemTestSetFilenames(checkProblem, dataFiles);
        }
        
        

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 after populateProblemTestSetFilenames");
        }

        return checkProblem;

    }
    
    
    /**
     * Makes a copy of the current Custom Validator Command line so that it can be restored if the user
     * switches back and forth between "PC2 Validator Interface" mode and "CLICS Validator Interface" mode.
     */
    private void updateLocalCustomValidatorCommandLine() {
        if (this.getUsePC2ValStdRadioButton().isSelected()) {
            localPC2InterfaceCustomValidatorCommandLine = this.getCustomValidatorCommandLineTextField().getText();
        } else if (this.getUseClicsValStdRadioButton().isSelected()) {
            localClicsInterfaceCustomValidatorCommandLine = this.getCustomValidatorCommandLineTextField().getText();
        }
    }

    private String getName(SerializedFile serializedFile) {
        if (serializedFile != null) {
            return serializedFile.getName();
        }

        return null;
    }

    /**
     * Populate the test data set file lists in Problem.
     * 
     * @param inProblem
     * @param dataFiles
     */
    private void populateProblemTestSetFilenames(Problem inProblem, ProblemDataFiles dataFiles) {

        String[] dataList = getTestDataList(dataFiles);
        String[] answerList = getTestAnswerList(dataFiles);

        inProblem.removeAllTestCaseFilenames();
        if (dataList != null) {
            for (int i = 0; i < dataList.length; i++) {
                inProblem.addTestCaseFilenames(dataList[i], answerList[i]);
            }
        }
    }

    private String[] getTestAnswerList(ProblemDataFiles dataFiles) {

        ArrayList<String> list = new ArrayList<String>();

        SerializedFile[] filelist = dataFiles.getJudgesAnswerFiles();
        SerializedFile[] dataFileList = dataFiles.getJudgesDataFiles();

        for (SerializedFile serializedFile : filelist) {
            list.add(serializedFile.getName());
        }

        padListIfNeeded(list, filelist, dataFileList);

        return (String[]) list.toArray(new String[list.size()]);
    }

    private String[] getTestDataList(ProblemDataFiles dataFiles) {

        ArrayList<String> list = new ArrayList<String>();
        SerializedFile[] filelist = dataFiles.getJudgesAnswerFiles();
        SerializedFile[] dataFileList = dataFiles.getJudgesDataFiles();

        for (SerializedFile serializedFile : dataFileList) {
            list.add(serializedFile.getName());
        }

        padListIfNeeded(list, filelist, dataFileList);

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * pad list with nulls if needed.
     * 
     * @param list
     * @param filelist
     * @param dataFileList
     */
    private void padListIfNeeded(ArrayList<String> stringList, SerializedFile[] filelist, SerializedFile[] dataFileList) {

        // find max of both lists
        int max = Math.max(filelist.length, dataFileList.length);

        for (int i = 0; i < max - stringList.size(); i++) {
            stringList.add("");
        }
    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateProblem();
                }
            });
        }
        return updateButton;
    }

    /**
     * Updates an existing contest Problem with the values specified by the current GUI fields.
     * This method is invoked by pushing the "Update" button on the EditProblemPane GUI 
     * after having entered into the GUI the new (updated) values for the problem being edited.
     * 
     * The method also gets invoked by making GUI changes to an existing problem definition,
     * then pressing "Cancel" (which displays a message "Problem Modified - Save Changes?")
     * and responding "Yes" to the message.
     * 
     */

    protected void updateProblem() {
        
//        showStackTrace();

        if (!validateProblemFields()) {
            // problem defined by the GUI fields is invalid, just return ( error message was issued by validateProblemFields() )
            return;
        }
        
        if (getInputValidatorProgramNameTextField().getText() == null || getInputValidatorProgramNameTextField().getText().equals("")) {
            //no input validator defined; issue a warning
            String warning = "You are attempting to specify a Problem which has no Input Data Validator." 
                    + "\n\nThis is usually not good practice because it provides no way to insure that the"
                    + "\nJudge's data files meet the Problem Specification."
                    + "\n\nAre you sure you want to do this?";
            int response = JOptionPane.showConfirmDialog(getParentFrame(), warning, "No Input Validator specified", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (!(response == JOptionPane.YES_OPTION)) {
                return;
            }
        }

        //all the GUI fields are valid; create a new Problem from them
        Problem newProblem = null;

        try {
            //create datafiles from the fields
            ProblemDataFiles dataFiles = getProblemDataFilesFromFields();
            
            //create a new Problem from the fields
            newProblem = getProblemFromFields(problem, dataFiles);
            
            //verify the correctness of the datafiles just obtained from the fields
            if (dataFiles != null) {
                // ensure what we got from the fields is what is actually on disk
                // enableUpdateButton() would enable if the sha1 sums changed.
                boolean changed = false;
                SerializedFile[] judgesDataFiles = dataFiles.getJudgesDataFiles();
                if (judgesDataFiles != null) {
                    if (judgesDataFiles.length > 0) {
                        for (int i = 0; i < judgesDataFiles.length; i++) {
                            SerializedFile serializedFile = judgesDataFiles[i];
                            SerializedFile serializedFile2 = new SerializedFile(serializedFile.getAbsolutePath(), newProblem.isUsingExternalDataFiles());
                            if (!serializedFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                // contents have changed on disk
                                judgesDataFiles[i] = serializedFile2;
                                changed = true;
                            }
                        }
                    } else {
                        // judgesDataFile.length is 0, but maybe the user has loaded something on the main page
                        if (problemRequiresDataCheckBox.isSelected()) {
                            // use it
                            SerializedFile serializedFile = new SerializedFile(inputDataFileLabel.getText(), newProblem.isUsingExternalDataFiles());
                            judgesDataFiles = new SerializedFile[1];
                            judgesDataFiles[0] = serializedFile;
                            changed = true;
                        }
                    }
                    if (changed) {
                        dataFiles.setJudgesDataFiles(judgesDataFiles);
                    }
                }
                SerializedFile[] judgesAnswerFiles = dataFiles.getJudgesAnswerFiles();
                if (judgesAnswerFiles != null) {
                    // compare each file
                    changed = false;
                    if (judgesAnswerFiles.length > 0) {
                        for (int i = 0; i < judgesAnswerFiles.length; i++) {
                            SerializedFile serializedFile = judgesAnswerFiles[i];
                            SerializedFile serializedFile2 = new SerializedFile(serializedFile.getAbsolutePath(), newProblem.isUsingExternalDataFiles());
                            if (!serializedFile.getSHA1sum().equals(serializedFile2.getSHA1sum())) {
                                // contents have changed on disk
                                judgesAnswerFiles[i] = serializedFile2;
                                changed = true;
                            }
                        }
                    } else {
                        // judgesAnswerFile.length is 0, but maybe the user has loaded something on the main page
                        if (judgesHaveAnswerFiles.isSelected()) {
                            // use it
                            SerializedFile serializedFile = new SerializedFile(answerFileNameLabel.getText(), newProblem.isUsingExternalDataFiles());
                            judgesAnswerFiles = new SerializedFile[1];
                            judgesAnswerFiles[0] = serializedFile;
                            changed = true;
                        }
                    }
                    if (changed) {
                        dataFiles.setJudgesAnswerFiles(judgesAnswerFiles);
                    }
                }
            }

        } catch (InvalidFieldValue e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            // showMessage(e.getMessage());
            return;
        }

        //add a Problem Letter to the problem if it doesn't have one (note: problem letter is not displayed in the GUI)
        if (newProblem.getLetter() == null || newProblem.getLetter().length() == 0) {

            // Update/Add next letter to problem.
            int problemNumber = Utilities.getProblemNumber(getContest(), problem);
            String letter = Utilities.getProblemLetter(problemNumber);
            newProblem.setLetter(letter);
        }
        
        //add the status of Input Validation (note: validation status is not displayed in the GUI)
        newProblem.setInputValidationStatus(this.getInputValidationStatus());

        //hand the new problem to the Controller for transmission to the Server
        getController().updateProblem(newProblem, newProblemDataFiles);

        //clean up the GUI state
        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Populate new data sets.
     * 
     * @param problem2
     * 
     * @param problem2
     * @return
     */
    protected ProblemDataFiles getProblemDataFilesFromFields() {

        /**
         * These are the judge data and ans from the first pane, they need to replace the first data set files.
         */

        newProblemDataFiles = multipleDataSetPane.getProblemDataFiles();

        if (debug22EditProblem) {
            Utilities.dump(newProblemDataFiles, "debug 22 in getProblemDataFilesFromFields");
        }

        return newProblemDataFiles;
    }

    /**
     * Validate that all problem fields in the GUI are ok.
     * 
     * @return true if all GUI values are valid; false otherwise
     */
    private boolean validateProblemFields() {

        //verify there is a problem name
        if (getProblemNameTextField().getText().trim().length() < 1) {
            showMessage("Enter a problem name (\"General\" tab)");
            return false;
        }

        //verify that if the PC2 Validator is selected, an option has been chosen
        if (getUsePC2ValidatorRadioButton().isSelected()) {
            if (getPc2ValidatorOptionComboBox().getSelectedIndex() < 1) {
                showMessage("PC^2 Validator is selected; you must select a Validator Mode option (\"Validator\" tab)");
                return false;
            }
        }
        
        //verify that if the CLICS validator has been selected, the tolerance fields are valid
        if (getUseCLICSValidatorRadioButton().isSelected()) {

            if (getFloatRelativeToleranceCheckBox().isSelected()) {
                String text = getFloatRelativeToleranceTextField().getText();
                try {
                    Float.parseFloat(text);
                } catch (NumberFormatException | NullPointerException e) {
                    showMessage("CLICS Validator 'Float Relative Tolerance' is selected; you must specify a valid tolerance (\"Validator\" tab)");
                    return false;
                }
            }
            
            if (getFloatAbsoluteToleranceCheckBox().isSelected()) {
                String text = getFloatAbsoluteToleranceTextField().getText();
                try {
                    Float.parseFloat(text);
                } catch (NumberFormatException | NullPointerException e) {
                    showMessage("CLICS Validator 'Float Absolute Tolerance' is selected; you must specify a valid tolerance (\"Validator\" tab)");
                    return false;
                }
            }
        }

        //verify that if a Custom Validator has been selected, there is a Validator Program specified
        if (getUseCustomValidatorRadioButton().isSelected()) {
            if (getCustomValidatorExecutableProgramTextField().getText() == null
                    || getCustomValidatorExecutableProgramTextField().getText().trim().length() < 1) {
                showMessage("\"Use Custom Validator\" is selected; you must specify Validator executable program (\"Validator\" tab)");
                return false;
            }
        }

        //verify that if a Custom Validator has been selected, there is a Validator Command specified
        if (getUseCustomValidatorRadioButton().isSelected()) {
            if (getCustomValidatorCommandLineTextField().getText() == null
                    || getCustomValidatorCommandLineTextField().getText().trim().length() < 1) {
                showMessage("\"Use Custom Validator\" is selected; you must specify Validator Command Line (\"Validator\" tab)");
                return false;
            }
        }
        
        //verify that if a Custom Validator has been selected, exactly one Validator Interface has been specified
        if (getUseCustomValidatorRadioButton().isSelected()) {
            if (!(getUseClicsValStdRadioButton().isSelected() ^ getUsePC2ValStdRadioButton().isSelected())) {  // ^ == XOR
                showMessage("\"Use Custom Validator\" is selected; you must select exactly one Validator Interface (\"Validator\" tab)");
                return false;
            }
        }

        //verify that if the problem requires data, a data file is specified
        if (getProblemRequiresDataCheckBox().isSelected()) {

            String fileName = inputDataFileLabel.getText();
            // this check is outside so we can provide a specific message
            if (fileName == null || fileName.trim().length() < 1) {
                showMessage("'Problem Requires Input Data' is checked; you must specify a data file (\"General\" tab)");
                return false;
            }

            if (fileName.trim().length() != inputDataFileLabel.getToolTipText().length()) {
                fileName = inputDataFileLabel.getToolTipText() + "";
            }

            if (!checkFile(fileName)) {
                // note: if error, then checkFile will showMessage
                return false;
            }
        }

        //verify that if "judges have answer files" is selected, a file has been specified
        if (getJudgesHaveAnswerFilesCheckbox().isSelected()) {

            //note: the Judge's Answer File name is displayed in a JLabel (not a textfield)
            String answerFileName = answerFileNameLabel.getText();

            // this check is outside so we can provide a specific message
            if (answerFileName == null || answerFileName.trim().length() == 0) {
                showMessage("\"Judges Have Provided Answer File\" is checked; you must select a Judge's Answer File (\"General\" tab)");
                return false;
            }

            if (answerFileName.trim().length() != answerFileNameLabel.getToolTipText().length()) {
                answerFileName = answerFileNameLabel.getToolTipText() + "";
            }

            if (!checkFile(answerFileName)) {
                // note: if error, then checkFile will showMessage
                return false;
            }
        }

        //verify that if computer judging is selected then a Validator has been specified
        if (getUseComputerJudgingRadioButton().isSelected()) {

            if (useNOValidatatorRadioButton.isSelected()) {
                showMessage("'Computer Judging' is selected (\"Judging Type\" tab); you must select a Validator on the \"Validator\" tab");
                return false;
            }

        }
        
        //verify that if an Input Validator program has been specified, there is a command specified to invoke it.
        // (Note that the reverse is NOT required; it would be legal to specify an Input Validator command with no explicit program name)
        if (getInputValidatorProgramNameTextField().getText() != null && !getInputValidatorProgramNameTextField().getText().equals("")) {
            if (getInputValidatorCommandTextField().getText() == null || getInputValidatorCommandTextField().getText().equals("")) {
                showMessage("An Input Validator Program has been specified; you must also specify an Input Validator command line"); 
                return false;
            }
        }

        return true;
    }

    /**
     * Checks to ensure the fileName exists, is a file, and is readable.
     * <P>
     * If error found will show Message to user.
     * 
     * @param fileName
     *            the file to check
     * @return true if the is readable
     */
    private boolean checkFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.canRead()) {
                    showMessage("Could not read file " + fileName);
                    return false;
                } // else exists, is a file, and is readable
            } else {
                // not a file
                showMessage(fileName + " is not a file");
                return false;
            }
        } else {
            Object[] options = { "Ok", "Cancel", "Ignore" };
            int n = JOptionPane.showOptionDialog(null, fileName + " does not exist", "Message", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (n < 2) {
                return false;
            } // only Ignore will fall thru to true
        }
        return true;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Problem modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addProblem();
                } else {
                    updateProblem();
                }
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public Problem getProblem() {
        return problem;
    }

    /**
     * Set Problem and ProblemDataFiles to be edited.
     */
    public void setProblem(final Problem inProblem, final ProblemDataFiles problemDataFiles) {

        problem = inProblem;
        this.newProblemDataFiles = null;
        originalProblemDataFiles = problemDataFiles;

        if (debug22EditProblem) {
            fileNameOne = createProblemReport(inProblem, problemDataFiles, "stuf1");
            Utilities.dump(originalProblemDataFiles, "debug 22   ORIGINAL  setProblem");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // first clear the old ones
                getMultipleDataSetPane().clearDataFiles();
                // now set the new ones
                try {
                    // bug 1002: if we do not send the problem here, the default for the pane will be used
                    // for the data source.
                    getMultipleDataSetPane().setProblemDataFiles(inProblem, problemDataFiles);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                // this sets the tableModel files list, which is what the getProblemDataFiles uses
                getMultipleDataSetPane().populateUI();
                populateGUI(inProblem);
                // do not automatically set this to no update, the files may have changed on disk
                if (inProblem == null) {
                    // new problem
                    enableUpdateButtons(false);
                } else {
                    enableUpdateButton();
                }
                // populatingGUI = true;
                // setForm(inProblem, problemDataFiles);
                // getAddButton().setVisible(true);
                // getUpdateButton().setVisible(false);
                // enableUpdateButtons(true);
                //
                // enableValidatorComponents();
                // enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
                // enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());

                // populatingGUI = false;
            }
        });
    }

    public MultipleDataSetPane getMultipleDataSetPane() {
        if (multipleDataSetPane == null) {
            multipleDataSetPane = new MultipleDataSetPane();
            multipleDataSetPane.setContestAndController(getContest(), getController());
            multipleDataSetPane.setParentPane(this);
        }
        return multipleDataSetPane;

    }

    /**
     * Set/populate (or remove) General tab judge's files.
     * 
     * If no first test set then clears fields.
     * 
     */
    public void setJudgingTestSetOne(ProblemDataFiles datafiles) {

        /**
         * Were fields assigned values from dataFiles ?
         */
        boolean assignedValues = false;

        if (datafiles == null) {
            deleteAllDataSets();
        } else {
            SerializedFile[] answerFiles = datafiles.getJudgesAnswerFiles();

            if (answerFiles.length > 0) {

                // Replace data files on General tab
                judgesHaveAnswerFiles.setSelected(true);
                problemRequiresDataCheckBox.setSelected(true);

                SerializedFile[] files = datafiles.getJudgesDataFiles();
                if (files.length > 0) {
                    inputDataFileLabel.setText(files[0].getName());
                    inputDataFileLabel.setToolTipText(files[0].getAbsolutePath());
                }
                files = datafiles.getJudgesAnswerFiles();
                if (files.length > 0) {
                    answerFileNameLabel.setText(files[0].getName());
                    answerFileNameLabel.setToolTipText(files[0].getAbsolutePath());
                }

                assignedValues = true;
            }
        }

        if (!assignedValues) {

            // Replace data files on General tab
            judgesHaveAnswerFiles.setSelected(false);
            problemRequiresDataCheckBox.setSelected(false);

            inputDataFileLabel.setText("");
            inputDataFileLabel.setToolTipText("");
            answerFileNameLabel.setText("");
            answerFileNameLabel.setToolTipText("");

        }

        enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
        enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());

    }

    /**
     * Remove all data sets
     */
    private void deleteAllDataSets() {
        ProblemDataFiles dataFiles = getMultipleDataSetPane().getProblemDataFiles();
        if (dataFiles != null) {
            dataFiles.removeAll();
        }
        getMultipleDataSetPane().setProblemDataFiles(dataFiles);
    }

    /**
     * Set new Problem to be edited.
     * 
     * @param problem
     */
    public void setProblem(final Problem problem) {

        this.problem = problem;
        this.newProblemDataFiles = null;
        this.originalProblemDataFiles = null;

        if (debug22EditProblem) {
            fileNameOne = createProblemReport(problem, originalProblemDataFiles, "stuf1");
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                getMultipleDataSetPane().clearDataFiles();

                populateGUI(problem);
                // do not automatically set this to no update, the files may have changed on disk
                if (problem == null) {
                    // new problem
                    enableUpdateButtons(false);

                    getMultipleDataSetPane().getInputDataStoragePanel().setEnabled(true);
                    getMultipleDataSetPane().getRdbtnCopyDataFiles().setEnabled(true);
                    getMultipleDataSetPane().getRdBtnKeepDataFilesExternal().setEnabled(true);

                } else {
                    enableUpdateButton();
                }
            }
        });
    }

    private void populateGUI(Problem inProblem) {

        populatingGUI = true;

        if (debug22EditProblem) {
            Utilities.dump(originalProblemDataFiles, "debug 22   ORIGINAL  populateGUI A");
        }

        if (inProblem != null) {

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

            setForm(inProblem, originalProblemDataFiles);

            try {
                @SuppressWarnings("unused")
                Problem changedProblem = getProblemFromFields(inProblem, originalProblemDataFiles);
            } catch (InvalidFieldValue e) {
                logException("Problem with input Problem fields", e);
                e.printStackTrace(System.err);
            }

        } else {
            //we're populating for a new problem
            clearForm();
            setInputValidationStatus(InputValidationStatus.NOT_TESTED);
        }

        enableOutputValidatorTabComponents();
        
        enableInputValidatorTabComponents();
        

        //enableGeneralTabComponents:
        enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
        enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());
        
        //enableJudgingTabComponents:  ??

        if (debug22EditProblem) {
            Utilities.dump(originalProblemDataFiles, "debug 22 ORIGINAL  populateGUI B");
        }

        try {
            getMultipleDataSetPane().setProblemDataFiles(problem, originalProblemDataFiles);
        } catch (Exception e) {
            String message = "Error loading/editing problem data files: " + e.getMessage();
            showMessage(message + " check logs.");
            getLog().log(Log.WARNING, message, e);
            if (debug22EditProblem) {
                e.printStackTrace(); // debug 22
            }
        }

        // select the general tab
        getMainTabbedPane().setSelectedIndex(0);
        populatingGUI = false;

        if (debug22EditProblem) {
            Utilities.dump(originalProblemDataFiles, "debug 22   ORIGINAL  populateGUI Z");
        }

    }

    @SuppressWarnings("unused")
    private void dumpProblem(String filename, ProblemDataFiles pdf) {

        PrintWriter out = new PrintWriter(System.out, true);
        ProblemsReport report = new ProblemsReport();
        report.setContestAndController(getContest(), getController());
        report.writeProblemDataFiles(out, pdf);

        if (filename != null) {
            try {
                FileOutputStream stream = new FileOutputStream(filename, false);
                out = new PrintWriter(stream, true);
                out.println("Problem = " + problem);
                report.writeProblemDataFiles(out, pdf);
                out.close();
                out = null;
            } catch (FileNotFoundException e) {
                e.printStackTrace(System.err);
            }
        }
        System.out.flush();
        System.err.flush();
        System.err.println("Write to vi " + filename);

    }

    /**
     * Set Form data -- that is, populates the GUI from the specified Problem and ProblemDataFiles.
     * 
     * Populates the form, no error checking is performed.
     * 
     * @param inProblem - the Problem which will be used to populate the GUI form
     * @param problemDataFiles - the ProblemDataFiles which will be used to populate the GUI form
     */
    private void setForm(Problem inProblem, ProblemDataFiles problemDataFiles) {

        problem = inProblem;
//        System.out.println (inProblem.toStringDetails());
        
        originalProblemDataFiles = problemDataFiles;

        //General tab fields:
        initializeGeneralTabFields(inProblem, problemDataFiles);

        //Output Validator tab:
        initializeOutputValidatorTabFields(inProblem, problemDataFiles);
        
        //Input Validator tab:
        initializeInputValidatorTabFields(inProblem, problemDataFiles);
        
        //Judging Type tab:
        initializeJudgingTabFields(inProblem);

        //Data Files tab:
        getMultipleDataSetPane().setLoadDirectory(inProblem.getExternalDataFileLocation());

    }
    
    /**
     * This method initializes the "General" tab GUI fields from the data in the specified Problem and ProblemDataFiles.
     * 
     * @param inProblem - the Problem to be used to initialize the GUI fields
     * @param inProblemDataFiles - the ProblemDataFiles to be used to initialize the GUI fields
     * 
     */
    private void initializeGeneralTabFields(Problem inProblem, ProblemDataFiles inProblemDataFiles) {
        
        //initialize problem description fields:
        getProblemNameTextField().setText(inProblem.getDisplayName());
        getTimeOutTextField().setText(inProblem.getTimeOutInSeconds() + "");
        getShortNameTextfield().setText(inProblem.getShortName());
        getProblemLetterTextField().setText(inProblem.getLetter()); //note: Problem Letter is currently not displayed in the GUI!

        //input data fields:
        problemRequiresDataCheckBox.setSelected(inProblem.getDataFileName() != null);
        if (inProblem.isReadInputDataFromSTDIN()) {
            fileRadioButton.setSelected(false);
            stdinRadioButton.setSelected(true);
        } else {
            fileRadioButton.setSelected(true);
            stdinRadioButton.setSelected(false);
        }
        inputDataFileLabel.setText(inProblem.getDataFileName());
        
        //judges answer fields:
        judgesHaveAnswerFiles.setSelected(inProblem.getAnswerFileName() != null);
        answerFileNameLabel.setText(inProblem.getAnswerFileName());
        
        //set ToolTips for input data and judge's answer labels to defaults
        inputDataFileLabel.setToolTipText("");
        answerFileNameLabel.setToolTipText("");

        //update ToolTips from ProblemDataFiles if available
        if (inProblemDataFiles != null) {
            SerializedFile[] files = inProblemDataFiles.getJudgesDataFiles();
            if (files.length > 0) {
                inputDataFileLabel.setToolTipText(files[0].getAbsolutePath());
            }
            files = inProblemDataFiles.getJudgesAnswerFiles();
            if (files.length > 0) {
                answerFileNameLabel.setToolTipText(files[0].getAbsolutePath());
            }
        }

        //miscellaneous fields:
        getDoShowOutputWindowCheckBox().setSelected(!inProblem.isHideOutputWindow());
        getShowCompareCheckBox().setSelected(inProblem.isShowCompareWindow());
        getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());

        getDeleteProblemCheckBox().setSelected(!inProblem.isActive());
    }

    /**
     * Sets the Judging Type radio and checkboxes in a sane manner.
     */
    private void initializeJudgingTabFields(Problem inProblem) {
    	
    	//TODO: all the following buttons and checkboxes should be accessed via accessors, not direct references...
        if (inProblem != null && inProblem.isComputerJudged()) {
            computerJudgingRadioButton.setSelected(true);
            manualReviewCheckBox.setSelected(inProblem.isManualReview());
            manualReviewCheckBox.setEnabled(true);

            prelimaryNotificationCheckBox.setSelected(inProblem.isPrelimaryNotification());
            if (manualReviewCheckBox.isSelected()) {
                prelimaryNotificationCheckBox.setEnabled(true);
            } else {
                prelimaryNotificationCheckBox.setEnabled(false);
            }
        } else {
            computerJudgingRadioButton.setSelected(false);
            manualJudgingRadioButton.setSelected(true);
            if (inProblem == null) {
                manualReviewCheckBox.setSelected(false);
                prelimaryNotificationCheckBox.setSelected(false);
            } else {
                manualReviewCheckBox.setSelected(inProblem.isManualReview());
                prelimaryNotificationCheckBox.setSelected(inProblem.isPrelimaryNotification());
            }

            manualReviewCheckBox.setEnabled(false);
            prelimaryNotificationCheckBox.setEnabled(false);
        }
    }
    
    /**
     * Initializes the Validator tab (GUI pane) from the specified Problem and ProblemDataFiles.
     * 
     * @param inProblem the {@link Problem} to be used to initialize the GUI
     * @param inProblemDataFiles the {@link ProblemDataFiles} to be used to initialize the GUI
     * 
     * @throws {@link RuntimeException} if either the received Problem or ProblemDataFiles are null
     * @throws {@link InvalidFieldValue} if the received Problem contains unspecified (but required) values
     */
    private void initializeOutputValidatorTabFields(Problem inProblem, ProblemDataFiles inProblemDataFiles) {
        
        if (inProblem==null || inProblemDataFiles==null) {
            throw new RuntimeException("EditProblemPane.initializeValidatorTabFields(): received null Problem or ProblemDataFiles");
        }
        
        //get what type of validator (if any) is specified in the problem
        VALIDATOR_TYPE validatorType = inProblem.getValidatorType();
        
        //enable the corresponding validator selection radio button (the ButtonGroup will disable all the others)
        switch (validatorType) {
            case NONE:
                getUseNOValidatatorRadioButton().setSelected(true);
                break;
            case PC2VALIDATOR:
                getUsePC2ValidatorRadioButton().setSelected(true);
                break;
            case CLICSVALIDATOR:
                getUseCLICSValidatorRadioButton().setSelected(true);
                break;
            case CUSTOMVALIDATOR:
                getUseCustomValidatorRadioButton().setSelected(true);
                break;
            default:
                throw new InvalidFieldValue("Unknown Validator type: '" + validatorType + "'");
        }
        
        //update the PC2 Validator settings in the GUI from the Problem
        PC2ValidatorSettings pc2ValSettings = inProblem.getPC2ValidatorSettings();
        if (pc2ValSettings!=null) {
            getPc2ValidatorOptionComboBox().setSelectedIndex(pc2ValSettings.getWhichPC2Validator());
            getPc2ValidatorIgnoreCaseCheckBox().setSelected(pc2ValSettings.isIgnoreCaseOnValidation());
        } else {
            throw new InvalidFieldValue("EditProblemPane.initializeValidatorTabFields(): null PC2 Validator Settings in received Problem");
        }

        //update the Clics Validator settings in the GUI from the Problem
        ClicsValidatorSettings clicsValSettings = inProblem.getClicsValidatorSettings();
        if (clicsValSettings!=null) {
            getCLICSValidatorCaseSensitiveCheckBox().setSelected(clicsValSettings.isCaseSensitive());
            getCLICSSpaceSensitiveCheckBox().setSelected(clicsValSettings.isSpaceSensitive());
            getFloatAbsoluteToleranceCheckBox().setSelected(clicsValSettings.isFloatAbsoluteToleranceSpecified());
            if (getFloatAbsoluteToleranceCheckBox().isSelected()) {
                getFloatAbsoluteToleranceTextField().setText(clicsValSettings.getFloatAbsoluteTolerance()+"");
            } else {
                getFloatAbsoluteToleranceTextField().setText("");   
            }
            getFloatRelativeToleranceCheckBox().setSelected(clicsValSettings.isFloatRelativeToleranceSpecified());
            if (getFloatRelativeToleranceCheckBox().isSelected()) {
                getFloatRelativeToleranceTextField().setText(clicsValSettings.getFloatRelativeTolerance()+"");
            } else {
                getFloatRelativeToleranceTextField().setText("");   
            }
        } else {
            throw new InvalidFieldValue("EditProblemPane.initializeValidatorTabFields(): null CLICS Validator Settings in received Problem");
        }
  
        //update the Custom Validator settings in the GUI from the Problem
        CustomValidatorSettings customSettings = inProblem.getCustomValidatorSettings().clone();
//        System.out.println (customSettings);
        if (customSettings!=null) {
            
            //get the Validator Program name
            String validatorFileName = customSettings.getCustomValidatorProgramName();  
            
            //if there's a Serialized validator file we should use the full path name from there
            SerializedFile validatorFile = inProblemDataFiles.getOutputValidatorFile();
            if (validatorFile!=null) {
                validatorFileName = inProblemDataFiles.getOutputValidatorFile().getAbsolutePath();
            }
            //put the Validator Program name into the GUI
            getCustomValidatorExecutableProgramTextField().setText(validatorFileName);
            getCustomValidatorExecutableProgramTextField().setToolTipText(validatorFileName);
            
            //set the radio buttons indicating which Validator Standard the Problem uses
            getUsePC2ValStdRadioButton().setSelected(customSettings.isUsePC2ValidatorInterface());
            getUseClicsValStdRadioButton().setSelected(customSettings.isUseClicsValidatorInterface());
            
            //set the custom validator command line based on which standard is being used (the CustomValidatorSettings object
            // stores command lines for both cases; the active one needs to be put into the GUI as well as into the local (temp) storage
            // while the other one needs to be put in the local temp storage only)
            if (getUsePC2ValStdRadioButton().isSelected()) {
                //Problem is currently using the PC2 interface; put that command line in the GUI and the local storage
                getCustomValidatorCommandLineTextField().setText(customSettings.getCustomValidatorCommandLine());
                localPC2InterfaceCustomValidatorCommandLine = customSettings.getCustomValidatorCommandLine();
                //get the CLICS Interface command line out of the cloned settings and save it locally
                customSettings.setUseClicsValidatorInterface(); //note customSettings is a clone; this doesn't affect the original problem settings
                localClicsInterfaceCustomValidatorCommandLine = customSettings.getCustomValidatorCommandLine();
            } else {
                //Problem is currently using the CLICS interface; put that in the GUI and the local storage
                getCustomValidatorCommandLineTextField().setText(customSettings.getCustomValidatorCommandLine());
                localClicsInterfaceCustomValidatorCommandLine = customSettings.getCustomValidatorCommandLine();
                //get the PC2 Interface command line out of the cloned settings and save it locally
                customSettings.setUsePC2ValidatorInterface();
                localPC2InterfaceCustomValidatorCommandLine = customSettings.getCustomValidatorCommandLine();
            }
            
            //set the Command Line ToolTip to "same as Command Line"
            getCustomValidatorCommandLineTextField().setToolTipText(inProblem.getCustomValidatorSettings().getCustomValidatorCommandLine());
            
        } else {
            throw new InvalidFieldValue("EditProblemPane.initializeValidatorTabFields(): null Custom Validator Settings in received Problem");
        }

        //update the SVTJ checkbox from the received Problem
        getShowValidatorToJudgesCheckBox().setSelected(inProblem.isShowValidationToJudges());

    }
    
    /**
     * Sets the Input Validator pane fields from the specified Problem information.
     * 
     * @param prob - the problem used to set the pane data
     * @param probDataFiles - ProblemDataFiles associated with the specified Problem
     */
    private void initializeInputValidatorTabFields (Problem prob, ProblemDataFiles probDataFiles) {
        
        //fill in the input validator program name 
        String inputValidatorProg = prob.getInputValidatorProgramName();
        if (inputValidatorProg != null) {
            getInputValidatorProgramNameTextField().setText(inputValidatorProg);
        } else {
            getInputValidatorProgramNameTextField().setText("");
        }
        //update the tooltip to reflect the name in the text field
        if (getInputValidatorProgramNameTextField().getText() == null || getInputValidatorProgramNameTextField().getText().equals("")) {
            //set the tooltip null (otherwise we get a little sliver of an empty-string tooltip)
            getInputValidatorProgramNameTextField().setToolTipText(null);
        } else {
            getInputValidatorProgramNameTextField().setToolTipText(getInputValidatorProgramNameTextField().getText());
        }
        
        //fill in the input validator command
        String inputValidatorCmd = prob.getInputValidatorCommandLine();
        if (inputValidatorCmd != null) {
            getInputValidatorCommandTextField().setText(inputValidatorCmd);
        } else {
            getInputValidatorCommandTextField().setText("");
        }
        //update the tooltip to reflect the name in the text field
        if (getInputValidatorCommandTextField().getText() == null || getInputValidatorCommandTextField().getText().equals("")) {
            //set the tooltip null (otherwise we get a little sliver of an empty-string tooltip)
            getInputValidatorCommandTextField().setToolTipText(null);
        } else {
            getInputValidatorCommandTextField().setToolTipText(getInputValidatorCommandTextField().getText());
        }
        
        //fill in the "Validate files on disk" field
        String filesOnDiskFolder = prob.getInputValidatorFilesOnDiskFolder() ;
        if (filesOnDiskFolder != null) {
            getInputValidatorFilesOnDiskTextField().setText(filesOnDiskFolder);
        } else {
            getInputValidatorFilesOnDiskTextField().setText("");
        }
        //update the files on disk tooltip to reflect the name in the files on disk text field
        if (getInputValidatorFilesOnDiskTextField().getText() == null || getInputValidatorFilesOnDiskTextField().getText().equals("")) {
            //set the tooltip null (otherwise we get a little sliver of an empty-string tooltip)
            getInputValidatorFilesOnDiskTextField().setToolTipText(null);
        } else {
            getInputValidatorFilesOnDiskTextField().setToolTipText(getInputValidatorFilesOnDiskTextField().getText());
        }

        //default to "files on disk" as file source (the ButtonGroup will automatically de-select the other buttons)
        getFilesOnDiskInFolderRadioButton().setSelected(true);
        
        //clear the results table
        //TODO: probably should be saving the results of a previous Input Validation run (if any) in the problem, and loading THOSE here
        ((InputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).setResults(null);
        ((InputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).fireTableDataChanged();
        
        //set the Status message based on the status in the specified problem
        InputValidationStatus problemValidationStatus = prob.getInputValidationStatus();
        Color color ;
        String msg ;
        switch (problemValidationStatus) {
            case NOT_TESTED:
                msg = "<No Input Validation test run yet>";
                color = Color.BLACK;
                break;
            case PASSED:
                msg = "All input data files PASSED validation";
                color = Color.GREEN;
                break;
            case FAILED:
                msg = "One or more input data files FAILED validation";
                color = Color.red;
                break;
            case ERROR:
                msg = "Error occurred during input validation; check logs";
                color = Color.YELLOW;
                break;
            default:
                msg = "This should never be displayed";
                color = Color.ORANGE;
        }
                
        getInputValidationResultSummaryTextLabel().setText(msg);
        getInputValidationResultSummaryTextLabel().setForeground(color);
        setInputValidationStatus(prob.getInputValidationStatus());  //note: validation status variable is not displayed on the GUI

    }
    
    

    /**
     * update/enable Update button.
     * 
     * @param fieldsChanged
     *            if false assumes changest must be undone aka Canceled.
     */
    protected void enableUpdateButtons(boolean fieldsChanged) {
        if (fieldsChanged) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }

        if (getUpdateButton().isVisible()) {
            getUpdateButton().setEnabled(fieldsChanged);
        } else {
            getAddButton().setEnabled(fieldsChanged);
        }
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
            mainTabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    if (sourceTabbedPane.getTitleAt(index).equals("Input Validator")) {
                        updateRunValidatorButtonState();
                    }
                }
            });
            mainTabbedPane.setPreferredSize(new Dimension(500, 600));
            mainTabbedPane.insertTab("Input Validator", null, getInputValidatorPane(), null, 0);
            mainTabbedPane.insertTab("Input Data Files", null, getMultipleDataSetPane(), null, 0);
            mainTabbedPane.insertTab("Output Validator", null, getOutputValidatorPane(), null, 0);
            mainTabbedPane.insertTab("Judging Type", null, getJudgingTypePanel(), null, 0);
            mainTabbedPane.insertTab("General", null, getGeneralPane(), null, 0);
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJudgingTypePanel() {
        if (judgingTypePane == null) {
            judgingTypePane = new JPanel();
            judgingTypePane.setLayout(new BorderLayout());
            judgingTypePane.add(getJudgeTypeInnerPane(), BorderLayout.NORTH);
            getJudgingTypeGroup().setSelected(getManualJudgingRadioButton().getModel(), true);
        }
        return judgingTypePane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPane() {
        if (generalPane == null) {
            timeoutLabel = new JLabel();
            timeoutLabel.setBounds(new Rectangle(23, 46, 150, 16));
            timeoutLabel.setText("Run Timeout Limit (Secs)");
            problemNameLabel = new JLabel();
            problemNameLabel.setBounds(new Rectangle(23, 14, 150, 16));
            problemNameLabel.setText("Problem name");
            generalPane = new JPanel();
            generalPane.setLayout(null);
            generalPane.add(getProblemNameTextField(), null);
            generalPane.add(getTimeOutTextField(), null);
            generalPane.add(getProblemRequiresDataCheckBox(), null);
            generalPane.add(getDataProblemPane(), null);
            generalPane.add(getJudgesHaveAnswerFilesCheckbox(), null);
            generalPane.add(getAnswerFilePane(), null);
            generalPane.add(problemNameLabel, null);
            generalPane.add(timeoutLabel, null);
            generalPane.add(getShowCompareCheckBox(), null);
            generalPane.add(getDoShowOutputWindowCheckBox(), null);
            generalPane.add(getDeleteProblemCheckBox(), null);

            generalPane.add(getProblemLetterTextField(), null);

            JLabel lblShortName = new JLabel();
            lblShortName.setText("Short Name");
            lblShortName.setBounds(new Rectangle(23, 14, 179, 16));
            lblShortName.setBounds(285, 46, 84, 16);
            generalPane.add(lblShortName);

            shortNameTextfield = new JTextField();
            shortNameTextfield.setPreferredSize(new Dimension(120, 20));
            shortNameTextfield.setBounds(new Rectangle(220, 44, 120, 20));
            shortNameTextfield.setBounds(379, 44, 97, 20);
            shortNameTextfield.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
            generalPane.add(shortNameTextfield);
        }
        return generalPane;
    }

    /**
     * This method initializes problemNameTextField
     * 
     * @return javax.swing.JTextField
     */
    protected JTextField getProblemNameTextField() {
        if (problemNameTextField == null) {
            problemNameTextField = new JTextField();
            problemNameTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            problemNameTextField.setSize(new Dimension(293, 20));
            problemNameTextField.setLocation(new Point(183, 12));
            problemNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return problemNameTextField;
    }

    /**
     * This method initializes the timeOut textfield
     * 
     * @return javax.swing.JTextField holding the timeOut
     */
    private JTextField getTimeOutTextField() {
        if (timeOutSecondTextField == null) {
            timeOutSecondTextField = new JTextField();
            timeOutSecondTextField.setBounds(new Rectangle(183, 44, 74, 20));
            timeOutSecondTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            timeOutSecondTextField.setDocument(new IntegerDocument());
            timeOutSecondTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return timeOutSecondTextField;
    }

    public JTextField getProblemLetterTextField() {
        // SOMEDAY - add field to form, define and make visible.
        if (problemLetterTextField == null) {
            problemLetterTextField = new JTextField();
        }
        return problemLetterTextField;
    }

    /**
     * This method initializes the problemRequiresData checkbox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getProblemRequiresDataCheckBox() {
        if (problemRequiresDataCheckBox == null) {
            problemRequiresDataCheckBox = new JCheckBox();
            problemRequiresDataCheckBox.setBounds(new java.awt.Rectangle(23, 76, 257, 26));
            problemRequiresDataCheckBox.setText("Problem Requires Input Data");
            problemRequiresDataCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
                    enableUpdateButton();
                }
            });
        }
        return problemRequiresDataCheckBox;
    }

    protected void enableRequiresInputDataComponents(boolean enableButtons) {
        getInputDataFilePane().setEnabled(enableButtons);
        getFileRadioButton().setEnabled(enableButtons);
        getStdinRadioButton().setEnabled(enableButtons);
        getTeamReadsFromPane().setEnabled(enableButtons);
        getSelectFileButton().setEnabled(enableButtons);
        getInputDataFilePane().setEnabled(enableButtons);
        getFileNamePane().setEnabled(enableButtons);
        getInputDataFilePane().setEnabled(enableButtons);
        getFileNamePane().setEnabled(enableButtons);
    }

    /**
     * This method initializes DataProblemPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDataProblemPane() {
        if (dataProblemPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            gridLayout.setHgap(5);
            gridLayout.setVgap(6);
            dataProblemPane = new JPanel();
            dataProblemPane.setLayout(gridLayout);
            dataProblemPane.setBounds(new java.awt.Rectangle(53, 114, 423, 113));
            dataProblemPane.add(getTeamReadsFromPane(), null);
            dataProblemPane.add(getInputDataFilePane(), null);
        }
        return dataProblemPane;
    }

    /**
     * This method initializes the TeamReadsFrom Pane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getTeamReadsFromPane() {
        if (readsFromPane == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setHgap(35);
            flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
            flowLayout1.setVgap(0);
            readsFromPane = new JPanel();
            readsFromPane.setLayout(flowLayout1);
            readsFromPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Team Reads From", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            readsFromPane.add(getFileRadioButton(), null);
            readsFromPane.add(getStdinRadioButton(), null);
            getTeamReadsFrombuttonGroup().setSelected(getFileRadioButton().getModel(), true);
        }
        return readsFromPane;
    }

    /**
     * This method initializes inputDataFilePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInputDataFilePane() {
        if (inputDataFilePane == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(15);
            borderLayout.setVgap(5);
            inputDataFilePane = new JPanel();
            inputDataFilePane.setLayout(borderLayout);
            inputDataFilePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Data File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            inputDataFilePane.setPreferredSize(new java.awt.Dimension(98, 45));
            inputDataFilePane.add(getFileNamePane(), java.awt.BorderLayout.CENTER);
            inputDataFilePane.add(getSelectFileButton(), java.awt.BorderLayout.EAST);
        }
        return inputDataFilePane;
    }

    /**
     * This method initializes stdinRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getStdinRadioButton() {
        if (stdinRadioButton == null) {
            stdinRadioButton = new JRadioButton();
            stdinRadioButton.setText("Stdin");
            stdinRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return stdinRadioButton;
    }

    /**
     * This method initializes fileRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getFileRadioButton() {
        if (fileRadioButton == null) {
            fileRadioButton = new JRadioButton();
            fileRadioButton.setText("File");
            fileRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return fileRadioButton;
    }

    /**
     * This method initializes fileNamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFileNamePane() {
        if (fileNamePane == null) {
            inputDataFileLabel = new JLabel();
            inputDataFileLabel.setText("");
            fileNamePane = new JPanel();
            fileNamePane.setLayout(new BorderLayout());
            fileNamePane.add(inputDataFileLabel, java.awt.BorderLayout.CENTER);
        }
        return fileNamePane;
    }

    /**
     * This method initializes selectFileButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSelectFileButton() {
        if (selectFileButton == null) {
            selectFileButton = new JButton();
            selectFileButton.setText("Browse");
            selectFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(inputDataFileLabel, "Open Input Data File")) {
                        inputDataFileLabel.setToolTipText(inputDataFileLabel.getText());
                        ProblemDataFiles datafiles = multipleDataSetPane.getProblemDataFiles();
                        if (datafiles != null) {
                            SerializedFile[] sFiles = datafiles.getJudgesDataFiles();
                            if (sFiles != null && sFiles.length > 0) {
                                sFiles[0] = new SerializedFile(inputDataFileLabel.getText());
                            } else {
                                sFiles = new SerializedFile[1];
                                sFiles[0] = new SerializedFile(inputDataFileLabel.getText());
                            }
                            datafiles.setJudgesDataFiles(sFiles);
                            multipleDataSetPane.setProblemDataFiles(datafiles);
                        } // else previous handling will take care of it
                    }
                    enableUpdateButton();
                }
            });
        }
        return selectFileButton;
    }

    /**
     * This method initializes the JudgesHaveAnswerFiles CheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJudgesHaveAnswerFilesCheckbox() {
        if (judgesHaveAnswerFiles == null) {
            judgesHaveAnswerFiles = new JCheckBox();
            judgesHaveAnswerFiles.setBounds(new java.awt.Rectangle(23, 239, 302, 24));
            judgesHaveAnswerFiles.setText("Judges Have Provided Answer File");
            judgesHaveAnswerFiles.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());
                    enableUpdateButton();
                }
            });
        }
        return judgesHaveAnswerFiles;
    }

    protected void enableProvideAnswerFileComponents(boolean enableComponents) {
        getAnswerFilenamePane().setEnabled(enableComponents);
        getAnswerBrowseButton().setEnabled(enableComponents);
        answerFileNameLabel.setEnabled(enableComponents);
        answerFilePane.setEnabled(enableComponents);
    }

    /**
     * This method initializes answerFilePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerFilePane() {
        if (answerFilePane == null) {
            BorderLayout borderLayout1 = new BorderLayout();
            borderLayout1.setHgap(15);
            borderLayout1.setVgap(5);
            answerFilePane = new JPanel();
            answerFilePane.setLayout(borderLayout1);
            answerFilePane.setBounds(new java.awt.Rectangle(53, 275, 423, 52));
            answerFilePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            answerFilePane.add(getAnswerFilenamePane(), java.awt.BorderLayout.CENTER);
            answerFilePane.add(getAnswerBrowseButton(), java.awt.BorderLayout.EAST);
        }
        return answerFilePane;
    }

    /**
     * This method initializes answerFilenamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerFilenamePane() {
        if (answerFilenamePane == null) {
            answerFileNameLabel = new JLabel();
            answerFileNameLabel.setText("");
            answerFilenamePane = new JPanel();
            answerFilenamePane.setLayout(new BorderLayout());
            answerFilenamePane.add(answerFileNameLabel, java.awt.BorderLayout.CENTER);
        }
        return answerFilenamePane;
    }

    /**
     * This method initializes answerBrowseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAnswerBrowseButton() {
        if (answerBrowseButton == null) {
            answerBrowseButton = new JButton();
            answerBrowseButton.setText("Browse");
            answerBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(answerFileNameLabel, "Open Judges Answer File")) {
                        answerFileNameLabel.setToolTipText(answerFileNameLabel.getText());
                        ProblemDataFiles datafiles = multipleDataSetPane.getProblemDataFiles();
                        if (datafiles != null) {
                            SerializedFile[] sFiles = datafiles.getJudgesAnswerFiles();
                            if (sFiles.length > 0) {
                                sFiles[0] = new SerializedFile(answerFileNameLabel.getText());
                            } else {
                                sFiles = new SerializedFile[1];
                                sFiles[0] = new SerializedFile(answerFileNameLabel.getText());
                            }
                            datafiles.setJudgesAnswerFiles(sFiles);
                            multipleDataSetPane.setProblemDataFiles(datafiles);
                        } // else previous handling will take care of it
                    }
                    enableUpdateButton();
                }
            });
        }
        return answerBrowseButton;
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }

    /**
     * select file, if file picked updates label.
     * 
     * @param label
     * @param dialogTitle
     *            title for file chooser
     * @return True if a file was select and label updated
     * @throws Exception
     */
    private boolean selectFile(JLabel label, String dialogTitle) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = label.getToolTipText();
        String startDir;
        if (oldFile.equalsIgnoreCase("")) {
            startDir = lastDirectory;
        } else {
            startDir = oldFile;
        }
        JFileChooser chooser = new JFileChooser(startDir);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                label.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (Exception e) {
            getLog().log(Log.INFO, "Error getting selected file, try again.", e);
            result = false;
        }
        chooser = null;
        return result;
    }

    /**
     * select file, if file picked updates specified JTextField.
     * 
     * @param textField -- a JTextField whose value will be updated if a file is chosen
     * @param dialogTitle
     *            title for file chooser
     * @return True if a file was select and the JTextField updated
     * @throws Exception
     */
    private boolean selectFile(JTextField textField, String dialogTitle) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = textField.getToolTipText();
        String startDir;
        if (oldFile.equalsIgnoreCase("")) { //<<--- consider adding a check for null to this
            startDir = lastDirectory;
        } else {
            startDir = oldFile;
        }
        JFileChooser chooser = new JFileChooser(startDir);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                textField.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (Exception e) {
            showMessage("Error getting selected file, try again: \n" + e.getMessage());
            getLog().log(Log.INFO, "Error getting selected file: ", e);
            result = false;
        }
        chooser = null;
        return result;
    }

    /**
     * Displays a JFileChooser allowing the user to select a directory.
     * 
     * Returns the selected directory name, or null if no directory was selected. 
     * Also updates the specified JTextField to contain the selected directory name.
     * 
     * @param dialogTitle a String giving the title to display on the dialog
     * 
     * @return a String giving the name of the chosen directory
     */
    private String selectDirectory(JTextField textField, String dialogTitle) {

        String chosenDir = null;
        
        String startDir = null;
        if (textField != null) {
            String toolTip = textField.getToolTipText();
            if (toolTip != null && !toolTip.equals("")) {
                startDir = toolTip;
            }
        }

        JFileChooser chooser = new JFileChooser(startDir);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (dialogTitle != null) {
            chooser.setDialogTitle(dialogTitle);
        }
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                chosenDir = chooser.getSelectedFile().toString();
                textField.setText(chosenDir);
            }
        } catch (Exception e) {
            showMessage("Error selecting input folder, try again: \n" + e.getMessage());
            getController().getLog().log(Log.INFO, "Error in JFileChooser getting selected directory", e);
        }
        chooser = null;
        return chosenDir;
        
         
    }

    /**
     * This method initializes teamReadsFrombuttonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getTeamReadsFrombuttonGroup() {
        if (teamReadsFrombuttonGroup == null) {
            teamReadsFrombuttonGroup = new ButtonGroup();
            teamReadsFrombuttonGroup.add(getStdinRadioButton());
            teamReadsFrombuttonGroup.add(getFileRadioButton());
        }
        return teamReadsFrombuttonGroup;
    }

    private ButtonGroup getJudgingTypeGroup() {
        if (judgingTypeGroup == null) {
            judgingTypeGroup = new ButtonGroup();
            judgingTypeGroup.add(getUseComputerJudgingRadioButton());
            judgingTypeGroup.add(getManualJudgingRadioButton());
        }
        return judgingTypeGroup;
    }

    /**
     * This method initializes the Output Validator Pane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getOutputValidatorPane() {
        if (outputValidatorPane == null) {
            outputValidatorPane = new JPanel();
            outputValidatorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            outputValidatorPane.setAlignmentY(Component.TOP_ALIGNMENT);
            outputValidatorPane.setMaximumSize(new Dimension(500, 400));
            outputValidatorPane.setLayout(new BoxLayout(outputValidatorPane, BoxLayout.Y_AXIS));
            outputValidatorPane.add(getVerticalStrut_4());
            outputValidatorPane.add(getNoValidatorPanel());
            outputValidatorPane.add(getVerticalStrut_1());
            outputValidatorPane.add(getPc2ValidatorPanel());
            outputValidatorPane.add(getVerticalStrut_5());
            outputValidatorPane.add(getClicsValidatorPanel());
            outputValidatorPane.add(getVerticalStrut());
            outputValidatorPane.add(getCustomValidatorPanel());
            outputValidatorPane.add(getVerticalStrut_2());
            outputValidatorPane.add(getShowValidatorToJudgesCheckBox());
            outputValidatorPane.add(getVerticalStrut_3());
            getValidatorChoiceButtonGroup().setSelected(getUseNOValidatatorRadioButton().getModel(), true);
        }
        return outputValidatorPane;
    }

    /**
     * This method initializes the useNoValidator RadioButton.
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseNOValidatatorRadioButton() {
        if (useNOValidatatorRadioButton == null) {
            useNOValidatatorRadioButton = new JRadioButton();
            useNOValidatatorRadioButton.setText("Do not use Validator");
            useNOValidatatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableOutputValidatorTabComponents();
                    enableUpdateButton();
                }
            });
        }
        return useNOValidatatorRadioButton;
    }

    
    private void enableInputValidatorTabComponents() {
        updateRunValidatorButtonState();
    }
    
    protected void enableOutputValidatorTabComponents() {
        if (getUsePC2ValidatorRadioButton().isSelected()) {
            enablePC2ValidatorComponents(true);
            enableClicsValidatorComponents(false);
            enableCustomValidatorComponents(false);
            getShowValidatorToJudgesCheckBox().setEnabled(true);
        } else if (getUseCLICSValidatorRadioButton().isSelected()) {
            enablePC2ValidatorComponents(false);
            enableClicsValidatorComponents(true);
            enableCustomValidatorComponents(false);
            getShowValidatorToJudgesCheckBox().setEnabled(true);
        } else if (getUseCustomValidatorRadioButton().isSelected()) {
            enablePC2ValidatorComponents(false);
            enableClicsValidatorComponents(false);
            enableCustomValidatorComponents(true);
            getShowValidatorToJudgesCheckBox().setEnabled(true);
        } else {
            // No validator used
            enablePC2ValidatorComponents(false);
            enableClicsValidatorComponents(false);
            enableCustomValidatorComponents(false);
            getShowValidatorToJudgesCheckBox().setEnabled(false);
        }
    }

    private void enablePC2ValidatorComponents(boolean enableComponents) {
        getPc2ValidatorOptionsSubPanel().setEnabled(enableComponents);
        getPc2ValidatorOptionComboBoxLabel().setEnabled(enableComponents);
        getPc2ValidatorOptionComboBox().setEnabled(enableComponents);
        getPc2ValidatorIgnoreCaseCheckBox().setEnabled(enableComponents);
    }

    /**
     * This method initializes the CLICS Validator jRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseCLICSValidatorRadioButton() {
        if (useCLICSValidatorRadioButton == null) {
            useCLICSValidatorRadioButton = new JRadioButton();
            useCLICSValidatorRadioButton.setMargin(new Insets(2, 12, 2, 2));
            useCLICSValidatorRadioButton.setText("Use CLICS Validator");
            useCLICSValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableOutputValidatorTabComponents();
                    enableUpdateButton();
                }
            });
        }
        return useCLICSValidatorRadioButton;
    }

    private JLabel getLblWhatsThisCLICSValidator() {
        if (lblWhatsThisCLICSValidator == null) {
//            lblWhatsThis = new JLabel("<What's This?>");
//            lblWhatsThis.setForeground(Color.blue);

            ImageIcon iconImage = (ImageIcon) UIManager.getIcon("OptionPane.questionIcon");
            Image image = iconImage.getImage();
            lblWhatsThisCLICSValidator = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            lblWhatsThisCLICSValidator.setToolTipText("What's This? (click for additional information");
            lblWhatsThisCLICSValidator.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisCLICSValidatorMessage, "CLICS Validator", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
            lblWhatsThisCLICSValidator.setBorder(new EmptyBorder(0, 15, 0, 0));
        }
        return lblWhatsThisCLICSValidator;
    }
    
    private String whatsThisCLICSValidatorMessage = "Selecting this option allows you to use the PC^2 implementation of the \"CLICS Validator\"."
            
            + "\n\nCLICS is the Competitive Learning Initiative Contest System specification, used among other things to define "
            + "\nrequirements for Contest Control Systems used at the ICPC World Finals. The CLICS specification includes a"
            + "\ndefinition for a \"standard validator\" used as the default when no other validator is selected."
            
            + "\n\nThe CLICS Validator \"tokenizes\" the Judge's Answer file and the Team Output file, ignoring case and whitespace"
            + "\nby default, and determines \"equivalence\" by comparing the corresponding tokens."
            
            + "\n\nOptions allow the user to require case-sensitivity and/or \"space-sensitivity\" (i.e., an exact match in whitespace),"
            + "\nand to specify tolerance requirements which floating-point tokens must meet to be considered equal."
            
            + "\n\nIf both absolute and relative tolerance values are specified, floating-point tokens are considered equivalent if"
            + "\nif they match within EITHER of the specified tolerances.  If neither absolute nor relative tolerance is specified,"
            + "\nfloating-point tokens must match character-for-character to be considered equivalent."
            
            + "\n\nFor more information, see the CLICS specification at https://clics.ecs.baylor.edu/index.php/Problem_format#Validators.  ";

    private String whatsThisPC2ValStdMessage = "Selecting this option indicates that your Validator is going to interface with PC^2 using the \"PC^2 Validator Standard\"."
            
            + "\n\n In this mode, PC^2 passes to the Validator program a set of four string parameters, in the following order:" 
            + "\n  (1) the name of the input data file which was used to test the program whose output is being validated; " 
            + "\n  (2) the name of the output file which was produced by the program being validated when it was run using the specified input data file; " 
            + "\n  (3) the name of an \"answer file\" which is input to the Validator (typically, the \"correct answer\" for the problem); and"
            + "\n  (4) the name of a \"result file\" which the Validator must produce."
  
            + "\n\n Your Validator is responsible for accepting the above parameters and producing the specified \"result file\"."
            + "\n The \"result file\" must be a valid XML document with a header of the form \"<?xml version=\"1.0\"?>\""
            + "\n and a root element of the form \"<result outcome = \"string1\">   string2  </result>\" "
            + "\n where \"string1\" is \"accepted\" for correct runs or some other string for rejected runs."
            
            + "\n\n  If \"string1\" is \"accepted\" then PC^2 assigns \"YES\" to the run; otherwise, PC^2 compares \"string1\" with the set of currently-defined" 
            + "\n \"judgement messages\"; if a match is found then PC^2 assigns that judgement as the result for the run;"
            + "\n  otherwise it assigns \"Undetermined\" as the result for the run.  The value of \"string2\" is ignored. "
            
            + "\n\n For additional information, see the PC^2 Contest Administrator's Guide; in particular, the Appendix on Validators."

            ;

    private String whatsThisCLICSValStdMessage = "Selecting this option indicates that your Validator is going to interface with PC^2 using the \"CLICS Validator Standard\"."
            
            + "\n\nCLICS is the Competitive Learning Initiative Contest System specification, used among other things to define "
            + "\nrequirements for Contest Control Systems used at the ICPC World Finals. "
            
            + "\n\n In this mode, PC^2 passes to the Validator program a set of three string parameters, in the following order: " 
            + "\n  (1) the name of the input data file which was used to test the program whose output is being validated; " 
            + "\n  (2) the name of an \"answer file\" which is input to the Validator (typically, the \"correct answer\" for the problem); and"
            + "\n  (3) the name of a \"feedback directory\" into which the Validator can place \"feedback files\" in order to report additional information"
            + "\n      on the validation of the output of the program being validated.  The \"feedback directory\" name will end with a \"path separator\" character ('/' or '\\')."
            
            + "\n\n When using a CLICS Validator, PC^2 arranges that the content of the output file which was produced by the program being validated "
            + "\n is sent to the \"standard input\" of the Validator program."
  
            + "\n\n The CLICS Validator Standard specifies that the Validator is responsible for accepting the above parameters (along with the data on its standard input)"
            + "\n and exiting with an exit code of 42 if the submission which produced the given output is to be accepted (i.e., judged \"Yes\"),"
            + "\n or exiting with an exit code of 43 if the submission is to be rejected (i.e., judged \"No - Wrong Answer\")."
            
            + "\n\n The Validator may also write additional feedback information into files in the \"feedback directory\"; these files can under certain conditions"
            + "\n be used by PC^2 to display additional information to the Judges and/or Teams."
            
            + "\n\nFor more information, see the PC^2 Contest Administrator's Guide; in particular, the Appendix on Validators."
            +"\nSee also the CLICS specification at https://clics.ecs.baylor.edu/index.php/Problem_format#Validators.";

    private JPanel clicsOptionButtonPanel;
    private JLabel lblValidatorInterface;
    private JRadioButton rdbtnUsePcStandard;
    private JRadioButton rdbtnUseClicsStandard;
    private final ButtonGroup validatorStandardButtonGroup = new ButtonGroup();
    private JLabel lblWhatsThisPC2ValStd;
    private JLabel lblWhatsThisCLICSValStd;
    private JPanel inputValidatorPane;
    private JLabel inputValidatorProgramNameLabel;
    private JTextField inputValidatorProgramNameTextField;
    private JLabel lblInputValidatorInvocation;
    private JTextField inputValidatorCommandTextField;
    private JButton validateInputDataButton;
    private JPanel defineInputValidatorPanel;
    private JPanel executeInputValidatorPanel;
    private JPanel inputValidatorDataFilesPanel;
    private JRadioButton filesPreviouslyLoadedRadioButton;
    private JRadioButton filesJustLoadedRadioButton;
    private JTextField inputValidatorFilesOnDiskTextField;
    private JButton chooseInputFilesButton;
    private JButton chooseInputValidatorProgramButton;
    private JPanel inputValidationResultDetailsPanel;
    private JScrollPane resultsScrollPane;
    private JTable resultsTable;
    private JPanel inputValidationResultSummaryPanel;
    private JLabel inputValidationResultsSummaryLabel;
    private JLabel inputValidationResultSummaryTextLabel;
    private JPanel inputValidationResultPanel;
    private Component verticalStrut_6;
    private Component verticalStrut_7;
    private Component verticalStrut_8;
    private Component verticalStrut_9;
    private Component verticalStrut_10;
    private Component verticalStrut_11;
    private JRadioButton filesOnDiskInFolderRadioButton;
    private Component horizontalStrut_3;
    private Component horizontalStrut_4;
    private final ButtonGroup inputFileLocationButtonGroup = new ButtonGroup();
    
    protected void enableCustomValidatorComponents(boolean enableComponents) {
        getCustomValidatorOptionsSubPanel().setEnabled(enableComponents);
        getChooseCustomOutputValidatorProgramButton().setEnabled(enableComponents);
        getCustomValidatorExecutableProgramLabel().setEnabled(enableComponents);
        getCustomValidatorExecutableProgramTextField().setEnabled(enableComponents);
        getCustomValidatorCommandLabel().setEnabled(enableComponents);
        getCustomValidatorCommandLineTextField().setEnabled(enableComponents);
        getCustomValidatorInterfaceLabel().setEnabled(enableComponents);
        getUsePC2ValStdRadioButton().setEnabled(enableComponents);
        getUseClicsValStdRadioButton().setEnabled(enableComponents);
    }

    protected void enableClicsValidatorComponents(boolean enableComponents) {
        getClicsValidatorOptionsSubPanel().setEnabled(enableComponents);
        getCLICSValidatorCaseSensitiveCheckBox().setEnabled(enableComponents);
        getCLICSSpaceSensitiveCheckBox().setEnabled(enableComponents);
        getFloatAbsoluteToleranceCheckBox().setEnabled(enableComponents);
        getFloatRelativeToleranceCheckBox().setEnabled(enableComponents);
        getFloatAbsoluteToleranceTextField().setEnabled(enableComponents);
        getFloatRelativeToleranceTextField().setEnabled(enableComponents);
    }

    /**
     * This method initializes the Custom Validator jRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseCustomValidatorRadioButton() {
        if (useCustomValidatorRadioButton == null) {
            useCustomValidatorRadioButton = new JRadioButton();
            useCustomValidatorRadioButton.setMargin(new Insets(2, 12, 2, 2));
            useCustomValidatorRadioButton.setText("Use Custom (User-supplied) Validator");
            useCustomValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableOutputValidatorTabComponents();
                    enableUpdateButton();
                }
            });
        }
        return useCustomValidatorRadioButton;
    }

    /**
     * This method initializes showValidatorToJudgesCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowValidatorToJudgesCheckBox() {
        if (showValidatorToJudgesCheckBox == null) {
            showValidatorToJudgesCheckBox = new JCheckBox();
            showValidatorToJudgesCheckBox.setHorizontalAlignment(SwingConstants.LEFT);
            showValidatorToJudgesCheckBox.setMargin(new Insets(2, 12, 2, 2));
            showValidatorToJudgesCheckBox.setText("Show Validator To Judges (SVTJ)");
            showValidatorToJudgesCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return showValidatorToJudgesCheckBox;
    }

    /**
     * This method initializes the sub-panel containing the Default Validator options
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getClicsValidatorOptionsSubPanel() {
        if (clicsValidatorOptionsSubPanel == null) {
            
            clicsValidatorOptionsSubPanel = new JPanel();
            clicsValidatorOptionsSubPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "CLICS Validator options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            
            GridBagLayout gbl_clicsValidatorOptionsSubPanel = new GridBagLayout();
            gbl_clicsValidatorOptionsSubPanel.columnWidths = new int[] {30, 100, 150};
            gbl_clicsValidatorOptionsSubPanel.rowHeights = new int[] {25, 25};
            gbl_clicsValidatorOptionsSubPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
            gbl_clicsValidatorOptionsSubPanel.rowWeights = new double[]{0.0, 0.0};
            clicsValidatorOptionsSubPanel.setLayout(gbl_clicsValidatorOptionsSubPanel);
            
            GridBagConstraints gbc_CaseSensitiveCheckBox = new GridBagConstraints();
            gbc_CaseSensitiveCheckBox.fill = GridBagConstraints.BOTH;
            gbc_CaseSensitiveCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_CaseSensitiveCheckBox.gridx = 0;
            gbc_CaseSensitiveCheckBox.gridy = 0;
            clicsValidatorOptionsSubPanel.add(getCLICSValidatorCaseSensitiveCheckBox(), gbc_CaseSensitiveCheckBox);
            
            GridBagConstraints gbc_FloatRelativeToleranceCheckBox = new GridBagConstraints();
            gbc_FloatRelativeToleranceCheckBox.fill = GridBagConstraints.BOTH;
            gbc_FloatRelativeToleranceCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_FloatRelativeToleranceCheckBox.gridx = 1;
            gbc_FloatRelativeToleranceCheckBox.gridy = 0;
            clicsValidatorOptionsSubPanel.add(getFloatRelativeToleranceCheckBox(), gbc_FloatRelativeToleranceCheckBox);
            
            GridBagConstraints gbc_FloatRelativeToleranceTextField = new GridBagConstraints();
            gbc_FloatRelativeToleranceTextField.insets = new Insets(0, 0, 5, 5);
            gbc_FloatRelativeToleranceTextField.fill = GridBagConstraints.BOTH;
            gbc_FloatRelativeToleranceTextField.gridx = 2;
            gbc_FloatRelativeToleranceTextField.gridy = 0;
            clicsValidatorOptionsSubPanel.add(getFloatRelativeToleranceTextField(), gbc_FloatRelativeToleranceTextField);
            
            GridBagConstraints gbc_SpaceSensitiveCheckBox = new GridBagConstraints();
            gbc_SpaceSensitiveCheckBox.fill = GridBagConstraints.BOTH;
            gbc_SpaceSensitiveCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_SpaceSensitiveCheckBox.gridx = 0;
            gbc_SpaceSensitiveCheckBox.gridy = 1;
            clicsValidatorOptionsSubPanel.add(getCLICSSpaceSensitiveCheckBox(), gbc_SpaceSensitiveCheckBox);
            
            GridBagConstraints gbc_FloatAbsoluteToleranceCheckBox = new GridBagConstraints();
            gbc_FloatAbsoluteToleranceCheckBox.anchor = GridBagConstraints.WEST;
            gbc_FloatAbsoluteToleranceCheckBox.insets = new Insets(0, 0, 5, 5);
            gbc_FloatAbsoluteToleranceCheckBox.gridx = 1;
            gbc_FloatAbsoluteToleranceCheckBox.gridy = 1;
            clicsValidatorOptionsSubPanel.add(getFloatAbsoluteToleranceCheckBox(), gbc_FloatAbsoluteToleranceCheckBox);
            
            GridBagConstraints gbc_FloatAbsoluteToleranceTextField = new GridBagConstraints();
            gbc_FloatAbsoluteToleranceTextField.insets = new Insets(0, 0, 5, 5);
            gbc_FloatAbsoluteToleranceTextField.fill = GridBagConstraints.BOTH;
            gbc_FloatAbsoluteToleranceTextField.gridx = 2;
            gbc_FloatAbsoluteToleranceTextField.gridy = 1;
            clicsValidatorOptionsSubPanel.add(getFloatAbsoluteToleranceTextField(), gbc_FloatAbsoluteToleranceTextField);
        }
        return clicsValidatorOptionsSubPanel;
    }

    /**
     * This method initializes the Custom Validator panel (and its Options sub-panel)
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getCustomValidatorPanel() {
        if (customValidatorPanel == null) {
            customValidatorPanel = new JPanel();
            customValidatorPanel.setMaximumSize(new Dimension(700, 200));
            customValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            customValidatorPanel.setLayout(new BorderLayout(0, 0));
            customValidatorPanel.add(getUseCustomValidatorRadioButton(), BorderLayout.NORTH);
            customValidatorPanel.add(getHorizontalStrut_1(), BorderLayout.WEST);
            customValidatorPanel.add(getCustomValidatorOptionsSubPanel());
        }
        return customValidatorPanel;
    }

    /**
     * This method initializes the isCaseSensitive checkbox for the old (deprecated) PC2 Validator.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCLICSValidatorCaseSensitiveCheckBox() {
        if (isCLICSCaseSensitiveCheckBox == null) {
            isCLICSCaseSensitiveCheckBox = new JCheckBox();
            isCLICSCaseSensitiveCheckBox.setText("Case-sensitive");
            isCLICSCaseSensitiveCheckBox.setSelected(false);
            isCLICSCaseSensitiveCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return isCLICSCaseSensitiveCheckBox;
    }

    /**
     * This method initializes the isSpaceSensitive checkbox.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getCLICSSpaceSensitiveCheckBox() {
        if (isCLICSSpaceSensitiveCheckBox == null) {
            isCLICSSpaceSensitiveCheckBox = new JCheckBox();
            isCLICSSpaceSensitiveCheckBox.setText("Space-sensitive");
            isCLICSSpaceSensitiveCheckBox.setSelected(false);
            isCLICSSpaceSensitiveCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return isCLICSSpaceSensitiveCheckBox;
    }

    /**
     * This method initializes the FloatRelativeTolerance checkbox.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getFloatRelativeToleranceCheckBox() {
        if (floatRelativeToleranceCheckBox == null) {
            floatRelativeToleranceCheckBox = new JCheckBox();
            floatRelativeToleranceCheckBox.setText("Float relative tolerance:");
            floatRelativeToleranceCheckBox.setSelected(false);
            floatRelativeToleranceCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return floatRelativeToleranceCheckBox;
    }

    /**
     * This method initializes the FloatRelativeTolerance Text Field.
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFloatRelativeToleranceTextField() {
        if (floatRelativeToleranceTextField == null) {
            floatRelativeToleranceTextField = new JTextField();
            floatRelativeToleranceTextField.setToolTipText("Enter the relative tolerance for floating point numbers");
            floatRelativeToleranceTextField.setMaximumSize(new Dimension(100, 20));
            floatRelativeToleranceTextField.setColumns(20);
            floatRelativeToleranceTextField.setEnabled(false);
        }
        return floatRelativeToleranceTextField;
    }

    /**
     * This method initializes the FloatAbsoluteTolerance checkbox.
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getFloatAbsoluteToleranceCheckBox() {
        if (floatAbsoluteToleranceCheckBox == null) {
            floatAbsoluteToleranceCheckBox = new JCheckBox();
            floatAbsoluteToleranceCheckBox.setText("Float absolute tolerance:");
            floatAbsoluteToleranceCheckBox.setSelected(false);
            floatAbsoluteToleranceCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return floatAbsoluteToleranceCheckBox;
    }

    /**
     * This method initializes the floatAbsoluteTolerance Text Field.
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getFloatAbsoluteToleranceTextField() {
        if (floatAbsoluteToleranceTextField == null) {
            floatAbsoluteToleranceTextField = new JTextField();
            floatAbsoluteToleranceTextField.setToolTipText("Enter the absolute tolerance for floating point numbers");
            floatAbsoluteToleranceTextField.setMaximumSize(new Dimension(100, 20));
            floatAbsoluteToleranceTextField.setColumns(10);
            floatAbsoluteToleranceTextField.setEnabled(false);
        }
        return floatAbsoluteToleranceTextField;
    }



    /**
     * Show diff between files using gvim.exe.
     * 
     * @param fileOne
     * @param fileTwo
     */
    protected void showFilesDiff(String fileOne, String fileTwo) {

        String command = "gvim.exe -d " + fileOne + " " + fileTwo;
//        System.out.println("cmd = " + command);
        try {
            Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            showMessage("Unable to diff " + e.getMessage());
//            System.out.println("debug diff cmd: " + command);
            e.printStackTrace();
        }
    }

    /**
     * This method initializes validatorProgramJButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getChooseCustomOutputValidatorProgramButton() {
        if (chooseValidatorProgramButton == null) {
            chooseValidatorProgramButton = new JButton();
            chooseValidatorProgramButton.setText("Choose...");
            chooseValidatorProgramButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(getCustomValidatorExecutableProgramTextField(), "Select Validator Program")) {
                        getCustomValidatorExecutableProgramTextField().setToolTipText((getCustomValidatorExecutableProgramTextField().getText()));
                        enableUpdateButton();
                    }
                }
            });
        }
        return chooseValidatorProgramButton;
    }
    
    

    /**
     * This method initializes validatorCommandLineTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getCustomValidatorCommandLineTextField() {
        if (customValidatorCommandLineTextField == null) {
            customValidatorCommandLineTextField = new JTextField();
            customValidatorCommandLineTextField.setEnabled(false);
            customValidatorCommandLineTextField.setMaximumSize(new Dimension(100, 20));
            customValidatorCommandLineTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    updateLocalCustomValidatorCommandLine();
                    enableUpdateButton();
                }
            });
        }
        return customValidatorCommandLineTextField;
    }

    /**
     * This method initializes showCompareCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowCompareCheckBox() {
        if (showCompareCheckBox == null) {
            showCompareCheckBox = new JCheckBox();
            showCompareCheckBox.setBounds(new Rectangle(23, 374, 207, 21));
            showCompareCheckBox.setText("Show Compare");
            showCompareCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return showCompareCheckBox;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDoShowOutputWindowCheckBox() {
        if (doShowOutputWindowCheckBox == null) {
            doShowOutputWindowCheckBox = new JCheckBox();
            doShowOutputWindowCheckBox.setBounds(new Rectangle(23, 338, 225, 24));
            doShowOutputWindowCheckBox.setSelected(true);
            doShowOutputWindowCheckBox.setText("Show the output window");
            doShowOutputWindowCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                    getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());
                }
            });
        }
        return doShowOutputWindowCheckBox;
    }

    /**
     * This method initializes validatorChoiceButtonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getValidatorChoiceButtonGroup() {
        if (validatorChoiceButtonGroup == null) {
            validatorChoiceButtonGroup = new ButtonGroup();
            validatorChoiceButtonGroup.add(getUseNOValidatatorRadioButton());
            validatorChoiceButtonGroup.add(getUsePC2ValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseCLICSValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseCustomValidatorRadioButton());
        }
        return validatorChoiceButtonGroup;
    }

    /**
     * Checks whether a given {@link SerializedFile} needs to be freshened, and if so prompts the user before freshening.
     * 
     * @param serializedFile the file to be checked to see if it is need of freshening
     * @param fileName the file name corresponding to the specified SerializedFile
     * 
     * @return a SerializedFile which is the updated version of the specified SerializedFile if the specified file
     *          was out of date AND the user confirmed the refresh operation
     * @throws InvalidFieldValue if the specified file cannot be read or if the user cancels the operation
     */
    private SerializedFile freshenIfNeeded(SerializedFile serializedFile, String fileName) {

        if (serializedFile == null) {
            return null;

        }
        if (serializedFile.getBuffer() == null) {
            throw new InvalidFieldValue("Unable to read file '" + fileName + "' while updating problem; choose file again");
        }

        // only check freshening if it is still the same fileName
        if (fileName != null && fileName.equals(serializedFile.getAbsolutePath())) {
            // only do this if we are not populating the gui
            if (!populatingGUI && needsFreshening(serializedFile, fileName)) {

                int result = JOptionPane.showConfirmDialog(this, "File (" + fileName + ") has changed; reload from disk?", "Freshen file " + serializedFile.getAbsolutePath() + "?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    serializedFile = new SerializedFile(serializedFile.getAbsolutePath());
                    checkFileFormat(serializedFile);
                    return serializedFile;
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    throw new InvalidFieldValue("Update cancelled");
                }
            } // else nothing to update
        } else {
            if (fileName != null) {
                serializedFile = new SerializedFile(fileName);
            }
        }

        checkFileFormat(serializedFile);
        return serializedFile;
    }

    /**
     * Has this file been updated on disk ?
     * 
     * @param serializedFile
     *            existing saved file
     * @param fileName
     *            name for file that might need freshening.
     * @return true if file on disk different than saved file.
     */
    public boolean needsFreshening(SerializedFile serializedFile, String fileName) {

        if (serializedFile == null) {
            return false;
        }

        try {
            File f = new File(serializedFile.getAbsolutePath());

            if (f.exists()) {
                // Only can check whether to update if file is on disk

                // Now compare them
                // Can't use SerializeFile.getFile() because it may return null... sigh.

                SerializedFile newSerializedFile = new SerializedFile(f.getAbsolutePath());

                return !serializedFile.getSHA1sum().equals(newSerializedFile.getSHA1sum());
            } // else no need to refresh, no file found.

        } catch (Exception ex99) {
            logDebugException("Exception ", ex99);
        }

        return false;
    }

    /**
     * Checks a given {@link SerializedFile} to see if the file type matches the platform on which we are running.
     * If the file type does not match, the user is prompted to confirm whether file should be converted to the current platform
     * format as it is read into PC2.
     * 
     * @param newFile the SerializedFile to be checked
     * @return true if the file was converted, false if the file was null or was not converted
     * @throws InvalidFieldValue if the user was prompted as to whether to do a file conversion and hits "Cancel"
     */
    public boolean checkFileFormat(SerializedFile newFile) {

        if (newFile == null) {
            showMessage("EditProblemPane.checkFileFormat(): Warning: specified file is null");
            return false;
        }

        /*
         * DOS FILE 0x0D 0x0A UNIX FILE 0xA MAC FILE 0xD
         */
        int currentOS = 0;
        // compare OS Versions.

        if (NL.length() == 2) {
            currentOS = Constants.FILETYPE_DOS;
        } else if (NL.charAt(0) == 0x0A) {
            currentOS = Constants.FILETYPE_UNIX;
        } else if (NL.charAt(0) == 0x0D) {
            currentOS = Constants.FILETYPE_MAC;
        }

        if ((currentOS != newFile.getFileType()) && (newFile.getFileType() != Constants.FILETYPE_BINARY) && (newFile.getFileType() != Constants.FILETYPE_ASCII_GENERIC)
                && (newFile.getFileType() != Constants.FILETYPE_ASCII_OTHER)) {

            String fileName = newFile.getName();
            String question = "The file (" + fileName + ") you are loading appears to be of type '";

            if (newFile.getFileType() == Constants.FILETYPE_BINARY) {
                question = question + Constants.FILETYPE_BINARY_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_DOS) {
                question = question + Constants.FILETYPE_DOS_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_MAC) {
                question = question + Constants.FILETYPE_MAC_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_UNIX) {
                question = question + Constants.FILETYPE_UNIX_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_ASCII_GENERIC) {
                question = question + Constants.FILETYPE_ASCII_GENERIC_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_ASCII_OTHER) {
                question = question + Constants.FILETYPE_ASCII_OTHER_TEXT;
            }

            question = question + "'." + NL + NL;
            question = question + "The Current OS is '" + System.getProperty("os.name", "?");
            question = question + "'." + NL + NL;
            question = question + "Do you want the file converted to the current OS file format as it is loaded into PC^2?";

            if (!newFile.isExternalFile()) {
                int answer = JOptionPane.showConfirmDialog(this, question, "File Format Mismatch", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    
                    //TODO: shouldn't the return value here be the value returned by "convertFile()", rather than "always true"?
                    newFile.convertFile(currentOS);
                    return true;
                } else if (answer == JOptionPane.CANCEL_OPTION) {
                    throw new InvalidFieldValue("Update canceled");
                }
            }

        }
        return false;
    }

    /**
     * This resets the form, eg for a new problem.
     */
    private void clearForm() {
        
        //we're defining (adding) a new problem; show Add and hide Update buttons
        getAddButton().setVisible(true);
        getAddButton().setEnabled(true);
        getUpdateButton().setVisible(false);
        getUpdateButton().setEnabled(false);

        //initialize the General Tab fields:
        
        //Problem description components:
        problemNameTextField.setText("");
        timeOutSecondTextField.setText(Integer.toString(Problem.DEFAULT_TIMEOUT_SECONDS));
        shortNameTextfield.setText("");
        
        //input data options:
        getProblemRequiresDataCheckBox().setSelected(false);
        stdinRadioButton.setSelected(true);
        fileRadioButton.setSelected(false);
        inputDataFileLabel.setText("");
        inputDataFileLabel.setToolTipText("");

        //answer files options:
        getJudgesHaveAnswerFilesCheckbox().setSelected(false);
        answerFileNameLabel.setText("");
        answerFileNameLabel.setToolTipText("");

        //misc options:
        getDoShowOutputWindowCheckBox().setSelected(true);
        getShowCompareCheckBox().setSelected(true);
        getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());

        getDeleteProblemCheckBox().setSelected(false);

        //Judging Type tab:
        initializeJudgingTabFields(null);
        
        //Output Validator tab:
        initializeOutputValidatorTabFields();
        
        //Input Validator tab:
        initializeInputValidatorTabFields();
        
        //Data Files tab:
        // ???

    }
    
    private void initializeInputValidatorTabFields() {
        getInputValidatorProgramNameTextField().setText("");
        getInputValidatorProgramNameTextField().setToolTipText("");
        getInputValidatorCommandTextField().setText("");
        getInputValidatorCommandTextField().setToolTipText("");
        getInputValidatorFilesOnDiskTextField().setText("");
        getInputValidatorFilesOnDiskTextField().setToolTipText("");
        getFilesOnDiskInFolderRadioButton().setSelected(true);  //button group will init others "not selected"
        getInputValidationResultSummaryTextLabel().setText("<No Input Validation test run yet>");
        getInputValidationResultSummaryTextLabel().setForeground(Color.BLACK);
//        getInputValidatorResultsTable().setModel(new InputValidationResultsTableModel()); //for some reason this removes the custom pass/fail cell renderer...
        ((InputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).setResults(null);
        ((InputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).fireTableDataChanged();

    }

    private void initializeOutputValidatorTabFields() {
        //default to "no validator"
        getUseNOValidatatorRadioButton().setSelected(true);
        
        //clear PC2 Validator options
        getUsePC2ValidatorRadioButton().setSelected(false);
        getPc2ValidatorOptionComboBox().setSelectedIndex(0);
        getPc2ValidatorIgnoreCaseCheckBox().setSelected(false);
        
        //clear CLICS default validator options
        getUseCLICSValidatorRadioButton().setSelected(false);
        getCLICSValidatorCaseSensitiveCheckBox().setSelected(false);
        getCLICSSpaceSensitiveCheckBox().setSelected(false);
        getFloatAbsoluteToleranceCheckBox().setSelected(false);
        getFloatRelativeToleranceCheckBox().setSelected(false);
        getFloatAbsoluteToleranceTextField().setText("");
        getFloatRelativeToleranceTextField().setText("");

        //clear custom validator options
        getUseCustomValidatorRadioButton().setSelected(false);
        getCustomValidatorExecutableProgramTextField().setText("");
        getCustomValidatorExecutableProgramTextField().setToolTipText("");
        
        localPC2InterfaceCustomValidatorCommandLine = Constants.DEFAULT_PC2_VALIDATOR_COMMAND;
        localClicsInterfaceCustomValidatorCommandLine = Constants.DEFAULT_CLICS_VALIDATOR_COMMAND;
        
        getCustomValidatorCommandLineTextField().setText(localClicsInterfaceCustomValidatorCommandLine);
        getUseClicsValStdRadioButton().setSelected(true); //button group also causes Use PC2 Val Std button to become unselected 
        
        getShowValidatorToJudgesCheckBox().setSelected(true);
        
    }
    

    /**
     * This method initializes the useComputerJudging Radio Button
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseComputerJudgingRadioButton() {
        if (computerJudgingRadioButton == null) {
            computerJudgingRadioButton = new JRadioButton();
            computerJudgingRadioButton.setText("Computer Judging");
            computerJudgingRadioButton.setBounds(new Rectangle(32, 14, 173, 21));
            computerJudgingRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getManualReviewCheckBox().setEnabled(true);
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    enableUpdateButton();
                }
            });

        }
        return computerJudgingRadioButton;
    }

    /**
     * This method initializes the manualJudging Radio Button
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getManualJudgingRadioButton() {
        if (manualJudgingRadioButton == null) {
            manualJudgingRadioButton = new JRadioButton();
            manualJudgingRadioButton.setText("Manual Judging");
            manualJudgingRadioButton.setBounds(new Rectangle(32, 132, 257, 21));
            manualJudgingRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getManualReviewCheckBox().setEnabled(false);
                    getPrelimaryNotificationCheckBox().setEnabled(false);
                    enableUpdateButton();
                }
            });
        }

        return manualJudgingRadioButton;
    }

    /**
     * This method initializes the manualReview CheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getManualReviewCheckBox() {
        if (manualReviewCheckBox == null) {
            manualReviewCheckBox = new JCheckBox();
            manualReviewCheckBox.setText("Manual Review");
            manualReviewCheckBox.setBounds(new Rectangle(57, 47, 186, 21));
            manualReviewCheckBox.setEnabled(false);
            manualReviewCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    getPrelimaryNotificationCheckBox().setEnabled(getManualReviewCheckBox().isSelected());
                    enableUpdateButton();
                }
            });

        }
        return manualReviewCheckBox;
    }

    /**
     * This method initializes prelimaryNotification
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPrelimaryNotificationCheckBox() {
        if (prelimaryNotificationCheckBox == null) {
            prelimaryNotificationCheckBox = new JCheckBox();
            prelimaryNotificationCheckBox.setText("Send Preliminary Notification to the team");
            prelimaryNotificationCheckBox.setBounds(new Rectangle(100, 80, 328, 21));
            prelimaryNotificationCheckBox.setEnabled(false);
            prelimaryNotificationCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return prelimaryNotificationCheckBox;
    }

    /**
     * This method initializes deleteProblemCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDeleteProblemCheckBox() {
        if (deleteProblemCheckBox == null) {
            deleteProblemCheckBox = new JCheckBox();
            deleteProblemCheckBox.setBounds(new Rectangle(285, 340, 182, 21));
            deleteProblemCheckBox.setText("Hide Problem");
            deleteProblemCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteProblemCheckBox;
    }

    /**
     * This method initializes loadButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getLoadButton() {
        if (loadButton == null) {
            loadButton = new JButton();
            loadButton.setText("Load");
            loadButton.setToolTipText("Load problem def from problem.yaml");
            loadButton.setMnemonic(KeyEvent.VK_L);
            loadButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    loadProblemInfoFile();
                }
            });
        }
        return loadButton;
    }

    /**
     * Load problem info.
     * 
     * If selects problem.yaml then load yaml and files If selects directory will scan for .in and .ans files and load them
     * 
     */
    protected void loadProblemInfoFile() {

        showMessage("Load not implemented, yet.");

        // huh
    }

    public File selectYAMLFileDialog(Component parent, String title, String startDirectory) {

        JFileChooser chooser = new JFileChooser(startDirectory);
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileFilter filterYAML = new FileNameExtensionFilter("YAML document (*.yaml)", "yaml");
        chooser.addChoosableFileFilter(filterYAML);

        chooser.setAcceptAllFileFilterUsed(false);
        // bug 759 java7 requires us to select it, otherwise the default choice would be empty
        chooser.setFileFilter(filterYAML);

        int action = chooser.showOpenDialog(parent);

        switch (action) {
            case JFileChooser.APPROVE_OPTION:
                File file = chooser.getSelectedFile();
                lastYamlLoadDirectory = chooser.getCurrentDirectory().toString();
                return file;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
            default:
                break;
        }
        return null;
    }

    // private String selectFileName(String title, String dirname) throws IOException {
    //
    // String chosenFile = null;
    // File file = selectYAMLFileDialog(this, title, lastDirectory);
    // if (file != null) {
    // chosenFile = file.getCanonicalFile().toString();
    // return chosenFile;
    // } else {
    // return null;
    // }
    // }

    public ContestSnakeYAMLLoader getLoader() {
        if (loader == null) {
            loader = new ContestSnakeYAMLLoader();
        }
        return loader;
    }

    /**
     * This method initializes exportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getExportButton() {
        if (exportButton == null) {
            exportButton = new JButton();
            exportButton.setText("Export");
            exportButton.setToolTipText("Export problem and files");
            exportButton.setMnemonic(KeyEvent.VK_X);
            exportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (Utilities.isDebugMode()) {
                        saveAndCompare();
                    } else {
                        showMessage("Export not implmented yet"); // SOMEDAY export problem data
                    }
                }
            });
        }
        return exportButton;
    }

    public static String getReportFilename(String prefix, IReport selectedReport) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd.SSS");
        // "yyMMdd HHmmss.SSS");
        String reportName = selectedReport.getReportTitle();

        while (reportName.indexOf(' ') > -1) {
            reportName = reportName.replace(" ", "_");
        }
        return prefix + "report." + reportName + "." + simpleDateFormat.format(new Date()) + ".txt";

    }

    void saveAndCompare() {

        try {
            if (debug22EditProblem) {
                System.out.println("debug 22   ORIGINAL  load dump");
                Utilities.dump(originalProblemDataFiles, "debug 22 in load orig");

                String[] s2 = getTestDataList(originalProblemDataFiles);
                System.out.println("debug 22 Number of   ORIGINAL  problem data files is " + s2.length);

                String[] s = getTestDataList(newProblemDataFiles);
                System.out.println("debug 22 B4 Number of new problem data files is " + s.length);
            }
            
            newProblemDataFiles = getProblemDataFilesFromFields();

            if (debug22EditProblem) {
                String[] s = getTestDataList(newProblemDataFiles);
                System.out.println("debug 22 B5 Number of new problem data files is " + s.length);
            }

            Problem newProblem = getProblemFromFields(problem, newProblemDataFiles);

            if (debug22EditProblem) {
                String[] s = getTestDataList(newProblemDataFiles);
                System.out.println("debug 22 B6 Number of new problem data files is " + s.length);

                Utilities.dump(newProblemDataFiles, "debug 22 in load new");
                System.out.flush();
            }

            String fileNameTwo = createProblemReport(newProblem, newProblemDataFiles, "stuf2");
            if (debug22EditProblem) {
                System.out.println("Created problem report " + fileNameOne);
            }

            showFilesDiff(fileNameOne, fileNameTwo);
        } catch (Exception e) {
            e.printStackTrace(); // debug 22
        }

    }

    private String createProblemReport(Problem prob, ProblemDataFiles datafiles, String fileNamePrefix) {

        ProblemsReport report = new ProblemsReport();
        report.setContestAndController(getContest(), getController());
        String filename = getReportFilename(fileNamePrefix + ".prob.txt", report);

        try {
            PrintWriter printWriter = null;
            printWriter = new PrintWriter(new FileOutputStream(filename, false), true);
            if (problem == null) {
                printWriter.println("  Problem is null");
            } else {
                report.writeRow(printWriter, prob, datafiles);
            }
            printWriter.close();
            printWriter = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        report = null;

        return filename;
    }

    /**
     * get Letter.
     * 
     * 
     * @param directory
     * @return
     */
    char currentDirectoryLetter(String directory) {
        char letter = 'A';
        String nextDirectory = directory + File.separator + letter;
        File file = new File(nextDirectory);

        while (file.isDirectory()) {
            
            if (debug22EditProblem) {
                System.out.println("Found directory: " + nextDirectory);
            }

            letter++;
            nextDirectory = directory + File.separator + letter;
            file = new File(nextDirectory);
            if (!file.isDirectory()) {
                letter--;
            }
        }

        return letter;

    }

    /**
     * This method initializes reportButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getReportButton() {
        if (reportButton == null) {
            reportButton = new JButton();
            reportButton.setText("Report");
            reportButton.setMnemonic(KeyEvent.VK_R);
            reportButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    viewProblemReport();
                }
            });
        }
        return reportButton;
    }

    protected void viewProblemReport() {
        SingleProblemReport singleProblemReport = new SingleProblemReport();

        try {
            newProblemDataFiles = getProblemDataFilesFromFields();
            Problem newProblem = getProblemFromFields(problem, newProblemDataFiles);
            singleProblemReport.setProblem(newProblem, newProblemDataFiles);
            Utilities.viewReport(singleProblemReport, "Problem Report " + getProblemNameTextField().getText(), getContest(), getController());
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

    }

    /**
     * This method initializes judgeTypeInnerPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJudgeTypeInnerPane() {
        if (judgeTypeInnerPane == null) {
            judgeTypeInnerPane = new JPanel();
            judgeTypeInnerPane.setLayout(null);
            judgeTypeInnerPane.setPreferredSize(new Dimension(190, 190));
            judgeTypeInnerPane.add(getUseComputerJudgingRadioButton(), null);
            judgeTypeInnerPane.add(getManualReviewCheckBox(), null);
            judgeTypeInnerPane.add(getPrelimaryNotificationCheckBox(), null);
            judgeTypeInnerPane.add(getManualJudgingRadioButton(), null);
        }
        return judgeTypeInnerPane;
    }

    /**
     * Compare the entries in two directories.
     * @param directory the first directory to compare
     * @param nextDirectory the second directory to compare with
     * @return a String containing a message indicating whether the directory contents match
     */
    public String compareDirectories(String directory, String nextDirectory) {

        ArrayList<String> filelist = getFileEntries(directory, "", 0);
        ArrayList<String> filelistTwo = getFileEntries(nextDirectory, "", 0);

        int matching = 0;

        if (filelist.size() == filelistTwo.size()) {
            for (int i = 0; i < filelist.size(); i++) {
                String name1 = filelist.get(i);
                String name2 = filelistTwo.get(i);
                if (name1.equals(name2)) {
                    matching++;
                } else {
                    System.err.println("Mismatch " + name1 + " vs " + name2);
                }
            }
        }

        if (matching == filelist.size()) {
            return "All " + matching + " matching";
        } else {
            return filelist.size() + " vs " + filelistTwo.size();
        }
    }

    /**
     * Returns all filenames (relative path) under input directory.
     * 
     * The list does not contain the string directory. Only directories under the directory will be included.
     * 
     * @param directory
     * @param relativeDirectory
     * @param level
     * @return all files names with relative paths.
     */
    private ArrayList<String> getFileEntries(String directory, String relativeDirectory, int level) {

        ArrayList<String> list = new ArrayList<>();

        File[] files = new File(directory).listFiles();

        if (relativeDirectory.length() > 0) {
            relativeDirectory += File.separator;
        }

        for (File entry : files) {
            if (entry.isFile()) {
                list.add(relativeDirectory + entry.getName());
            }
        }

        // recurse

        for (File entry : files) {
            if (entry.isDirectory() && !(entry.getName().equals(".") || entry.getName().equals(".."))) {
                list.addAll(getFileEntries(directory + File.separator + entry.getName(), //
                        relativeDirectory + entry.getName(), level + 1));
            }
        }

        return list;
    }

    public JTextField getShortNameTextfield() {
        return shortNameTextfield;
    }
    private JPanel getNoValidatorPanel() {
        if (noValidatorPanel == null) {
        	noValidatorPanel = new JPanel();
        	noValidatorPanel.setMaximumSize(new Dimension(500, 200));
        	noValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	noValidatorPanel.setBorder(null);
        	FlowLayout flowLayout = (FlowLayout) noValidatorPanel.getLayout();
        	flowLayout.setHgap(10);
        	flowLayout.setAlignment(FlowLayout.LEFT);
        	noValidatorPanel.add(getUseNOValidatatorRadioButton());
        }
        return noValidatorPanel;
    }
    private JPanel getClicsValidatorPanel() {
        if (clicsValidatorPanel == null) {
        	clicsValidatorPanel = new JPanel();
        	clicsValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            clicsValidatorPanel.setLayout(new BorderLayout(0, 0));
        	clicsValidatorPanel.setMaximumSize(new Dimension(500, 200));
            clicsValidatorPanel.add(getClicsOptionButtonPanel(), BorderLayout.NORTH);
        	clicsValidatorPanel.add(getHorizontalStrut(), BorderLayout.WEST);
        	clicsValidatorPanel.add(getClicsValidatorOptionsSubPanel());
        }
        return clicsValidatorPanel;
    }
    
    private Component getHorizontalStrut() {
        if (horizontalStrut == null) {
        	horizontalStrut = Box.createHorizontalStrut(20);
        	horizontalStrut.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut;
    }
    
    private JPanel getClicsOptionButtonPanel() {
        if (clicsOptionButtonPanel == null) {
            clicsOptionButtonPanel = new JPanel();
            clicsOptionButtonPanel.setBorder(null);
            clicsOptionButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            FlowLayout fl_clicsOptionButtonPanel = new FlowLayout(FlowLayout.LEFT);
            fl_clicsOptionButtonPanel.setHgap(0);
            clicsOptionButtonPanel.setLayout(fl_clicsOptionButtonPanel);
            clicsOptionButtonPanel.add(getUseCLICSValidatorRadioButton());
            clicsOptionButtonPanel.add(getLblWhatsThisCLICSValidator());
        }
        return clicsOptionButtonPanel;
    }
    
    private JPanel getCustomValidatorOptionsSubPanel() {
        if (customValidatorOptionsSubPanel == null) {
            
            //define the Custom Validator subpanel
        	customValidatorOptionsSubPanel = new JPanel();
        	customValidatorOptionsSubPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	customValidatorOptionsSubPanel.setPreferredSize(new Dimension(500, 300));
        	customValidatorOptionsSubPanel.setBorder(new TitledBorder(null, "Validator options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        	
        	//define the (GridBag) layout for the Custom Validator subpanel
        	GridBagLayout gbl_customValidatorOptionsPanel = new GridBagLayout();
        	gbl_customValidatorOptionsPanel.columnWidths = new int[] {140, 150, 50};
        	gbl_customValidatorOptionsPanel.rowHeights = new int[] {30, 30, 0, 0};
        	gbl_customValidatorOptionsPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
        	gbl_customValidatorOptionsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
        	customValidatorOptionsSubPanel.setLayout(gbl_customValidatorOptionsPanel);
        	
        	//add the Custom Validator Executable Program Label to the subpanel
        	GridBagConstraints gbc_customValidatorProgramNameLabel = new GridBagConstraints();
        	gbc_customValidatorProgramNameLabel.anchor = GridBagConstraints.EAST;
        	gbc_customValidatorProgramNameLabel.insets = new Insets(0, 0, 5, 5);
        	gbc_customValidatorProgramNameLabel.gridx = 0;
        	gbc_customValidatorProgramNameLabel.gridy = 0;
        	customValidatorOptionsSubPanel.add(getCustomValidatorExecutableProgramLabel(), gbc_customValidatorProgramNameLabel);
        	
        	//add the Custom Validator Executable Program TextField to the subpanel
        	GridBagConstraints gbc_customValidatorProgramNameTextField = new GridBagConstraints();
        	gbc_customValidatorProgramNameTextField.insets = new Insets(0, 0, 5, 5);
        	gbc_customValidatorProgramNameTextField.fill = GridBagConstraints.HORIZONTAL;
        	gbc_customValidatorProgramNameTextField.gridx = 1;
        	gbc_customValidatorProgramNameTextField.gridy = 0;
        	customValidatorOptionsSubPanel.add(getCustomValidatorExecutableProgramTextField(), gbc_customValidatorProgramNameTextField);
        	
        	//add the Choose Validator Program button to the subpanel
        	GridBagConstraints gbc_validatorProgramButton = new GridBagConstraints();
        	gbc_validatorProgramButton.anchor = GridBagConstraints.NORTHWEST;
        	gbc_validatorProgramButton.insets = new Insets(0, 0, 5, 0);
        	gbc_validatorProgramButton.gridx = 2;
        	gbc_validatorProgramButton.gridy = 0;
        	customValidatorOptionsSubPanel.add(getChooseCustomOutputValidatorProgramButton(), gbc_validatorProgramButton);
        	
        	//add the Validator Command Line label to the subpanel
        	GridBagConstraints gbc_customValidatorCommandLineLabel = new GridBagConstraints();
        	gbc_customValidatorCommandLineLabel.anchor = GridBagConstraints.EAST;
        	gbc_customValidatorCommandLineLabel.insets = new Insets(0, 0, 5, 5);
        	gbc_customValidatorCommandLineLabel.gridx = 0;
        	gbc_customValidatorCommandLineLabel.gridy = 1;
        	customValidatorOptionsSubPanel.add(getCustomValidatorCommandLabel(), gbc_customValidatorCommandLineLabel);
        	
        	//add the Custom Validator Command Textfield to the subpanel
        	GridBagConstraints gbc_customValidatorCommandLineTextField = new GridBagConstraints();
        	gbc_customValidatorCommandLineTextField.insets = new Insets(0, 0, 5, 5);
        	gbc_customValidatorCommandLineTextField.fill = GridBagConstraints.HORIZONTAL;
        	gbc_customValidatorCommandLineTextField.gridx = 1;
        	gbc_customValidatorCommandLineTextField.gridy = 1;
        	customValidatorOptionsSubPanel.add(getCustomValidatorCommandLineTextField(), gbc_customValidatorCommandLineTextField);
        	
        	//add the Custom Validator Interface label to the subpanel
        	GridBagConstraints gbc_CustomValidatorInterfaceLabel = new GridBagConstraints();
        	gbc_CustomValidatorInterfaceLabel.anchor = GridBagConstraints.EAST;
        	gbc_CustomValidatorInterfaceLabel.insets = new Insets(0, 0, 5, 5);
        	gbc_CustomValidatorInterfaceLabel.gridx = 0;
        	gbc_CustomValidatorInterfaceLabel.gridy = 2;
        	customValidatorOptionsSubPanel.add(getCustomValidatorInterfaceLabel(), gbc_CustomValidatorInterfaceLabel);
        	
        	//add the "Use PC2 Validator Interface" radio button to the subpanel
        	GridBagConstraints gbc_rdbtnUsePc2Standard = new GridBagConstraints();
        	gbc_rdbtnUsePc2Standard.insets = new Insets(0, 0, 5, 5);
        	gbc_rdbtnUsePc2Standard.gridx = 1;
        	gbc_rdbtnUsePc2Standard.gridy = 2;
        	customValidatorOptionsSubPanel.add(getUsePC2ValStdRadioButton(), gbc_rdbtnUsePc2Standard);
        	
        	//add the "What's This" icon for the PC2 Validator Standard Interface radio button to the subpanel
        	GridBagConstraints gbc_label_WhatsThisPC2ValStd = new GridBagConstraints();
        	gbc_label_WhatsThisPC2ValStd.anchor = GridBagConstraints.WEST;
            gbc_label_WhatsThisPC2ValStd.insets = new Insets(0, 0, 5, 0);
        	gbc_label_WhatsThisPC2ValStd.gridx = 2;
        	gbc_label_WhatsThisPC2ValStd.gridy = 2;
        	customValidatorOptionsSubPanel.add(getLabelWhatsThisPC2ValStd(), gbc_label_WhatsThisPC2ValStd);
        	
        	//add the "Use CLICS Validator Interface" radio button to the subpanel
        	GridBagConstraints gbc_rdbtnUseClicsStandard = new GridBagConstraints();
        	gbc_rdbtnUseClicsStandard.insets = new Insets(0, 0, 0, 5);
        	gbc_rdbtnUseClicsStandard.gridx = 1;
        	gbc_rdbtnUseClicsStandard.gridy = 3;
        	customValidatorOptionsSubPanel.add(getUseClicsValStdRadioButton(), gbc_rdbtnUseClicsStandard);
        	
            //add the "What's This" icon for the CLICS Validator Standard Interface radio button to the subpanel
        	GridBagConstraints gbc_label_WhatsThisCLICSValStd = new GridBagConstraints();
        	gbc_label_WhatsThisCLICSValStd.anchor = GridBagConstraints.WEST;
        	gbc_label_WhatsThisCLICSValStd.gridx = 2;
        	gbc_label_WhatsThisCLICSValStd.gridy = 3;
        	customValidatorOptionsSubPanel.add(getLabelWhatsThisCLICSValStd(), gbc_label_WhatsThisCLICSValStd);
        }
        return customValidatorOptionsSubPanel;
    }
    
    private Component getHorizontalStrut_1() {
        if (horizontalStrut_1 == null) {
        	horizontalStrut_1 = Box.createHorizontalStrut(20);
        	horizontalStrut_1.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut_1;
    }
    
    private JLabel getCustomValidatorExecutableProgramLabel() {
        if (customValidatorProgramNameLabel == null) {
        	customValidatorProgramNameLabel = new JLabel("Validator program:");
        }
        return customValidatorProgramNameLabel;
    }
    
    private JLabel getCustomValidatorCommandLabel() {
        if (customValidatorCommandLineLabel == null) {
            customValidatorCommandLineLabel = new JLabel("Validator Command Line:");
        }
        return customValidatorCommandLineLabel;
    }    
    
    private JTextField getCustomValidatorExecutableProgramTextField() {
        if (customValidatorProgramNameTextField == null) {
        	customValidatorProgramNameTextField = new JTextField();
        	customValidatorProgramNameTextField.setEnabled(false);
        	customValidatorProgramNameTextField.setColumns(25);
        	customValidatorProgramNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return customValidatorProgramNameTextField;
    }
    private Component getVerticalStrut() {
        if (verticalStrut == null) {
        	verticalStrut = Box.createVerticalStrut(20);
        	verticalStrut.setPreferredSize(new Dimension(0, 40));
        }
        return verticalStrut;
    }
    private Component getVerticalStrut_1() {
        if (verticalStrut_1 == null) {
        	verticalStrut_1 = Box.createVerticalStrut(20);
        }
        return verticalStrut_1;
    }
    private Component getVerticalStrut_2() {
        if (verticalStrut_2 == null) {
        	verticalStrut_2 = Box.createVerticalStrut(20);
        }
        return verticalStrut_2;
    }
    private Component getVerticalStrut_3() {
        if (verticalStrut_3 == null) {
        	verticalStrut_3 = Box.createVerticalStrut(20);
        }
        return verticalStrut_3;
    }
    private Component getVerticalStrut_4() {
        if (verticalStrut_4 == null) {
        	verticalStrut_4 = Box.createVerticalStrut(20);
        }
        return verticalStrut_4;
    }
    private Component getVerticalStrut_5() {
        if (verticalStrut_5 == null) {
        	verticalStrut_5 = Box.createVerticalStrut(20);
        }
        return verticalStrut_5;
    }
    private JPanel getPc2ValidatorPanel() {
        if (pc2ValidatorPanel == null) {
        	pc2ValidatorPanel = new JPanel();
        	pc2ValidatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	pc2ValidatorPanel.setLayout(new BorderLayout(0, 0));
        	pc2ValidatorPanel.setMaximumSize(new Dimension(500, 200));
        	pc2ValidatorPanel.add(getUsePC2ValidatorRadioButton(), BorderLayout.NORTH);
        	pc2ValidatorPanel.add(getHorizontalStrut_2(), BorderLayout.WEST);
        	pc2ValidatorPanel.add(getPc2ValidatorOptionsSubPanel());
        }
        return pc2ValidatorPanel;
    }
    private JRadioButton getUsePC2ValidatorRadioButton() {
        if (usePC2ValidatorRadioButton == null) {
        	usePC2ValidatorRadioButton = new JRadioButton("Use PC^2 Validator");
        	usePC2ValidatorRadioButton.setPreferredSize(new Dimension(21, 23));
        	usePC2ValidatorRadioButton.setMinimumSize(new Dimension(21, 23));
        	usePC2ValidatorRadioButton.setMaximumSize(new Dimension(21, 23));
        	usePC2ValidatorRadioButton.setMargin(new Insets(2, 12, 2, 2));
        	usePC2ValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableOutputValidatorTabComponents();
                    enableUpdateButton();
                }
            });
        }
        return usePC2ValidatorRadioButton;
    }
    private Component getHorizontalStrut_2() {
        if (horizontalStrut_2 == null) {
        	horizontalStrut_2 = Box.createHorizontalStrut(20);
        	horizontalStrut_2.setPreferredSize(new Dimension(35, 0));
        }
        return horizontalStrut_2;
    }
    private JPanel getPc2ValidatorOptionsSubPanel() {
        if (pc2ValidatorOptionsSubPanel == null) {
        	pc2ValidatorOptionsSubPanel = new JPanel();
        	pc2ValidatorOptionsSubPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "PC^2 Validator options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        	
        	GridBagLayout gbl_pc2ValidatorOptionsSubPanel = new GridBagLayout();
        	gbl_pc2ValidatorOptionsSubPanel.columnWidths = new int[] {100, 100};
        	gbl_pc2ValidatorOptionsSubPanel.rowHeights = new int[]{20,20};
        	gbl_pc2ValidatorOptionsSubPanel.columnWeights = new double[]{0.0, 0.0};
        	gbl_pc2ValidatorOptionsSubPanel.rowWeights = new double[]{0.0,0.0};
        	pc2ValidatorOptionsSubPanel.setLayout(gbl_pc2ValidatorOptionsSubPanel);
        	
        	GridBagConstraints gbc_pc2ValidatorOptionComboBoxLabel = new GridBagConstraints();
        	gbc_pc2ValidatorOptionComboBoxLabel.insets = new Insets(0, 20, 5, 5);
        	gbc_pc2ValidatorOptionComboBoxLabel.gridx = 0;
        	gbc_pc2ValidatorOptionComboBoxLabel.gridy = 0;
        	pc2ValidatorOptionsSubPanel.add(getPc2ValidatorOptionComboBoxLabel(), gbc_pc2ValidatorOptionComboBoxLabel);
        	
        	GridBagConstraints gbc_pc2ValidatorOptionComboBox = new GridBagConstraints();
        	gbc_pc2ValidatorOptionComboBox.anchor = GridBagConstraints.WEST;
        	gbc_pc2ValidatorOptionComboBox.fill = GridBagConstraints.VERTICAL;
        	gbc_pc2ValidatorOptionComboBox.weightx = 1.0;
        	gbc_pc2ValidatorOptionComboBox.insets = new Insets(0, 0, 5, 0);
        	gbc_pc2ValidatorOptionComboBox.gridx = 1;
        	gbc_pc2ValidatorOptionComboBox.gridy = 0;
        	pc2ValidatorOptionsSubPanel.add(getPc2ValidatorOptionComboBox(), gbc_pc2ValidatorOptionComboBox);
        	
        	GridBagConstraints gbc_pc2ValidatorIgnoreCaseCheckBox = new GridBagConstraints();
        	gbc_pc2ValidatorIgnoreCaseCheckBox.anchor = GridBagConstraints.WEST;
        	gbc_pc2ValidatorIgnoreCaseCheckBox.insets = new Insets(0, 30, 0, 5);
        	gbc_pc2ValidatorIgnoreCaseCheckBox.gridx = 0;
        	gbc_pc2ValidatorIgnoreCaseCheckBox.gridy = 1;
        	pc2ValidatorOptionsSubPanel.add(getPc2ValidatorIgnoreCaseCheckBox(), gbc_pc2ValidatorIgnoreCaseCheckBox);
        	


        	
        	
        }
        return pc2ValidatorOptionsSubPanel;
    }

    private JLabel getPc2ValidatorOptionComboBoxLabel() {
        if (pc2ValidatorOptionComboBoxLabel == null) {
        	pc2ValidatorOptionComboBoxLabel = new JLabel("Validator mode:");
        }
        return pc2ValidatorOptionComboBoxLabel;
    }
    
    private JComboBox<String> getPc2ValidatorOptionComboBox() {
        if (pc2ValidatorOptionComboBox == null) {
            pc2ValidatorOptionComboBox = new JComboBox<String>();
//            pc2ValidatorOptionComboBox.setBounds(new java.awt.Rectangle(158, 24, 255, 26));

            pc2ValidatorOptionComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    enableUpdateButton();

                }
            });
            pc2ValidatorOptionComboBox.addItem("None Selected");
            pc2ValidatorOptionComboBox.addItem("1 - diff");
            pc2ValidatorOptionComboBox.addItem("2 - ignore whitespace at start of file");
            pc2ValidatorOptionComboBox.addItem("3 - ignore leading whitespace on lines");
            pc2ValidatorOptionComboBox.addItem("4 - ignore all whitespace on lines");
            pc2ValidatorOptionComboBox.addItem("5 - ignore empty lines");

        }
        return pc2ValidatorOptionComboBox;
    }
    
    private JCheckBox getPc2ValidatorIgnoreCaseCheckBox() {
        if (pc2ValidatorIgnoreCaseCheckBox == null) {
        	pc2ValidatorIgnoreCaseCheckBox = new JCheckBox("Ignore Case In Output");
        	pc2ValidatorIgnoreCaseCheckBox.setMinimumSize(new Dimension(80, 23));
        	pc2ValidatorIgnoreCaseCheckBox.setMaximumSize(new Dimension(80, 23));
        	pc2ValidatorIgnoreCaseCheckBox.setPreferredSize(new Dimension(150, 23));
        	pc2ValidatorIgnoreCaseCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return pc2ValidatorIgnoreCaseCheckBox;
    }
    
    /**
     * Returns a {@link CustomValidatorSettings} object containing the Custom Validator settings currently displayed in the GUI.
     * Displays an error message and throws {@link InvalidFieldValue} if the Validator Interface buttons displayed in the GUI
     * are inconsistent.
     * 
     * @return a CustomValidatorSettings object populated from the GUI
     * 
     * @throws {@link InvalidFieldValue} if it's not the case that exactly one of the GUI radio buttons identifying 
     *                              the Validator Interface mode is selected
     */
    private CustomValidatorSettings getCustomValidatorSettingsFromFields() {
        
        //make sure exactly one of the Interface mode buttons is selected
        if (!(this.getUsePC2ValStdRadioButton().isSelected() ^ this.getUseClicsValStdRadioButton().isSelected())) {
            showMessage("Invalid settings for Validator Interface radio buttons");
            throw new InvalidFieldValue("Invalid settings for Validator Interface radio buttons.");
        }
        
        CustomValidatorSettings settings = new CustomValidatorSettings();
        
        //put the Validator Program Name into the new settings
        settings.setValidatorProgramName(this.getCustomValidatorExecutableProgramTextField().getText());
        
        //put the settings object into "PC2 Interface" mode and copy the current PC2 Validator Command line to it
        //Note that this gets the "local copy" (which is updated on every keystroke) rather than what is currently
        // in the GUI textbox, because the user could have switched to the CLICS Validator Interface mode after
        // entering data for the PC2 Command Line while in PC2 Validator Interface mode
        settings.setUsePC2ValidatorInterface();
        settings.setValidatorCommandLine(this.localPC2InterfaceCustomValidatorCommandLine);
        
        //put the settings object into "CLICS Interface" mode and copy the current CLICS Validator Command line to it
        settings.setUseClicsValidatorInterface();
        settings.setValidatorCommandLine(this.localClicsInterfaceCustomValidatorCommandLine);
        
        //put the settings object into the Validator Interface mode indicated in the GUI
        if (this.getUsePC2ValStdRadioButton().isSelected()) {
            settings.setUsePC2ValidatorInterface();
        } else {
            settings.setUseClicsValidatorInterface();
        }
        
        return settings;
    }

    /**
     * Returns a {@link PC2ValidatorSettings} object containing the values currently displayed in the GUI.
     * Displays an error message and throws {@link InvalidFieldValue} if the values displayed in the GUI
     * are illegal (for example, if the PC2 Validator has been selected but no PC2 Validator Mode has been chosen.
     * 
     * @return a PC2ValidatorSettings object populated from the GUI
     * @throws {@link InvalidFieldValue} if an invalid tolerance value is detected
     */
    private PC2ValidatorSettings getPC2ValidatorSettingsFromFields() {
        
        //check if the PC2 Validator has been selected but no Mode chosen
        if (getUsePC2ValidatorRadioButton().isSelected() && getPc2ValidatorOptionComboBox().getSelectedIndex()<=0) {
            throw new InvalidFieldValue("PC2 Validator selected but no valid Mode chosen");
        }
        
        PC2ValidatorSettings settings = new PC2ValidatorSettings();
        
        settings.setIgnoreCaseOnValidation(getPc2ValidatorIgnoreCaseCheckBox().isSelected());;
        settings.setValidatorProgramName(Constants.PC2_VALIDATOR_NAME);
        settings.setValidatorCommandLine(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);
        settings.setWhichPC2Validator(getPc2ValidatorOptionComboBox().getSelectedIndex());
        
        return settings;
    }

    /**
     * Returns a {@link ClicsValidatorSettings} object containing the values currently displayed in the GUI.
     * Displays an error message and throws {@link InvalidFieldValue} if either the absolute or relative
     * tolerance are selected and the string in the corresponding text box is invalid.
     * 
     * @return a ClicsValidatorSettings object populated from the GUI
     * @throws {@link InvalidFieldValue} if an invalid tolerance value is detected
     */
    private ClicsValidatorSettings getCLICSValidatorSettingsFromFields() {
        
        ClicsValidatorSettings settings = new ClicsValidatorSettings();
        
        settings.setCaseSensitive(getCLICSValidatorCaseSensitiveCheckBox().isSelected());
        settings.setSpaceSensitive(getCLICSSpaceSensitiveCheckBox().isSelected());
        
        if (getFloatAbsoluteToleranceCheckBox().isSelected()) {

            double absTol;
            try {
                absTol = Double.parseDouble(getFloatAbsoluteToleranceTextField().getText());
            } catch (NumberFormatException e) {
                showMessage("Invalid absolute tolerance value");
                throw new InvalidFieldValue("Invalid absolute tolerance value");
            }
            settings.setFloatAbsoluteTolerance(absTol);
            
        } else {
            settings.disableFloatAbsoluteTolerance();
        }
        
        if (getFloatRelativeToleranceCheckBox().isSelected()) {

            double relTol;
            try {
                relTol = Double.parseDouble(getFloatRelativeToleranceTextField().getText());
            } catch (NumberFormatException e) {
                showMessage("Invalid relative tolerance value");
                throw new InvalidFieldValue("Invalid relative tolerance value");
            }
            settings.setFloatRelativeTolerance(relTol);
        
        } else {
            settings.disableFloatRelativeTolerance();
        }
        
        return settings;
    }
    
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    private JLabel getCustomValidatorInterfaceLabel() {
        if (lblValidatorInterface == null) {
        	lblValidatorInterface = new JLabel("Validator Interface:");
        }
        return lblValidatorInterface;
    }
    private JRadioButton getUsePC2ValStdRadioButton() {
        if (rdbtnUsePcStandard == null) {
        	rdbtnUsePcStandard = new JRadioButton("Use PC^2 Standard Interface");
        	rdbtnUsePcStandard.setSelected(false);
            rdbtnUsePcStandard.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // switching to Use PC2 Standard Interface for this Custom Validator;
                    // check to see if we are editing a problem which might already have a PC2 Interface Validator Command Line
                    if (problem != null && problem.getCustomValidatorSettings() != null && problem.getCustomValidatorSettings().isUsePC2ValidatorInterface()
                            && problem.getValidatorCommandLine() != null && problem.getValidatorCommandLine().trim().length() > 0) {
                        // we are editing a problem which has a Custom Validator which is using the PC2 Interface Standard and has a
                        // Custom Validator Command Line which is not empty; put that command line in the GUI
                        getCustomValidatorCommandLineTextField().setText(problem.getValidatorCommandLine().trim());
                        
                    } else if (localPC2InterfaceCustomValidatorCommandLine != null) {
                        //we have a local version of the pc2 command line; fill that into the command line on the GUI
                        getCustomValidatorCommandLineTextField().setText(localPC2InterfaceCustomValidatorCommandLine);
                        
                    } else {
                        // there's currently no definition for the Validator Command Line in any current problem or locally;
                        // put the default in the command line GUI
                        getCustomValidatorCommandLineTextField().setText(Constants.DEFAULT_PC2_VALIDATOR_COMMAND);
                    }
                    enableUpdateButton();
                }
            });
            validatorStandardButtonGroup.add(rdbtnUsePcStandard);
        }
        return rdbtnUsePcStandard;
    }
    private JRadioButton getUseClicsValStdRadioButton() {
        if (rdbtnUseClicsStandard == null) {
        	rdbtnUseClicsStandard = new JRadioButton("Use CLICS Standard Interface");
        	rdbtnUseClicsStandard.setSelected(true);
        	rdbtnUseClicsStandard.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
                    // switching to Use Clics Standard Interface for this Custom Validator;
                    // check to see if we are editing a problem which might already have a Clics Interface Validator Command Line
                    if (problem != null && problem.getCustomValidatorSettings() != null && problem.getCustomValidatorSettings().isUseClicsValidatorInterface()
                            && problem.getValidatorCommandLine() != null && problem.getValidatorCommandLine().trim().length() > 0) {
                        // we are editing a problem which has a Custom Validator which is using the Clics Interface Standard and has a
                        // Custom Validator Command Line which is not empty; put that command line in the GUI
                        getCustomValidatorCommandLineTextField().setText(problem.getValidatorCommandLine().trim());
                        
                    } else if (localClicsInterfaceCustomValidatorCommandLine != null) {
                        //we have a local version of the Clics command line; fill that into the command line on the GUI
                        getCustomValidatorCommandLineTextField().setText(localClicsInterfaceCustomValidatorCommandLine);
                        
                    } else {
                        // there's currently no definition for the Validator Command Line in any current problem or locally;
                        // put the default in the command line GUI
                        getCustomValidatorCommandLineTextField().setText(Constants.DEFAULT_CLICS_VALIDATOR_COMMAND);
                    }
                    enableUpdateButton();
                }
        	});
            validatorStandardButtonGroup.add(rdbtnUseClicsStandard);
        }
        return rdbtnUseClicsStandard;
    }
    private JLabel getLabelWhatsThisPC2ValStd() {
        if (lblWhatsThisPC2ValStd == null) {
            
//        	lblWhatsThisPC2ValStd = new JLabel("<What's This?>");
//        	lblWhatsThisPC2ValStd.setForeground(Color.blue);
        	
            ImageIcon iconImage = (ImageIcon) UIManager.getIcon("OptionPane.questionIcon");
            Image image = iconImage.getImage();
            lblWhatsThisPC2ValStd = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            lblWhatsThisPC2ValStd.setToolTipText("What's This? (click for additional information)");

        	lblWhatsThisPC2ValStd.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisPC2ValStdMessage, "PC^2 Validator Interface Standard", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
        	lblWhatsThisPC2ValStd.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
        return lblWhatsThisPC2ValStd;
    }
    private JLabel getLabelWhatsThisCLICSValStd() {
        if (lblWhatsThisCLICSValStd == null) {
            
//        	lblWhatsThisCLICSValStd = new JLabel("<What's This?>");
//        	lblWhatsThisCLICSValStd.setForeground(Color.blue);
        	
            ImageIcon iconImage = (ImageIcon) UIManager.getIcon("OptionPane.questionIcon");
            Image image = iconImage.getImage();
            lblWhatsThisCLICSValStd = new JLabel(new ImageIcon(getScaledImage(image, 20, 20)));
            lblWhatsThisCLICSValStd.setToolTipText("What's This?  (click for additional information)");

            lblWhatsThisCLICSValStd.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JOptionPane.showMessageDialog(null, whatsThisCLICSValStdMessage, "CLICS Validator Interface Standard", JOptionPane.INFORMATION_MESSAGE, null);
                }
            });
        	lblWhatsThisCLICSValStd.setBorder(new EmptyBorder(0, 0, 0, 0));
        }
        return lblWhatsThisCLICSValStd;
    }
    
    private JPanel getInputValidatorPane() {
        if (inputValidatorPane == null) {
        	inputValidatorPane = new JPanel();
        	inputValidatorPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        	inputValidatorPane.setLayout(new BoxLayout(inputValidatorPane, BoxLayout.Y_AXIS));
        	inputValidatorPane.add(getVerticalStrut_8());
        	
        	inputValidatorPane.add(getDefineInputValidatorPanel());
        	inputValidatorPane.add(getVerticalStrut_9());
        	inputValidatorPane.add(getExecuteInputValidatorPanel());
        	inputValidatorPane.add(getVerticalStrut_10());
        	inputValidatorPane.add(getInputValidationResultPanel());
        	inputValidatorPane.add(getVerticalStrut_6());
        }
        return inputValidatorPane;
    }
    private JLabel getInputValidatorProgramNameLabel() {
        if (inputValidatorProgramNameLabel == null) {
        	inputValidatorProgramNameLabel = new JLabel("Input Validator Program: ");
        	inputValidatorProgramNameLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        	inputValidatorProgramNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
        	inputValidatorProgramNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        	inputValidatorProgramNameLabel.setToolTipText("The name, including the full path to it, of the program to be used to validate input data");
        }
        return inputValidatorProgramNameLabel;
    }
    private JTextField getInputValidatorProgramNameTextField() {
        if (inputValidatorProgramNameTextField == null) {
        	inputValidatorProgramNameTextField = new JTextField();
        	inputValidatorProgramNameTextField.setPreferredSize(new Dimension(300, 25));
        	inputValidatorProgramNameTextField.setMinimumSize(new Dimension(300, 25));
        	inputValidatorProgramNameTextField.setColumns(50);
        	inputValidatorProgramNameTextField.setText("");
        	inputValidatorProgramNameTextField.setToolTipText("");
        }
        return inputValidatorProgramNameTextField;
    }
    private JLabel getLblInputValidatorInvocation() {
        if (lblInputValidatorInvocation == null) {
        	lblInputValidatorInvocation = new JLabel("Input Validator command:");
        	lblInputValidatorInvocation.setAlignmentX(Component.RIGHT_ALIGNMENT);
        	lblInputValidatorInvocation.setToolTipText("The command to be used to invoke the Input Validator");
        	lblInputValidatorInvocation.setFont(new Font("Tahoma", Font.PLAIN, 11));
        }
        return lblInputValidatorInvocation;
    }
    private JTextField getInputValidatorCommandTextField() {
        if (inputValidatorCommandTextField == null) {
        	inputValidatorCommandTextField = new JTextField();
        	inputValidatorCommandTextField.setPreferredSize(new Dimension(300, 25));
        	inputValidatorCommandTextField.setMinimumSize(new Dimension(300, 25));
        	inputValidatorCommandTextField.setColumns(50);
        	inputValidatorCommandTextField.setText("");
        	inputValidatorCommandTextField.setToolTipText("");
        	
        	inputValidatorCommandTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                    updateRunValidatorButtonState();
                }
            });

        }
        return inputValidatorCommandTextField;
    }
    
    /**
     * Sets the "Run Input Validator" button enabled or disabled according to whether all attributes necessary to run the
     * Input Validator have been specified.
     */
    private void updateRunValidatorButtonState() {
        boolean enableButton = true ;
        
        //don't enable the button if there's no validator command defined
        if (getInputValidatorCommandTextField().getText() == null || getInputValidatorCommandTextField().getText().equals("")) {
            enableButton = false;
        }
        
        //don't enable the button if "Files on disk in folder" is selected but there's no folder specified
        if (getFilesOnDiskInFolderRadioButton().isSelected()) {
            if (getInputValidatorFilesOnDiskTextField().getText() == null || getInputValidatorFilesOnDiskTextField().getText().equals("")) {
                enableButton = false;
            }
        }
        
        //don't enable the button if "Files just loaded via 'input data files' pane" is selected but there's no files on the MTSOV pane
        if (getFilesJustLoadedRadioButton().isSelected()) {
            if (getMultipleDataSetPane().getTestDataSetsListBox().getModel().getRowCount() <= 0) {
                //there are no data rows in the MTSOVPane table
                enableButton = false ;               
            }
        }
        
        // don't enable the button if "Files previously loaded into PC2" is selected but there's no data files loaded
        if (getFilesPreviouslyLoadedRadioButton().isSelected()) {
            
            //make sure we have a problem from which we can possibly load data files
            if (problem != null) {
                
                // make sure the problem in the contest model has data files
                ProblemDataFiles pdf = getContest().getProblemDataFile(getProblem());
                if (pdf != null) {
                    //make sure the data files contain judge's answer files
                    SerializedFile[] answerFiles = pdf.getJudgesAnswerFiles();
                    if (answerFiles == null || answerFiles.length <= 0) {
                        //problem has no judge's answer files; don't enable Run button
                        enableButton = false;
                    }
                } else {
                    //problem has no data files; don't enable Run button
                    enableButton = false;
                }
            } else {
                //problem is null; don't enable Run button
                enableButton = false ;
            }
        }
        
        //set the button-enabled condition based on the above determinations
        getRunInputValidatorButton().setEnabled(enableButton);
        
        //update the tooltip to match the current state
        if (enableButton) {
            getRunInputValidatorButton().setToolTipText("Run the defined Input Validator command using the specified set of Input Data files");
        } else {
            //there must be something blocking permission to run the input validator; set the tooltip to indicate the condition(s)
            String toolTip = "";
            
            //check for the required validator command line
            if (getInputValidatorCommandTextField() == null || getInputValidatorCommandTextField().getText().equals("")) {
                toolTip += "No Input Validator Command defined";
            }
            
            //check whether, if files are coming from a disk folder, there is a folder defined
            if (getFilesOnDiskInFolderRadioButton().isSelected()) {
                if (getInputValidatorFilesOnDiskTextField().getText() == null || getInputValidatorFilesOnDiskTextField().getText().equals("")) {
                    if (toolTip.equals("")) {
                        toolTip = "No Input File folder defined";
                    } else {
                        toolTip += "; no Input File folder defined"; 
                    }
                }
            }
            
            //check whether, if files are coming from the Load Data Files pane, there are files in that pane
            if (getFilesJustLoadedRadioButton().isSelected()) {
                if (getMultipleDataSetPane().getTestDataSetsListBox().getRowCount() <= 0) {
                    if (toolTip.equals("")) {
                        toolTip = "No Input Files defined on Input Data Files pane";
                    } else {
                        toolTip += "; no Input Files defined on Input Data Files pane"; 
                    }
                }
            }
            
            //check whether, if files are coming from those previously loaded, there actually ARE files loaded
            if (getFilesPreviouslyLoadedRadioButton().isSelected()) {
                
                //make sure we have a problem from which we can possibly load data files
                if (problem == null || getContest().getProblemDataFile(getProblem()) == null 
                        || getContest().getProblemDataFile(getProblem()).getJudgesDataFiles() == null
                        || getContest().getProblemDataFile(getProblem()).getJudgesDataFiles().length <= 0) {
                    if (toolTip.equals("")) {
                        toolTip = "No Input Files saved in current problem";
                    } else {
                        toolTip += "; no Input Files saved in current problem"; 
                    }
                }
            }
            
            if (!toolTip.equals("")) {
                toolTip += "; cannot run Input Validator";
            } else {
                //we shouldn't be able to get here; the tooltip shouldn't be empty if 'enable' was false (there must be some condition
                // suppressing the enable, so why didn't we pick it up in the above set of 'if' statements??)
                getLog().log(Log.WARNING, "Empty Run Validator button tooltip when this shouldn't be possible");
            }
            
            getRunInputValidatorButton().setToolTipText(toolTip);
        }

    }
    private JButton getRunInputValidatorButton() {
        if (validateInputDataButton == null) {
        	validateInputDataButton = new JButton("Run Input Validator");
        	validateInputDataButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        runInputDataValidationTest() ;
        	        enableUpdateButton();
        	    }
        	});
        }
        return validateInputDataButton;
    }
    
    /**
     * Runs the Input Validator specified in the GUI, using the GUI-specified Input Validator Command, against 
     */
    private void runInputDataValidationTest() {
        
        //get the command line from the GUI
        String cmdline = getInputValidatorCommandLine();
        
        //get an execute directory name
        String executeDir = getExecuteDirectoryName();        
  
        //TODO: need to save the Serialized File in the model (but don't do that in this method - do it in the Add/Update button handler)
        SerializedFile validatorProg = new SerializedFile(getInputValidatorProgramNameTextField().getText());
        try {
            Utilities.checkSerializedFileError(validatorProg);
        } catch (Exception e) {
            showMessage(getParentFrame(), "Error Creating Validator", "An error occurred while creating the validator program" + e.getMessage());
            setInputValidationStatus(InputValidationStatus.ERROR);
            return;
        }
        
        SerializedFile [] dataFiles = getDataFiles();
        
        InputValidatorRunner runner = new InputValidatorRunner(getContest(), getController());
        
        InputValidationResult[] results = null;
        try {
            results = runner.runInputValidator(validatorProg, cmdline, executeDir, dataFiles);
        } catch (ExecuteException e) {
            JOptionPane.showMessageDialog(this, "Error running Input Validator: \n" + e.getMessage() + "\nCheck logs for further details", "Input Validator Error", JOptionPane.WARNING_MESSAGE);
            getInputValidationResultSummaryTextLabel().setText("Errror Running Input Validator");
            getInputValidationResultSummaryTextLabel().setForeground(Color.RED);
            setInputValidationStatus(InputValidationStatus.ERROR);
        }
            
        
        //update the results table
        ((InputValidationResultsTableModel)getInputValidatorResultsTable().getModel()).setResults(results);
        ((AbstractTableModel) getInputValidatorResultsTable().getModel()).fireTableDataChanged();
        
        //adjust the column widths in the updated table
//        getInputValidatorResultsTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        TableColumnAdjuster tca = new TableColumnAdjuster(getInputValidatorResultsTable());
//        tca.adjustColumns();  //this is shrinking columns to use less than the component width; looks ugly
        
        if (results != null) {
            // update the result summary label

            boolean allPassed = true;
            for (int i = 0; i < results.length; i++) {
                if (!results[i].isPassed()) {
                    allPassed = false;
                    break;
                }
            }
            String resultSummaryString = allPassed ? "All Input Data Files passed validation"
                    : "One or more Input Data Files FAILED validation";
            Color color = allPassed ? Color.green : Color.red;
            getInputValidationResultSummaryTextLabel().setText(resultSummaryString);
            getInputValidationResultSummaryTextLabel().setForeground(color);
            if (allPassed) {
                setInputValidationStatus(InputValidationStatus.PASSED);   
            } else {
                setInputValidationStatus(InputValidationStatus.FAILED);
            }
        }
    }
    
    private InputValidationStatus getInputValidationStatus() {
        return this.inputValidationStatus;
    }
    
    private void setInputValidationStatus(InputValidationStatus result) {
        this.inputValidationStatus = result;
    }

    /**
     * Returns an array of SerializedFiles containing data files to be validated.
     * 
     * @return
     */
    private SerializedFile [] getDataFiles() {
        
        SerializedFile [] retArray = null;
        
        //check if the files are coming from either the MSTOVPane or from a folder
        if (getFilesOnDiskInFolderRadioButton().isSelected() || getFilesJustLoadedRadioButton().isSelected()) {

            //get the names of the data files to be validated
            String [] inputFileNames = getInputFileNames();
            
            if (inputFileNames == null || inputFileNames.length == 0) {
                showMessage(getParentFrame(), "No Data Files found", "Error - no input data files found");
                getLog().log(Log.INFO, "Request to run Input Validator, but no input data files found");
                throw new RuntimeException("Request to run Input Validator, but no input data files found");
            } else {
                
                //construct SerializedFiles from the specified file names
                
                retArray = new SerializedFile [inputFileNames.length];
                
                for (int i=0; i< inputFileNames.length; i++) {
                    
                    retArray[i] = new SerializedFile(inputFileNames[i]);
                    try {
                        Utilities.checkSerializedFileError(retArray[i]);
                    } catch (Exception e) {
                        showMessage(getParentFrame(), "Error creating data files", "An error occurred while serializing the data files: " + e.getMessage());
                        getLog().log(Log.WARNING, "An error occurred while serializing the data files: " + e.getMessage());
                        return null;
                    }
                }
            }
            
        } else if (getFilesPreviouslyLoadedRadioButton().isSelected()) {
            //get the Serialized Judge's Data files out of the contest model and return that
            
            retArray = originalProblemDataFiles.getJudgesDataFiles();
            
        } else {
            //we should never be able to get here -- the button group should insure that exactly one button is pushed
            System.err.println ("Undefined condition in EditProblemPane.getDataFiles(): no Input Data File radio button is selected!");
            getLog().log(Log.WARNING, "Undefined condition in EditProblemPane.getDataFiles(): no Input Data File radio button is selected!");
        }
        
        return retArray;
        
    }
    
    
    

    /**
     * Returns an array of Strings giving the names of Problem Input Data Files which are to be checked 
     * by running the currently-specified Input Validator using each file as input.
     * 
     * Uses the currently active "Input Data Files to Validate" button to determine the set of data file
     * names to be returned.  If no data files could be found at the specified source, null is returned.
     *  
     * @return an Array of Strings containing Input Data file names, or null if no files were found
     */
    private String[] getInputFileNames() {

        String [] retVal = null;
        
        if (getFilesOnDiskInFolderRadioButton().isSelected()) {
            //read the specified folder and return the names of files in that folder
            String folderName = getInputValidatorFilesOnDiskTextField().getText();
            if (folderName != null && !folderName.trim().equals("")) {
                
                File folderPath = new File(folderName);
                if (folderPath.exists() && folderPath.isDirectory() && folderPath.canRead()) {
                    
                    //at this point we know the folder exists and we can read it; copy the names of its files
                    retVal = folderPath.list();
                    for (int i = 0; i < retVal.length; i++) {
                        retVal[i] = folderName + File.separator + retVal[i];                        
                    }
                }
            }
            
        } else if (getFilesJustLoadedRadioButton().isSelected()) {
            
            //read the MTSOVPane data table and return the files in that table (if any)
            JTable inputDataFilesTable = getMultipleDataSetPane().getTestDataSetsListBox();
            if (inputDataFilesTable != null) {
                
                TestCaseTableModel tableModel = (TestCaseTableModel) inputDataFilesTable.getModel();
            
                if (tableModel != null) {
                    
                    ProblemDataFiles pdf = tableModel.getFiles();
                    
                    if (pdf != null) {
                        
                        SerializedFile [] dataFiles = pdf.getJudgesDataFiles();
                        
                        if (dataFiles.length > 0 ) {
                            
                            //at this point we know there are judge's data files in the MTSOVPane table; copy their names
                            retVal = new String [dataFiles.length];
                            for (int file = 0; file < dataFiles.length; file++) {
                                retVal[file] = dataFiles[file].getAbsolutePath();
                            }
                        }
                    }
                }
            }
            
        } else if (getFilesPreviouslyLoadedRadioButton().isSelected()) {
            
            //reach into the Problem (if defined) and get the judge's data files (if any)
            if (problem != null) {
                
                ProblemDataFiles pdf = getContest().getProblemDataFile(problem);
                
                if (pdf != null) {
                    
                    SerializedFile [] dataFiles = pdf.getJudgesDataFiles();
                    
                    if (dataFiles.length > 0 ) {
                        
                        //at this point we know there are judge's data files in the problem; copy their names
                        retVal = new String [dataFiles.length];
                        for (int file = 0; file < dataFiles.length; file++) {
                            retVal[file] = dataFiles[file].getAbsolutePath();
                        }
                    }
                }
            }
            
        } else {
            //none of the three radio buttons is selected
            getLog().log(Log.SEVERE, "Error - no 'Input Data Files to Validate' button is selected (shouldn't be possible)");
        }
        
        return retVal ;
    }

    public String getExecuteDirectoryName() {
        return "inputValidate" + getContest().getClientId().getSiteNumber() + getContest().getClientId().getName() ;
    }

    /**
     * Remove all files from specified directory, including subdirectories.
     * 
     * @param dirName
     *            directory to be cleared.
     * @return true if directory was cleared.
     */
    public boolean clearDirectory(String dirName) {
        File dir = null;
        boolean result = true;

        dir = new File(dirName);
        //TODO: need to handle if the new FILE() returns null (e.g. for an empty filename string)
        String[] filesToRemove = dir.list();
        for (int i = 0; i < filesToRemove.length; i++) {
            File fn1 = new File(dirName + File.separator + filesToRemove[i]);
            if (fn1.isDirectory()) {
                // recurse through any directories
                result &= clearDirectory(dirName + File.separator + filesToRemove[i]);
            }
            result &= fn1.delete();
        }
        return (result);
    }
    /**
     * Return string minus last extension. <br>
     * Finds last . (period) in input string, strips that period and all other characters after that last period. If no period is found in string, will return a copy of the original string. <br>
     * Unlike the Unix basename program, no extension is supplied.
     * 
     * @param original
     *            the input string
     * @return a string with all text after last . removed
     */
    public String removeExtension(String original) {
        String outString = new String(original);

        // Strip off all text after and including final dot

        int dotIndex = outString.lastIndexOf('.', outString.length() - 1);
        if (dotIndex != -1) {
            outString = outString.substring(0, dotIndex);
        }

        return outString;

    }

    private String getInputValidatorCommandLine() {
       
        String progName = getInputValidatorProgramNameTextField().getText();
        String cmd = getInputValidatorCommandTextField().getText();
        
        cmd = replaceString(cmd, "{:inputvalidator}", progName);
        cmd = replaceString(cmd, "{:basename}", Utilities.basename(removeExtension(progName)));
        
        return cmd;
    }

    /**
     * Replace all instances of beforeString with afterString.
     * 
     * If before string is not found, then returns original string.
     * 
     * @param origString
     *            string to be modified
     * @param beforeString
     *            string to search for
     * @param afterString
     *            string to replace beforeString
     * @return original string with all beforeString instances replaced with afterString
     */
    public String replaceString(String origString, String beforeString, String afterString) {

        if (origString == null || afterString == null) {
            return origString;
        }

        int startIdx = origString.lastIndexOf(beforeString);

        if (startIdx == -1) {
            return origString;
        }

        StringBuffer buf = new StringBuffer(origString);

        while (startIdx != -1) {
            buf.replace(startIdx, startIdx + beforeString.length(), afterString);
            startIdx = origString.lastIndexOf(beforeString, startIdx - 1);
        }

        return buf.toString();
    }


    private JPanel getDefineInputValidatorPanel() {
        if (defineInputValidatorPanel == null) {
            
        	defineInputValidatorPanel = new JPanel();
        	defineInputValidatorPanel.setBorder(new TitledBorder(null, "Define Input Validator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        	
        	GridBagLayout gbl_defineInputValidatorPanel = new GridBagLayout();
        	gbl_defineInputValidatorPanel.columnWidths = new int[] {20, 60, 60};
        	gbl_defineInputValidatorPanel.rowHeights = new int[]{20, 20};
        	gbl_defineInputValidatorPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
        	gbl_defineInputValidatorPanel.rowWeights = new double[]{0.0, 1.0};
        	defineInputValidatorPanel.setLayout(gbl_defineInputValidatorPanel);
        	
        	GridBagConstraints gbc_inputValidatorProgramNameLabel = new GridBagConstraints();
        	gbc_inputValidatorProgramNameLabel.anchor = GridBagConstraints.EAST;
        	gbc_inputValidatorProgramNameLabel.insets = new Insets(0, 20, 5, 0);
        	gbc_inputValidatorProgramNameLabel.gridx = 0;
        	gbc_inputValidatorProgramNameLabel.gridy = 0;
        	defineInputValidatorPanel.add(getInputValidatorProgramNameLabel(), gbc_inputValidatorProgramNameLabel);
        	
        	GridBagConstraints gbc_inputValidatorProgramNameTextField = new GridBagConstraints();
        	gbc_inputValidatorProgramNameTextField.anchor = GridBagConstraints.WEST;
        	gbc_inputValidatorProgramNameTextField.insets = new Insets(0, 0, 5, 5);
        	gbc_inputValidatorProgramNameTextField.gridx = 1;
        	gbc_inputValidatorProgramNameTextField.gridy = 0;
        	defineInputValidatorPanel.add(getInputValidatorProgramNameTextField(), gbc_inputValidatorProgramNameTextField);
        	
        	GridBagConstraints gbc_chooseInputValidatorProgramButton = new GridBagConstraints();
        	gbc_chooseInputValidatorProgramButton.anchor = GridBagConstraints.WEST;
        	gbc_chooseInputValidatorProgramButton.insets = new Insets(0, 0, 0, 5);
        	gbc_chooseInputValidatorProgramButton.gridx = 2;
        	gbc_chooseInputValidatorProgramButton.gridy = 0;
        	defineInputValidatorPanel.add(getChooseInputValidatorProgramButton(), gbc_chooseInputValidatorProgramButton);
        	
        	GridBagConstraints gbc_lblInputValidatorInvocation = new GridBagConstraints();
        	gbc_lblInputValidatorInvocation.anchor = GridBagConstraints.EAST;
        	gbc_lblInputValidatorInvocation.insets = new Insets(0, 20, 5, 0);
        	gbc_lblInputValidatorInvocation.gridx = 0;
        	gbc_lblInputValidatorInvocation.gridy = 1;
        	defineInputValidatorPanel.add(getLblInputValidatorInvocation(), gbc_lblInputValidatorInvocation);
        	
        	GridBagConstraints gbc_inputValidatorCommandTextField = new GridBagConstraints();
        	gbc_inputValidatorCommandTextField.insets = new Insets(0, 0, 5, 5);
        	gbc_inputValidatorCommandTextField.anchor = GridBagConstraints.WEST;
        	gbc_inputValidatorCommandTextField.gridx = 1;
        	gbc_inputValidatorCommandTextField.gridy = 1;
        	defineInputValidatorPanel.add(getInputValidatorCommandTextField(), gbc_inputValidatorCommandTextField);
        }
        return defineInputValidatorPanel;
    }
    private JPanel getExecuteInputValidatorPanel() {
        if (executeInputValidatorPanel == null) {
        	executeInputValidatorPanel = new JPanel();
        	executeInputValidatorPanel.setBorder(new TitledBorder(null, "Execute Input Validator", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        	executeInputValidatorPanel.setLayout(new BoxLayout(executeInputValidatorPanel, BoxLayout.X_AXIS));
        	executeInputValidatorPanel.add(getHorizontalStrut_3());
        	executeInputValidatorPanel.add(getRunInputValidatorButton());
        	executeInputValidatorPanel.add(getHorizontalStrut_4());
        	executeInputValidatorPanel.add(getInputValidatorDataFilesPanel());
        }
        return executeInputValidatorPanel;
    }
    private JPanel getInputValidatorDataFilesPanel() {
        if (inputValidatorDataFilesPanel == null) {
        	inputValidatorDataFilesPanel = new JPanel();
        	inputValidatorDataFilesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	inputValidatorDataFilesPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Input Data Files to Validate:", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        	GridBagLayout gbl_inputValidatorDataFilesPanel = new GridBagLayout();
        	gbl_inputValidatorDataFilesPanel.columnWidths = new int[] {50, 200, 50};
        	gbl_inputValidatorDataFilesPanel.rowHeights = new int[] {25, 25, 25};
        	gbl_inputValidatorDataFilesPanel.columnWeights = new double[]{0.0, 0.0, 0.0};
        	gbl_inputValidatorDataFilesPanel.rowWeights = new double[]{0.0, 0.0, 0.0};
        	inputValidatorDataFilesPanel.setLayout(gbl_inputValidatorDataFilesPanel);
        	GridBagConstraints gbc_filesPreviouslyLoadedRadioButton = new GridBagConstraints();
        	gbc_filesPreviouslyLoadedRadioButton.gridwidth = 2;
        	gbc_filesPreviouslyLoadedRadioButton.anchor = GridBagConstraints.WEST;
        	gbc_filesPreviouslyLoadedRadioButton.insets = new Insets(0, 0, 5, 5);
        	gbc_filesPreviouslyLoadedRadioButton.gridx = 0;
        	gbc_filesPreviouslyLoadedRadioButton.gridy = 0;
        	inputValidatorDataFilesPanel.add(getFilesPreviouslyLoadedRadioButton(), gbc_filesPreviouslyLoadedRadioButton);
        	GridBagConstraints gbc_filesJustLoadedRadioButton = new GridBagConstraints();
        	gbc_filesJustLoadedRadioButton.gridwidth = 2;
        	gbc_filesJustLoadedRadioButton.anchor = GridBagConstraints.WEST;
        	gbc_filesJustLoadedRadioButton.insets = new Insets(0, 0, 5, 5);
        	gbc_filesJustLoadedRadioButton.gridx = 0;
        	gbc_filesJustLoadedRadioButton.gridy = 1;
        	inputValidatorDataFilesPanel.add(getFilesJustLoadedRadioButton(), gbc_filesJustLoadedRadioButton);
        	GridBagConstraints gbc_filesOnDiskInFolderRadioButton = new GridBagConstraints();
        	gbc_filesOnDiskInFolderRadioButton.anchor = GridBagConstraints.WEST;
        	gbc_filesOnDiskInFolderRadioButton.insets = new Insets(0, 0, 0, 5);
        	gbc_filesOnDiskInFolderRadioButton.gridx = 0;
        	gbc_filesOnDiskInFolderRadioButton.gridy = 2;
        	inputValidatorDataFilesPanel.add(getFilesOnDiskInFolderRadioButton(), gbc_filesOnDiskInFolderRadioButton);
        	GridBagConstraints gbc_inputValidatorFilesOnDiskTextField = new GridBagConstraints();
        	gbc_inputValidatorFilesOnDiskTextField.anchor = GridBagConstraints.WEST;
        	gbc_inputValidatorFilesOnDiskTextField.insets = new Insets(0, 0, 5, 5);
        	gbc_inputValidatorFilesOnDiskTextField.gridx = 1;
        	gbc_inputValidatorFilesOnDiskTextField.gridy = 2;
        	inputValidatorDataFilesPanel.add(getInputValidatorFilesOnDiskTextField(), gbc_inputValidatorFilesOnDiskTextField);
        	GridBagConstraints gbc_chooseInputFilesButton = new GridBagConstraints();
        	gbc_chooseInputFilesButton.insets = new Insets(0, 0, 0, 5);
        	gbc_chooseInputFilesButton.gridx = 2;
        	gbc_chooseInputFilesButton.gridy = 2;
        	inputValidatorDataFilesPanel.add(getChooseInputFilesButton(), gbc_chooseInputFilesButton);
        }
        return inputValidatorDataFilesPanel;
    }
    private JRadioButton getFilesPreviouslyLoadedRadioButton() {
        if (filesPreviouslyLoadedRadioButton == null) {
        	filesPreviouslyLoadedRadioButton = new JRadioButton("Files previously loaded into PC2");
        	filesPreviouslyLoadedRadioButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
                    enableUpdateButton();
                    updateRunValidatorButtonState();
        	    }
        	});
        	inputFileLocationButtonGroup.add(filesPreviouslyLoadedRadioButton);
        	filesPreviouslyLoadedRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        return filesPreviouslyLoadedRadioButton;
    }
    private JRadioButton getFilesJustLoadedRadioButton() {
        if (filesJustLoadedRadioButton == null) {
        	filesJustLoadedRadioButton = new JRadioButton("Files just loaded via \"Input Data Files\" pane");
        	filesJustLoadedRadioButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
                    enableUpdateButton();
                    updateRunValidatorButtonState();
        	    }
        	});
        	inputFileLocationButtonGroup.add(filesJustLoadedRadioButton);
        }
        return filesJustLoadedRadioButton;
    }
    private JTextField getInputValidatorFilesOnDiskTextField() {
        if (inputValidatorFilesOnDiskTextField == null) {
        	inputValidatorFilesOnDiskTextField = new JTextField();
        	inputValidatorFilesOnDiskTextField.setMinimumSize(new Dimension(300, 25));
        	inputValidatorFilesOnDiskTextField.setPreferredSize(new Dimension(300, 25));
        	inputValidatorFilesOnDiskTextField.setColumns(50);
            
        	inputValidatorFilesOnDiskTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                    updateRunValidatorButtonState();
                }
            });

        }
        return inputValidatorFilesOnDiskTextField;
    }
    
    private JButton getChooseInputValidatorProgramButton() {
        if (chooseInputValidatorProgramButton == null) {
            chooseInputValidatorProgramButton = new JButton("Choose...");
            chooseInputValidatorProgramButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectFile(getInputValidatorProgramNameTextField(), "Select Input Validator")) {
                        getInputValidatorProgramNameTextField().setToolTipText((getInputValidatorProgramNameTextField().getText()));
                        enableUpdateButton();
                    }
                }
            });
        }
        return chooseInputValidatorProgramButton;
    }
    
    /**
     * A button allowing the user to choose the directory from which Input Data Files to be validated
     * are to be loaded.
     * 
     * @return A JButton which displays a chooser dialog
     */
    private JButton getChooseInputFilesButton() {
        if (chooseInputFilesButton == null) {
        	chooseInputFilesButton = new JButton("Choose...");
        	chooseInputFilesButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        String directory = selectDirectory(getInputValidatorFilesOnDiskTextField(),"Select Input File Folder");
                    if (directory != null && !directory.equals("")) {
                        getInputValidatorFilesOnDiskTextField().setText(directory);
                        getInputValidatorFilesOnDiskTextField().setToolTipText(directory);
                        enableUpdateButton();
                        updateRunValidatorButtonState();
                    }
        	    }
        	});
        }
        return chooseInputFilesButton;
    }
    
    private JPanel getInputValidationResultDetailsPanel() {
        if (inputValidationResultDetailsPanel == null) {
        	inputValidationResultDetailsPanel = new JPanel();
        	inputValidationResultDetailsPanel.setLayout(new BorderLayout(0, 0));
        	inputValidationResultDetailsPanel.add(getInputValidatorResultsScrollPane(), BorderLayout.CENTER);        	
        }
        return inputValidationResultDetailsPanel;
    }
    private JScrollPane getInputValidatorResultsScrollPane() {
        if (resultsScrollPane == null) {
        	resultsScrollPane = new JScrollPane();
        	resultsScrollPane.setViewportView(getInputValidatorResultsTable());
        }
        return resultsScrollPane;
    }
    private JTable getInputValidatorResultsTable() {
        if (resultsTable == null) {
        	resultsTable = new JTable(inputValidationResultsTableModel);
        	
            //set the desired options on the table
        	resultsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        	resultsTable.setFillsViewportHeight(true);
        	resultsTable.setRowSelectionAllowed(false);
        	resultsTable.getTableHeader().setReorderingAllowed(false);

        	//code from MultipleDataSetPane:
            // insert a renderer that will center cell contents
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < resultsTable.getColumnCount(); i++) {
                resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            resultsTable.setDefaultRenderer(String.class, centerRenderer);
//
//            // also center column headers (which use a different CellRenderer)
            //(this code came from MultipleDataSetPane, but the JTable here already has centered headers...
//            ((DefaultTableCellRenderer) testDataSetsListBox.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

            // change the header font
            JTableHeader header = resultsTable.getTableHeader();
            header.setFont(new Font("Dialog", Font.BOLD, 12));
            
            // render Result column as Pass/Fail on Green/Red background
            resultsTable.getColumn("Result").setCellRenderer(new PassFailCellRenderer());


        }
        return resultsTable;
    }
    private JPanel getInputValidationResultSummaryPanel() {
        if (inputValidationResultSummaryPanel == null) {
        	inputValidationResultSummaryPanel = new JPanel();
        	inputValidationResultSummaryPanel.add(getInputValidationResultsSummaryLabel());
        	inputValidationResultSummaryPanel.add(getInputValidationResultSummaryTextLabel());
        }
        return inputValidationResultSummaryPanel;
    }
    private JLabel getInputValidationResultsSummaryLabel() {
        if (inputValidationResultsSummaryLabel == null) {
        	inputValidationResultsSummaryLabel = new JLabel("Most Recent Status: ");
        }
        return inputValidationResultsSummaryLabel;
    }
    private JLabel getInputValidationResultSummaryTextLabel() {
        if (inputValidationResultSummaryTextLabel == null) {
        	inputValidationResultSummaryTextLabel = new JLabel("<No Input Validation test run yet>");
        	inputValidationResultSummaryTextLabel.setForeground(Color.black);
        }
        return inputValidationResultSummaryTextLabel;
    }
    private JPanel getInputValidationResultPanel() {
        if (inputValidationResultPanel == null) {
        	inputValidationResultPanel = new JPanel();
        	inputValidationResultPanel.setBorder(new TitledBorder(null, "Input Validation Results", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        	inputValidationResultPanel.setLayout(new BoxLayout(inputValidationResultPanel, BoxLayout.Y_AXIS));
        	inputValidationResultPanel.add(getVerticalStrut_7());
        	inputValidationResultPanel.add(getInputValidationResultSummaryPanel());
        	inputValidationResultPanel.add(getVerticalStrut_11());
        	inputValidationResultPanel.add(getInputValidationResultDetailsPanel());
        }
        return inputValidationResultPanel;
    }
    private Component getVerticalStrut_6() {
        if (verticalStrut_6 == null) {
        	verticalStrut_6 = Box.createVerticalStrut(20);
        }
        return verticalStrut_6;
    }
    private Component getVerticalStrut_7() {
        if (verticalStrut_7 == null) {
        	verticalStrut_7 = Box.createVerticalStrut(20);
        }
        return verticalStrut_7;
    }
    private Component getVerticalStrut_8() {
        if (verticalStrut_8 == null) {
        	verticalStrut_8 = Box.createVerticalStrut(20);
        }
        return verticalStrut_8;
    }
    private Component getVerticalStrut_9() {
        if (verticalStrut_9 == null) {
        	verticalStrut_9 = Box.createVerticalStrut(20);
        }
        return verticalStrut_9;
    }
    private Component getVerticalStrut_10() {
        if (verticalStrut_10 == null) {
        	verticalStrut_10 = Box.createVerticalStrut(20);
        }
        return verticalStrut_10;
    }
    private Component getVerticalStrut_11() {
        if (verticalStrut_11 == null) {
        	verticalStrut_11 = Box.createVerticalStrut(20);
        }
        return verticalStrut_11;
    }
    private JRadioButton getFilesOnDiskInFolderRadioButton() {
        if (filesOnDiskInFolderRadioButton == null) {
        	filesOnDiskInFolderRadioButton = new JRadioButton("Files on disk in folder:");
        	filesOnDiskInFolderRadioButton.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) {
        	        enableUpdateButton();
        	        updateRunValidatorButtonState();
        	    }
        	});
        	inputFileLocationButtonGroup.add(filesOnDiskInFolderRadioButton);
        }
        return filesOnDiskInFolderRadioButton;
    }
    private Component getHorizontalStrut_3() {
        if (horizontalStrut_3 == null) {
        	horizontalStrut_3 = Box.createHorizontalStrut(20);
        }
        return horizontalStrut_3;
    }
    private Component getHorizontalStrut_4() {
        if (horizontalStrut_4 == null) {
        	horizontalStrut_4 = Box.createHorizontalStrut(20);
        }
        return horizontalStrut_4;
    }
    
    public class PassFailCellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public void setValue(Object value) {
            if (value instanceof Boolean) {
                boolean passed = (Boolean) value;
                if (passed) {
                    setBackground(Color.green);
                    setForeground(Color.black);
                    setText("Pass");
                } else {
                    setBackground(Color.red);
                    setForeground(Color.white);
                    setText("Fail");
                }
            } else {
                // illegal value
                setBackground(Color.yellow);
                setText("??");
                getController().getLog().log(Log.SEVERE, "EditProblem.PassFailCellRenderer: unknown pass/fail result: ", value);
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(0, 0, 0, 0));

        }

    }

} 

