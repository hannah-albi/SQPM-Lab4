package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main(String[] args){

        /* Creating an array to store the names of the files */
        String[] filenames = {"model_1.csv", "model_2.csv", "model_3.csv"};

        /* Creating the variables to store the best model for each metric */
        double bestMSE = Double.MAX_VALUE;
        double bestMARE = Double.MAX_VALUE;
        double bestMAE = Double.MAX_VALUE;

        /* Creating variables to store the file names of the best model */
        String bestMSEFilename = "";
        String bestMAEFilename = "";
        String bestMAREFilename = "";

        for(int i = 0; i < filenames.length; i++){
            String file = filenames[i];
            FileReader filereader;
            List<String[]> allData;

            try{
                filereader = new FileReader(file); 
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
                allData = csvReader.readAll();
            }
            catch(Exception e){
                System.out.println("Error reading the CSV file");
                continue;
            }

            /* Creating the variables for the sums of each metric */
            double squaredErrorSum = 0.0;
            double absoluteErrorSum = 0.0;
            double relativeErrorSum = 0.0;

            int count = 0;

            for(int j = 0; j < allData.size(); j++){
                String[] row = allData.get(j);

                double y_true = Double.parseDouble(row[0]);
                double y_predicted = Double.parseDouble(row[1]);

                double error = y_true - y_predicted;
                double absoluteError = Math.abs(error);

                // Calculating the values
                squaredErrorSum += error * error;
                absoluteErrorSum += absoluteError;
                relativeErrorSum += absoluteError / Math.abs(y_true);

                count++;
            }

            // Calculating the metrics 
            double mse = squaredErrorSum / count;
            double mae = absoluteErrorSum / count;
            double mare = relativeErrorSum / count; 

            // Printing the results 
            System.out.println("for " + file);
            System.out.println("\tMSE = " + mse);
            System.out.println("\tMAE = " + mae);
            System.out.println("\tMARE = " + mare);

            /* Finding the best model */
            if(mse < bestMSE){
                bestMSE = mse;
                bestMSEFilename = file;
            }

            if(mae < bestMAE){
                bestMAE = mae;
                bestMAEFilename = file;
            }

            if(mare < bestMARE){
                bestMARE = mare;
                bestMAREFilename = file;
            }
        }

        // Printing the best models
        System.out.println("According to MSE, the best model is " + bestMSEFilename);
        System.out.println("According to MAE, the best model is " + bestMAEFilename);
        System.out.println("According to MARE, the best model is " + bestMAREFilename);
    }
}
