package upm.sdamaso.testsmell;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

/**
 * This class is utilized to write output to a CSV file
 */
public class ResultsWriter {

    private String outputFile;
    private FileWriter writer;

    /**
     * Creates the file into which output it to be written into. Results from each
     * file will be stored in a new file
     * 
     * @throws IOException
     */
    private ResultsWriter() throws IOException {
        String time = String.valueOf(Calendar.getInstance().getTimeInMillis());
        outputFile = MessageFormat.format("{0}_{1}_{2}.{3}", "Output", "TestSmellDetection", time, "html");
        writer = new FileWriter(outputFile, false);
    }

    /**
     * Factory method that provides a new instance of the ResultsWriter
     * 
     * @return new ResultsWriter instance
     * @throws IOException
     */
    public static ResultsWriter createResultsWriter() throws IOException {
        return new ResultsWriter();
    }

    /**
     * Writes column names into the CSV file
     * 
     * @param columnNames the column names
     * @throws IOException
     */
    public void writeColumnName(List<String> columnNames) throws IOException {
        writeOutput(columnNames);
    }

    /**
     * Writes column values into the CSV file
     * 
     * @param columnValues the column values
     * @throws IOException
     */
    public void writeLine(List<String> columnValues) throws IOException {
        writeOutput(columnValues);
    }

    /**
     * Appends the input values into the CSV file
     * 
     * @param dataValues the data that needs to be written into the file
     * @throws IOException
     */
    private void writeOutput(List<String> dataValues) throws IOException {
        writer = new FileWriter(outputFile, true);

        for (int i = 0; i < dataValues.size(); i++) {
            writer.append("<td style=\"border: 1px solid black; padding: 5px;\">");
            writer.append(String.valueOf(dataValues.get(i)));
            writer.append("</td>");
        }
        writer.flush();
        writer.close();
    }

    public void writeDocType() throws IOException {
        writer = new FileWriter(outputFile, true);
        writer.append("<!DOCTYPE html>\n" +
                "   <html>\n" + //
                "    <head>\n" + //
                "        <title>TEST SMELLS</title>\n" + //
                "    </head>\n" + //
                "    <body>\n" + //
                "    <h1>TEST SMELLS DEL PROYECTO</h1>\n" + //
                "        <table style=\"border-collapse: collapse;\">\n" + //
                "            \n" + //
                "            <tr>");
        writer.flush();
        writer.close();
    }

    public void writeTrMiddle() throws IOException {
        writer = new FileWriter(outputFile, true);
        writer.append("</tr>\n" + //
                "            \n" + //
                "            <tr>");
        writer.flush();
        writer.close();
    }

    public void writeTrEnd() throws IOException {
        writer = new FileWriter(outputFile, true);
        writer.append("</tr>\n" + //
                "        </table>\n" + //
                "      <h4 style =\"padding-top: 50px\">Report created by Samuel Dámaso</h4>\n" + //
                "    </body>\n" + //
                "   </html>");
        writer.flush();
        writer.close();
    }
}
