package edu.csus.ecs.pc2.core.report;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import edu.csus.ecs.pc2.VersionInfo;
import edu.csus.ecs.pc2.core.IController;
import edu.csus.ecs.pc2.core.list.RunComparatorByTeamProblem;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.Account;
import edu.csus.ecs.pc2.core.model.ClientId;
import edu.csus.ecs.pc2.core.model.ElementId;
import edu.csus.ecs.pc2.core.model.Filter;
import edu.csus.ecs.pc2.core.model.IContest;
import edu.csus.ecs.pc2.core.model.Judgement;
import edu.csus.ecs.pc2.core.model.JudgementRecord;
import edu.csus.ecs.pc2.core.model.Language;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.Run;

/**
 * Print all runs grouped by team.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class RunsByTeamReport implements IReport {

    private IContest contest;

    private IController controller;

    private Log log;

    private Filter reportFilter = new Filter();

    private void writeReport(PrintWriter printWriter) {

        // Runs
        Run[] runs = contest.getRuns();
        Arrays.sort(runs, new RunComparatorByTeamProblem());

        int count = 0;
        for (Run run : runs) {
            if (reportFilter.matches(run)) {
                count++;
            }
        }

        if (reportFilter.isThisSiteOnly()) {
            printWriter.print(" for site " + reportFilter.getSiteNumber());
        }

        ClientId currentTeam = null;

        /** 
         * Each team's runs.
         */
        Vector<Run> teamsRuns = new Vector<Run>();
        
        printWriter.println();
        if (count > 0) {
            for (Run run : runs) {
                if (reportFilter.matches(run)) {
                    if (run.getSubmitter().equals(currentTeam)) {
                        // accumulate all team's runs into teamsRuns
                        teamsRuns.add(run);

                    } else {
                        if (teamsRuns.size() > 0) {
                            printTeamsRuns(printWriter, currentTeam, teamsRuns);
                        }
                        currentTeam = run.getSubmitter();
                        teamsRuns = new Vector<Run>();
                        teamsRuns.add(run);
                    }
                }
            }

            if (teamsRuns.size() > 0) {
                printTeamsRuns(printWriter, currentTeam, teamsRuns);
            }
        }
    }

    private String getClientName(ClientId clientId) {
        Account account = contest.getAccount(clientId);
        if (account != null) {
            return account.getDisplayName();
        } else {
            return clientId.getName();
        }
    }

    private String getLanguageTitle(ElementId languageId) {
        Language language = contest.getLanguage(languageId);
        if (language != null) {
            return language.getDisplayName();
        }
        return "UndefLang";
    }

    private String getProblemTitle(ElementId problemId) {
        Problem problem = contest.getProblem(problemId);
        if (problem != null) {
            return problem.toString();
        }
        return "UndefProb";
    }

    @SuppressWarnings("unused")
    private String getSiteTitle(String string) {
        return "Site " + string;
    }
    
    /**
     * Returns list of runs for input problemId.
     * @param runs
     * @param problemElementId
     * @return
     */
    private Run [] getRunsForProblem (Vector<Run> runs, ElementId problemElementId){
        
        Vector <Run> listOfRuns = new Vector<Run>();
        
        for (int i = 0; i < runs.size(); i ++){
            if (runs.elementAt(i).getProblemId().equals(problemElementId)){
                listOfRuns.add(runs.elementAt(i));
            }
        }
        
        return (Run[]) listOfRuns.toArray(new Run[listOfRuns.size()]);
    }

    /**
     * Print run by problem for team.
     * 
     * @param printWriter
     * @param currentTeam
     * @param teamsRuns
     */
    private void printTeamsRuns(PrintWriter printWriter, ClientId currentTeam, Vector<Run> teamsRuns) {

        printWriter.println(currentTeam.getName() + " Site " + currentTeam.getSiteNumber() + " - '" + getClientName(currentTeam) + "'");
        
        long firstSolveTime = 0;
        int solved = 0;
        int attempts = 0;
        int deleteCount = 0;
        
        int totalSolved = 0;
        
        Problem [] problems = contest.getProblems();
        
        for (Problem problem : problems){
            
            Run [] runs = getRunsForProblem(teamsRuns, problem.getElementId());
            
            solved = 0;
            attempts = 0;
            firstSolveTime = 0;
            deleteCount = 0;
            
            printWriter.println("   Problem " + getProblemTitle(problem.getElementId()));

            for (Run run : runs){
                printWriter.format("     %3d %3d", run.getNumber(), run.getElapsedMins());
                if (run.isDeleted()){
                    printWriter.print(" DELETED");
                    deleteCount++;
                } 
                printWriter.print(" " +run.getStatus());
                attempts++;
                if (run.isJudged()) {

                    JudgementRecord judgementRecord = run.getJudgementRecord();
                    Judgement judgement = contest.getJudgement(judgementRecord.getJudgementId());
                    printWriter.print(" " + judgement.toString().substring(0, 3));

                    if (run.isSolved() && (! run.isDeleted())) {
                        
                        if (firstSolveTime == 0){
                            firstSolveTime = run.getElapsedMins();
                            totalSolved++;
                        }
                        solved++;
                    }

                }
                printWriter.print(" " + getLanguageTitle(run.getLanguageId()));
                printWriter.println();
            }
            
            if (solved > 0 || attempts > 0) {
                printWriter.print("             " + attempts + " submissions. ");
                if (deleteCount > 0) {
                    printWriter.print(" "+deleteCount+" deleted");
                }
                if (solved > 0) {
                    printWriter.print(" SOLVED @ "+firstSolveTime+" mins.");
                } else {
                    printWriter.print(" NOT solved");
                }
                printWriter.println();
            } else {
                printWriter.println("             No submissions.");
            }
            
            printWriter.println();
        }
    }

    private void printHeader(PrintWriter printWriter) {
        printWriter.println(new VersionInfo().getSystemName());
        printWriter.println("Date: " + new Date());
        printWriter.println(new VersionInfo().getSystemVersionInfo());
        printWriter.println();
        printWriter.println(getReportTitle());
        
        if (reportFilter != null){
            String filterInfo = reportFilter.toString();
            if (! filterInfo.equals("")){
                printWriter.println("Filter: " + filterInfo);
            }
        }
    }

    private void printFooter(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("end report");
    }

    public void createReportFile(String filename, Filter filter) throws IOException {

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(filename, false), true);

        try {
            printHeader(printWriter);

            try {
                writeReport(printWriter);
            } catch (Exception e) {
                printWriter.println("Exception in report: " + e.getMessage());
                e.printStackTrace(printWriter);
            }

            printFooter(printWriter);

            printWriter.close();
            printWriter = null;

        } catch (Exception e) {
            log.log(Log.INFO, "Exception writing report", e);
            printWriter.println("Exception generating report " + e.getMessage());
        }
    }

    public String[] createReport(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String createReportXML(Filter filter) {
        throw new SecurityException("Not implemented");
    }

    public String getReportTitle() {
        return "Runs grouped by team Report";
    }

    public void setContestAndController(IContest inContest, IController inController) {
        this.contest = inContest;
        this.controller = inController;
        log = controller.getLog();
    }

    public String getPluginTitle() {
        return "Runs grouped by team Report";
    }
}
