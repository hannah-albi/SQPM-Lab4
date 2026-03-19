package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Multi-Class Classification
 *
 */
public class App 
{
    public static void main(String[] args)
    {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;

        try{
            filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
        }
        catch(Exception e){
            System.out.println("Error reading the CSV file");
            return;
        }

        double ceSum = 0.0;
        int count = 0;

        /* Creating the 5 x 5 confusion matrix */
        int[][] confusionMatrix = new int[5][5];

        for (int rowIndex = 0; rowIndex < allData.size(); rowIndex++) {

            String[] row = allData.get(rowIndex);

            int y_true = Integer.parseInt(row[0]);
            double[] y_predicted = new double[5];

            for(int i = 0; i < 5; i++){
                y_predicted[i] = Double.parseDouble(row[i + 1]);
            }

            /* CE calculation: use the probability of the true class */
            double trueClassProbability = y_predicted[y_true - 1];

            /* Preventing the calculation of log(0) */
            if(trueClassProbability == 0.0){
                trueClassProbability = 0.000001;
            }

            ceSum += -Math.log(trueClassProbability);

            /* Find predicted class: class with maximum probability */
            int predictedClass = 1;
            double maxProbability = y_predicted[0];

            for(int i = 1; i < 5; i++){
                if(y_predicted[i] > maxProbability){
                    maxProbability = y_predicted[i];
                    predictedClass = i + 1;
                }
            }

            /* Update confusion matrix:
               row = predicted class
               column = true class
            */
            confusionMatrix[predictedClass - 1][y_true - 1]++;
            count++;
        }

        double ce = ceSum / count;

        System.out.println("CE =" + ce);
        System.out.println("Confusion matrix");
        System.out.println("\t\ty=1\ty=2\ty=3\ty=4\ty=5");

        for(int i = 0; i < 5; i++){
            System.out.print("\ty^=" + (i + 1));
            for(int j = 0; j < 5; j++){
                System.out.print("\t" + confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }
}
