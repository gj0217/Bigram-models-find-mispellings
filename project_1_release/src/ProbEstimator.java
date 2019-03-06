import javax.sound.sampled.Line;
import java.io.*;
import java.util.*;
import org.apache.commons.math3.stat.regression.SimpleRegression;


public class ProbEstimator {


    public static void main(String[] args) throws IOException {
        long starTime=System.currentTimeMillis();

        //Put tokens into an ArrayList.
        FileReader fr = new FileReader("./data/train_tokens.txt");
        BufferedReader br = new BufferedReader(fr);
        ArrayList<String> tokens = new ArrayList<>();
        String line ;
        while ((line = br.readLine()) != null) {
            tokens.add(line);
        }

        //Delete duplicate words.
        ArrayList<String> unigrams = new ArrayList<>();
        for (String s : tokens) {
            if (!unigrams.contains(s)) {
                unigrams.add(s);
            }
        }

        //Put bigrams in an array.
        ArrayList<String> bigrams = new ArrayList<>();
        for (int i = 0; i < tokens.size()-1; i++) {
            if (!(tokens.get(i) + " " + tokens.get(i + 1)).equals("</s> <s>")){
                bigrams.add(tokens.get(i) + " " + tokens.get(i + 1));
            }

        }

        StringSameCount ssc = new StringSameCount();
        for (int i = 0; i < bigrams.size(); i++) {
            ssc.hashInsert(bigrams.get(i));
        }
        HashMap map = ssc.getHashMap();
        Iterator it2 = map.keySet().iterator();
        Iterator it3 = map.keySet().iterator();
        String temp;
        String temp2;

        int num = 0;
        int i=0;
        FileWriter fw = new FileWriter("./results/bigrams.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        while (it2.hasNext()) {
            num++;
            temp = (String) it2.next();
            String useless=(temp+" "+map.get(temp));

        }
        String[] bigramTemp = new String[num];
        bw.write(num + "\n");
        while (it3.hasNext()) {

            temp2 = (String) it3.next();
            bw.write(temp2 + " " + map.get(temp2) + "\n");
            bigramTemp[i]=(temp2+" "+map.get(temp2));
            bw.flush();
            i++;
        }
        bw.close();
        String[][] matrix= new String[num][3];

        for(int j=0;j<num;j++){
            String[] parts = bigramTemp[j].split(" ");
            matrix[j][0] = parts[0];
            matrix[j][1] = parts[1];
            matrix[j][2] = parts[2];
        }

        for(int j=0;j<num;j++){
            String[] parts = bigramTemp[j].split(" ");

        }
//        int row = 0;
//        int column = 0;
//        int key1,key2;
//        int[][] numMatrix = new int[unigrams.size()][unigrams.size()];

        Map<String, Integer> m = new HashMap<>();
        int key[] = new int[unigrams.size()];
        for(i=0;i<unigrams.size();i++){
            key[i] = i;
            m.put(unigrams.get(i),key[i]);

        }

//        for(i=0;i<bigramTemp.length;i++){
//
//            key1=m.get(matrix[i][0]);
//            key2=m.get(matrix[i][1]);
//            numMatrix[key1][key2]=Integer.parseInt(matrix[i][2]);
//        }

//        System.out.println(tokens.size());
//        System.out.println(unigrams.size());
//        System.out.println(bigrams.length);
//        System.out.println(bigramTemp.length);

        //Output ff.txt
        StringSameCount ssc2 = new StringSameCount();
        int aaa=0;
        for ( i = 0; i < num ; i++) {
            ssc2.hashInsert(matrix[i][2]);

        }

        String temp4;

        HashMap map2 = ssc2.getHashMap();
        for(int s=0;i<map2.size();i++){
            if(map2.containsKey(s)){
            aaa=aaa+(int)map2.get("i");
            }
        }
        map2.put("0",unigrams.size()*unigrams.size()-aaa);
        Iterator it4 = map2.keySet().iterator();

        FileWriter fw4 = new FileWriter("./results/ff.txt");
        BufferedWriter bw4 = new BufferedWriter(fw4);

        int j=0;
        int c,nc;

        while (it4.hasNext()) {
            temp4 = (String) it4.next();
            c = Integer.parseInt(temp4);
            nc = Integer.parseInt((map2.get(temp4)).toString());
            j++;
            bw4.write(Math.log(c)+" "+Math.log(nc)+"\n");
            bw4.flush();
        }
        int k=0;
        StringSameCount ssc3 = new StringSameCount();
        for ( i = 0; i < num ; i++) {
            ssc3.hashInsert(matrix[i][2]);
        }
        int[][] arrayLog = new int[j][2];
        float[][] data1 = new float[j][2];
        String temp5;
        HashMap map3 = ssc3.getHashMap();
        Iterator it5 = map3.keySet().iterator();
        while (it5.hasNext()) {
            temp5 = (String) it5.next();
            arrayLog[k][0]=Integer.parseInt(temp5);
            arrayLog[k][1]=Integer.parseInt((map3.get(temp5)).toString());
            data1[k][0]=(float)Math.log(arrayLog[k][0]);
            data1[k][1]=(float)Math.log(arrayLog[k][1]);
            k++;
        }
        bw4.close();
        //GT smoothing

        SimpleRegression regression = new SimpleRegression();
        for(i=0;i<j;i++){
            regression.addData((double)data1[i][0],(double)data1[i][1]);
        }

        TreeMap<Double, Double> mapLog = new TreeMap<>();
        for(i=0;i<j;i++) {
            mapLog.put(Math.log(arrayLog[i][0]), Math.log(arrayLog[i][1]));
        }

        Double maxKey = mapLog.lastKey();
        int maxValue = (int)Math.exp(maxKey);
        //System.out.println(maxValue);
        for(i=0;i<maxValue+2;i++){
            if (!mapLog.keySet().contains(Math.log(i))) {
                mapLog.put(Math.log(i) , regression.predict(Math.log(i)));
            }
        }

        TreeMap<Integer, Double> mapGT = new TreeMap<>();
        double cStar;
        double ncNextLinear;
        double ncLinear;

        FileWriter fw5 = new FileWriter("./results/GTTable.txt");
        BufferedWriter bw5 = new BufferedWriter(fw5);

        for(i=0;i<mapLog.size()-1;i++) {
            if(i==0){
                c=0;
                cStar=Math.exp(mapLog.get(Math.log(1)))/bigrams.size();
                mapGT.put(c, Math.log(cStar));
                bw5.write(c+" "+Math.log(cStar)+"\n");
                bw5.flush();
            }else{
            c=i;
            ncNextLinear = Math.exp(mapLog.get(Math.log((c+1))));
            ncLinear = Math.exp(mapLog.get(Math.log(c)));
            cStar= (c+1)*ncNextLinear/ncLinear;
            mapGT.put(c, Math.log(cStar));
            bw5.write(c+" "+Math.log(cStar)+"\n");
            bw5.flush();}
        }
        long endTime=System.currentTimeMillis();
        long Time=endTime-starTime;
        System.out.println(Time);

        //laplacian smoothing
//        for(row=0;row<unigrams.size();++row){
//            for(column=0;column<unigrams.size();++column){
//                if (numMatrix[row][column] == 0){
//                    numMatrix[row][column] = numMatrix[row][column]+1;
//                }
//            }
//        }
    }

}