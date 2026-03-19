package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Binary Classification
 *
 */
public class App
{
    public static void main(String[] args){

        /* Creating an array to store the names of the files */
        String[] filenames = {"model_1.csv", "model_2.csv", "model_3.csv"};

        /* Creating variables to store the best model for each metric */
        double bestBCE = Double.MAX_VALUE;
        double bestAccuracy = -1.0;
        double bestPrecision = -1.0;
        double bestRecall = -1.0;
        double bestF1Score = -1.0;
        double bestAUCROC = -1.0;

        /* Creating variables to store the file names of the best model */
        String bestBCEFilename = "";
        String bestAccuracyFilename = "";
        String bestPrecisionFilename = "";
        String bestRecallFilename = "";
        String bestF1ScoreFilename = "";
        String bestAUCROCFilename = "";

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
                System.out.println("Error reading the CSV file: " + file);
                continue;
            }

            /* Variables for BCE and confusion matrix */
            double bceSum = 0.0;
            int truep = 0;
            int truen = 0;
            int falsep = 0;
            int falsen = 0;

            /* Arrays for AUC-ROC */
            double[] positiveScores = new double[allData.size()];
            double[] negativeScores = new double[allData.size()];

            int posCount = 0;
            int negCount = 0;

            for(int j = 0; j < allData.size(); j++){

                String[] row = allData.get(j);

                int y_true = Integer.parseInt(row[0]);
                double y_predicted = Double.parseDouble(row[1]);

                /* Preventing calculating log(0) in BCE */
                double p = y_predicted;
                if(p == 0.0){
                    p = 0.000001;
                }
                if(p == 1.0){
                    p = 0.999999;
                }

                /* Calculating BCE */
                bceSum += -(y_true * Math.log(p) + (1 - y_true) * Math.log(1 - p));

                /* Storing scores for AUC-ROC */
                if(y_true == 1){
                    positiveScores[posCount] = y_predicted;
                    posCount++;
                }
                else{
                    negativeScores[negCount] = y_predicted;
                    negCount++;
                }

                /* Converting probability into predicted label */
                int predictedClass;
                if(y_predicted >= 0.5){
                    predictedClass = 1;
                }
                else{
                    predictedClass = 0;
                }

                /* Updating confusion matrix */
                if(predictedClass == 1 && y_true == 1){
                    truep++;
                }
                else if(predictedClass == 1 && y_true == 0){
                    falsep++;
                }
                else if(predictedClass == 0 && y_true == 1){
                    falsen++;
                }
                else{
                    truen++;
                }
            }

            /* Calculating the final metrics */
            int total = truep + truen + falsep + falsen;

            double bce = bceSum / allData.size();
            double accuracy = (double)(truep + truen) / total;
            double precision = (double)truep / (truep + falsep);
            double recall = (double)truep / (truep + falsen);
            double f1Score = 2 * precision * recall / (precision + recall);

            /* Calculating AUC-ROC */
            double aucCount = 0.0;

            for(int p = 0; p < posCount; p++){
                for(int n = 0; n < negCount; n++){
                    if(positiveScores[p] > negativeScores[n]){
                        aucCount += 1.0;
                    }
                    else if(positiveScores[p] == negativeScores[n]){
                        aucCount += 0.5;
                    }
                }
            }

            double aucROC = aucCount / (posCount * negCount);

            /* Printing the results */
            System.out.println("for " + file);
            System.out.println("\tBCE =" + bce);
            System.out.println("\tConfusion matrix");
            System.out.println("\t\t\ty=1\t\ty=0");
            System.out.println("\t\ty^=1\t" + truep + "\t\t" + falsep);
            System.out.println("\t\ty^=0\t" + falsen + "\t\t" + truen);
            System.out.println("\tAccuracy =" + accuracy);
            System.out.println("\tPrecision =" + precision);
            System.out.println("\tRecall =" + recall);
            System.out.println("\tf1 score =" + f1Score);
            System.out.println("\tauc roc =" + aucROC);

            /* Finding the best model */
            if(bce < bestBCE){
                bestBCE = bce;
                bestBCEFilename = file;
            }

            if(accuracy > bestAccuracy){
                bestAccuracy = accuracy;
                bestAccuracyFilename = file;
            }

            if(precision > bestPrecision){
                bestPrecision = precision;
                bestPrecisionFilename = file;
            }

            if(recall > bestRecall){
                bestRecall = recall;
                bestRecallFilename = file;
            }

            if(f1Score > bestF1Score){
                bestF1Score = f1Score;
                bestF1ScoreFilename = file;
            }

            if(aucROC > bestAUCROC){
                bestAUCROC = aucROC;
                bestAUCROCFilename = file;
            }
        }

        /* Printing the best models */
        System.out.println("According to BCE, The best model is " + bestBCEFilename);
        System.out.println("According to Accuracy, The best model is " + bestAccuracyFilename);
        System.out.println("According to Precision, The best model is " + bestPrecisionFilename);
        System.out.println("According to Recall, The best model is " + bestRecallFilename);
        System.out.println("According to F1 score, The best model is " + bestF1ScoreFilename);
        System.out.println("According to AUC ROC, The best model is " + bestAUCROCFilename);
    }
}
