package com.example.optimalschedule.fpgrowth;

import java.util.Comparator;

public class HeaderComparator implements Comparator<HeaderNode>{

    @Override
    public int compare(HeaderNode h1, HeaderNode h2) {
        if(h1.supportCount > h2.supportCount){
            return -1;
        }else if(h1.supportCount < h2.supportCount){
            return 1;
        }else{
            return 0;
        }
    }

}