import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        int vecSize;
        int failures = 0;
        double errorLimit = 0.01;
        double accuracy = 0;

        double alpha = Double.parseDouble(args[0]);
        int iterations = Integer.parseInt(args[1]);
        File testSet = new File(args[3]);
        File trainSet = new File(args[2]);

        HashMap<String, List<List<Double>>> trainMap = new HashMap<>();
        HashMap<String, List<List<Double>>> testMap = new HashMap<>();

        try (
                BufferedReader trainSetReader = new BufferedReader(new FileReader(trainSet.getAbsolutePath()));
                BufferedReader testSetReader = new BufferedReader(new FileReader(testSet.getAbsolutePath()));
        ){
            while (trainSetReader.ready())
                addToMap(trainMap, trainSetReader.readLine().split(","));
            while (testSetReader.ready())
                addToMap(testMap, testSetReader.readLine().split(","));
        } catch (IOException e) {
            e.printStackTrace();
        }

        vecSize = testMap.get(testMap.entrySet().iterator().next().getKey()).get(0).size();

        Perceptron perceptron = new Perceptron(alpha, vecSize);

        int i = 0;
        double errorFreq;

        do {
            int size = 0;
            int trainingFailures = 0;
            for (Map.Entry<String, List<List<Double>>> trainEntry : trainMap.entrySet()) {
                for (List<Double> trainVec : trainEntry.getValue()) {
                    perceptron.setInput(trainVec);
                    perceptron.setRawExpectedOutput(trainEntry.getKey());
                    perceptron.train();

                    trainingFailures+=Math.pow(perceptron.getOutput()-perceptron.getExpectedOutput(), 2);
                    size++;
                }
            }
            errorFreq = (double)trainingFailures/size;
//            System.out.println(trainingFailures + "/" + size + " = " + errorFreq);

        } while (i++ < iterations && errorFreq > errorLimit);
        // && errorFreq > errorLimit

        int testSize = 0;
        for (Map.Entry<String, List<List<Double>>> testEntry : testMap.entrySet()) {
            for (List<Double> testVec : testEntry.getValue()) {
                System.out.println(testVec + " " + testEntry.getKey());
                perceptron.setInput(testVec);
                perceptron.setRawExpectedOutput(testEntry.getKey());
                perceptron.updateOutput();
                System.out.println(perceptron);
                failures+=Math.pow(perceptron.getOutput()-perceptron.getExpectedOutput(), 2);
                System.out.println();
                testSize++;
            }
            System.out.println("==================================");
        }

        System.out.println(perceptron.getWeights());
        System.out.println(perceptron.getTheta());
        System.out.println(failures + "/" + testSize);
        System.out.println("Accuracy: " + (1 - (double) failures / testSize) * 100 + "%");

        Scanner scan = new Scanner(System.in);
        while (true){
            List<Double> insertedList = new ArrayList<>();
            System.out.println("Insert 'q' to exit.");
            int j = 0;
            while (j++ < vecSize){
                System.out.print("Insert feature " + j + ": ");
                String inserted = scan.next();
                if (inserted.equals("q")){
                    return;
                }else{
                    try {
                        insertedList.add(Double.parseDouble(inserted));
                    }catch (NumberFormatException e){
                        j--;
                        System.out.println("Incorrect data inserted.");
                    }
                }
            }
            System.out.print("Insert term: ");
            String term = scan.next();
//            FeatureHolder featureHolder = new FeatureHolder(insertedList, labelAttribute);

            System.out.println(insertedList + " " + term);
            perceptron.setInput(insertedList);
            perceptron.setRawExpectedOutput(term);
            perceptron.updateOutput();
            System.out.println(perceptron);
            failures+=Math.pow(perceptron.getOutput()-perceptron.getExpectedOutput(), 2);
            System.out.println();
            testSize++;

            System.out.println(failures + "/" + testSize + " failures");
            System.out.println("Accuracy: " + (1 - (double) failures / testSize) * 100 + "%");
        }

    }

    public static void addToMap(HashMap<String, List<List<Double>>> map, String[] currVec){
        if (map.get(currVec[currVec.length - 1]) == null) {
            List<List<Double>> list = new ArrayList<>();
            list.add(convertToList(currVec));
            map.put(currVec[currVec.length - 1], list);
        } else {
            map.get(currVec[currVec.length - 1]).add(convertToList(currVec));
        }

    }

    public static List<Double> convertToList(String[] vec){
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < vec.length-1; i++) {
            list.add(Double.parseDouble(vec[i]));
        }
        return list;
    }
}