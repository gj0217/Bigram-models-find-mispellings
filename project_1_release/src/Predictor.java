import java.io.*;
import java.util.*;

public class Predictor {

    public static Integer[][] Sort(Integer[][] matrix) {

        Arrays.sort(matrix,new Comparator<Integer[]>() {
            @Override
            public int compare(Integer[] x, Integer[] y) {
                if(x[0] < y[0]){
                    return -1;
                } else if(x[0] > y[0]){
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return matrix;
    }

    public static void main(String[] args) throws IOException {
        long starTime=System.currentTimeMillis();
        //Read train_tokens.txt to arrayList tfList.
        ArrayList<String> tfList = new ArrayList<>();
        FileReader fr = new FileReader("./data/test_tokens_fake.txt");
        BufferedReader br = new BufferedReader(fr);
        int i =0;

        String line ;
        while ((line = br.readLine()) != null) {
            tfList.add(line);
            i++;
        }

        //Read all_confusing.txt to map cwMap.
        FileReader fr1 = new FileReader("./data/all_confusingWords.txt");
        BufferedReader br1 =new BufferedReader(fr1);
        Map<String, String> cwMap = new HashMap<>();
        String[] cwArray;
        String line1;

        while ((line1 = br1.readLine()) != null) {
            cwArray = line1.split(":");
            cwMap.put(cwArray[0],cwArray[1]);
        }

        //Read all training bigrams and their c.
        FileReader fr2 = new FileReader("./results/bigrams.txt");
        BufferedReader br2 =new BufferedReader(fr2);

        String[] bigramArray;
        Map<String, String> bigramMap = new HashMap<>();
        ArrayList<String> hasLeftList = new ArrayList<>();
        ArrayList<String> hasRightList = new ArrayList<>();
        String str;
        int line2=0;
        while ((str = br2.readLine()) != null) {
            line2++;
            if(line2==1) continue;
            bigramArray = str.split(" ");
            bigramMap.put(bigramArray[0]+" "+bigramArray[1],bigramArray[2]);
        }

        //find all confusing bigrams.
        //choiceArray是confusing words换成另一个单词后的bigrams
        //hasLeftList是包含左列单词的bigrams
        //hasRightList是包含右列单词的bigrams
        int sentence=-1;
        int word=0;
        ArrayList<Integer> numberS = new ArrayList<>();
        ArrayList<Integer> numberW = new ArrayList<>();

        for (String s1:cwMap.keySet()){

            for(i=0;i<tfList.size();i++){
                if (tfList.get(i).equals("<s>")) {
                    sentence++;
                    word = 0;
                } else word++;
                if (s1.equals(tfList.get(i))){
                    hasLeftList.add(tfList.get(i-1)+" "+tfList.get(i));
                    numberS.add(sentence);
                    numberW.add(word);
                }
            }
            sentence=-1;
        }

        for (String s2:cwMap.keySet()){
            for(i=0;i<tfList.size();i++){
                if (tfList.get(i).equals("<s>")) {
                    sentence++;
                    word = 0;
                } else word++;
                if (cwMap.get(s2).equals(tfList.get(i))){
                    hasRightList.add(tfList.get(i-1)+" "+tfList.get(i));
                    numberS.add(sentence);
                    numberW.add(word);
                }
            }
            sentence=-1;
        }

        ArrayList<String> choiceList = new ArrayList<>();
        for (String s3:cwMap.keySet()){
            for(i=0;i<tfList.size();i++){
                if (s3.equals(tfList.get(i))){
                    if (cwMap.get(tfList.get(i))!=null){
                        choiceList.add(tfList.get(i-1)+" "+cwMap.get(tfList.get(i)));
                    }else choiceList.add(tfList.get(i-1)+" "+s3);

                }
            }
        }
        for (String s4:cwMap.keySet()){
            for(i=0;i<tfList.size();i++){
                if (cwMap.get(s4).equals(tfList.get(i))){
                    if (cwMap.get(tfList.get(i))!=null){
                        choiceList.add(tfList.get(i-1)+" "+cwMap.get(tfList.get(i)));
                    }else choiceList.add(tfList.get(i-1)+" "+s4);

                }
            }
        }

        //Export GTTable(c&c*) to cStarMap
        FileReader fr3 = new FileReader("./results/GTTable.txt");
        BufferedReader br3 =new BufferedReader(fr3);
        Map<String, String> cStarMap = new HashMap<>();
        String line3 ;
        String[] cStarArray;
        while ((line3 = br3.readLine()) != null) {
            cStarArray = line3.split(" ");
            cStarMap.put(cStarArray[0],cStarArray[1]);
        }
        //Find c and logc* of each left bigram.

        String[] cLeft = new String[hasLeftList.size()];
        String[] cStarLeft = new String[hasLeftList.size()];
        String[] cRight = new String[hasLeftList.size()];
        String[] cStarRight = new String[hasLeftList.size()];
        for(i=0;i<hasLeftList.size();i++){
            cLeft[i]=bigramMap.get(hasLeftList.get(i));
            cStarLeft[i] = cStarMap.get(cLeft[i]);
            if (cLeft[i]==null){
                cStarLeft[i] = "-3";
            }

        }
        //Find c and logc* of each right bigram.
        for(i=0;i<hasRightList.size();i++){
            cRight[i]=bigramMap.get(hasRightList.get(i));
            cStarRight[i] = cStarMap.get(cRight[i]);
            if (cRight[i]==null){
                cStarRight[i] = "-3";
            }
        }

        //Find c and logc* of 换词后的bigram
        String[] cChoice = new String[hasLeftList.size()+hasRightList.size()];
        String[] cStarChoice = new String[hasLeftList.size()+hasRightList.size()];
        for(i=0;i<choiceList.size();i++){
            cChoice[i]=bigramMap.get(choiceList.get(i));
            cStarChoice[i] = cStarMap.get(cChoice[i]);
            if (cChoice[i]==null){
                cStarChoice[i] = "-3";
            }
        }

        //Find logc* of training bigrams and confusing bigrams
        String[][] originalArray = new String[hasLeftList.size()+hasRightList.size()][2];
        int k;
        for(i=0;i<hasLeftList.size();i++){
            originalArray[i][0]=hasLeftList.get(i);
            originalArray[i][1]=cStarLeft[i];
        }
        for(k=i;k<hasRightList.size()+hasLeftList.size();k++){
            originalArray[k][0]=hasRightList.get(k-i);
            originalArray[k][1]=cStarRight[k-i];
        }

        Map<String, String> replacedMap = new HashMap<>();
        for(i=0;i<hasLeftList.size()+hasRightList.size();i++){
            replacedMap.put(choiceList.get(i),cStarChoice[i]);
        }


        int m=0;
        for(i=0;i<hasLeftList.size()+hasRightList.size();i++){
            if(Double.parseDouble(originalArray[i][1])<Double.parseDouble(replacedMap.get(choiceList.get(i)))){
                m++;
            }
        }

        Integer[][] numArray=new Integer[m][2];
        m=0;
        for(i=0;i<hasLeftList.size()+hasRightList.size();i++){
            if(Double.parseDouble(originalArray[i][1])<Double.parseDouble(replacedMap.get(choiceList.get(i)))){
                numArray[m][0] = numberS.get(i);
                numArray[m][1] = numberW.get(i);
                m++;
            }
        }

        numArray=Sort(numArray);

        FileWriter fw = new FileWriter("./results/test_predictions.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        for(i=0;i<m-1;i++){
            if(!numArray[i][0].equals(numArray[i+1][0])){
                bw.write(numArray[i][0]+":"+numArray[i][1]+","+"\n");
                bw.flush();
            }else{
                bw.write(numArray[i][0]+":"+numArray[i][1]+","+numArray[i+1][1]+"\n");
                i++;
                bw.flush();
            }

        }
        bw.write(numArray[m-1][0]+":"+numArray[m-1][1]+",");
        bw.flush();
        long endTime=System.currentTimeMillis();
        long Time=endTime-starTime;
        System.out.println(Time);

    }

}