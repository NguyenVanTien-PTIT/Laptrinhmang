
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class test {
    public static void main(String[] args) {
        String text = "00:00:00:88";
        String text2 = "00:00:00:86";
        String[] arStr = text.split("\\:");
        for(int i=0;i<4;i++){
            System.out.println(Integer.parseInt(arStr[i]));
        }
        String[] arStr2 = text2.split("\\:");
        for(int i=0;i<4;i++){
            System.out.println(Integer.parseInt(arStr2[i]));
        }
        for (int i=0;i<4;i++) {
            if(Integer.parseInt(arStr[i])> Integer.parseInt(arStr2[i])){
                System.out.printf("1 lon hon"+i);
                break;
            }else{
                if(Integer.parseInt(arStr[i])<Integer.parseInt(arStr2[i])){
                    System.out.printf("2 lon hon"+i);
                    break;
                }
            }
	}
    }
//    private void shuffleArray(int[] array)
//    {
//        int index;
//        Random random = new Random();
//        for (int i = array.length - 1; i > 0; i--)
//        {
//            index = random.nextInt(i + 1);
//            if (index != i)
//            {
//                array[index] ^= array[i];
//                array[i] ^= array[index];
//                array[index] ^= array[i];
//            }
//        }
//        for (int i = array.length - 1; i > 0; i--)
//        {
//            System.out.println(array[i]);
//        }
//    }
//    public static void main(String[] arg){
//        test t=new test();
//        int a[]={1,2,3,4,5,6,7,8};
//        t.shuffleArray(a);
//    }
}
