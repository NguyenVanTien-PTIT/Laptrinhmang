
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
        Set<Integer> set = new HashSet<>();
        Random generator = new Random();
        List<Integer> a = new ArrayList<>();
        int size = 0;
        while(true){
            Integer value = generator.nextInt(25) + 1;
            set.add(value);
            if(set.size()>size) {
                a.add(value);
                size = set.size();
            }
            if(set.size()==25) break;
            
        }
        for (Integer integer : a) {
            System.out.print(integer+" ");
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
