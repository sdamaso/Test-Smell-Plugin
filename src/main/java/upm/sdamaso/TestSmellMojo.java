package upm.sdamaso;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import upm.sdamaso.testsmell.AbstractSmell;
import upm.sdamaso.testsmell.ResultsWriter;
import upm.sdamaso.testsmell.TestFile;
import upm.sdamaso.testsmell.TestSmellDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mojo(name = "testsmell", defaultPhase = LifecyclePhase.TEST)
public class TestSmellMojo extends AbstractMojo {

    @Parameter(property = "csv.input", required = true, defaultValue = "input.csv")
    private String csvInput;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!csvInput.isEmpty()) {
            File inputFile = new File(csvInput);
            if (!inputFile.exists() || inputFile.isDirectory()) {
                System.out.println("Please provide a valid file containing the paths to the collection of test files");
                return;
            }
        }

        TestSmellDetector testSmellDetector = new TestSmellDetector();

        /*
         * Read the input file and build the TestFile objects
         */
        try (BufferedReader in = new BufferedReader(new FileReader(csvInput))) {
            String str;

            String[] lineItem;
            TestFile testFile;
            List<TestFile> testFiles = new ArrayList<>();
            while ((str = in.readLine()) != null) {
                // use comma as separator
                lineItem = str.split(",");

                // check if the test file has an associated production file
                if (lineItem.length == 2) {
                    testFile = new TestFile(lineItem[0], lineItem[1], "");
                } else {
                    testFile = new TestFile(lineItem[0], lineItem[1], lineItem[2]);
                }

                testFiles.add(testFile);
            }

            /*
             * Initialize the output file - Create the output file and add the column names
             */
            ResultsWriter resultsWriter = ResultsWriter.createResultsWriter();
            List<String> columnNames;
            List<String> columnValues;

            resultsWriter.writeDocType();

            columnNames = testSmellDetector.getTestSmellNames();
            columnNames.add(0, "App");
            columnNames.add(1, "TestClass");
            columnNames.add(2, "TestFilePath");
            columnNames.add(3, "ProductionFilePath");
            columnNames.add(4, "RelativeTestFilePath");
            columnNames.add(5, "RelativeProductionFilePath");

            resultsWriter.writeColumnName(columnNames);

            /*
             * Iterate through all test files to detect smells and then write the output
             */
            TestFile tempFile;
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date;
            for (TestFile file : testFiles) {
                date = new Date();
                System.out.println(dateFormat.format(date) + " Processing: " + file.getTestFilePath());
                System.out.println("Processing: " + file.getTestFilePath());

                resultsWriter.writeTrMiddle();

                // detect smells
                tempFile = testSmellDetector.detectSmells(file);

                // write output
                columnValues = new ArrayList<>();
                columnValues.add(file.getApp());
                columnValues.add(file.getTestFileName());
                columnValues.add(file.getTestFilePath());
                columnValues.add(file.getProductionFilePath());
                columnValues.add(file.getRelativeTestFilePath());
                columnValues.add(file.getRelativeProductionFilePath());
                for (AbstractSmell smell : tempFile.getTestSmells()) {
                    try {
                        columnValues.add(String.valueOf(smell.getHasSmell()));
                    } catch (NullPointerException e) {
                        columnValues.add("");
                    }
                }
                resultsWriter.writeLine(columnValues);
            }
            resultsWriter.writeTrEnd();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end");
    }

}
