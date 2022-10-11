package com.example;

import cn.hutool.core.util.ArrayUtil;
import org.junit.jupiter.api.Test;

import java.util.*;


public class ForumServerApplicationTest {


    @Test
    public String longestCommonPrefix(String[] strs) {
        int x =0;
        char p = strs[0].charAt(x);
        for(int i=1;i<strs.length;i++){
            if (x == strs[i].length()){
                break;
            }
            if(strs[i].charAt(x)==p){
                if(i == strs.length-1){
                    x++;i=0;
                    if (x == strs[i].length()){
                        break;
                    }
                    p = strs[0].charAt(x);
                }
            }else{
                break;
            }
        }
        return strs[0].substring(0,x);
    }

    @Test
    void test2(){

        List list = new ArrayList();
        list.add(1);
        List list1 = list;
        list1.add(2);
        list.add(3);
        System.out.println(list);
        System.out.println(list1);
    }

    @Test
    void test3(int[] nums){

        int cur = 0;
        for(int i=0;i<nums.length;i++){
            if(nums[cur] != nums[i]){
                nums[cur+1] = nums[i];
                cur++;
            }
        }

    }

    @Test
    void test4(){
        int[] nums = {-3,-1,0,0,0,3,3};
        test3(nums);

    }

    @Test
    int removeElement(int[] nums, int val) {
        // 快慢指针
        int fastIndex = 0;
        int slowIndex;
        for (slowIndex = 0; fastIndex < nums.length; fastIndex++) {
            if (nums[fastIndex] != val) {
                nums[slowIndex] = nums[fastIndex];
                slowIndex++;
            }
        }
        return slowIndex;
    }
}
