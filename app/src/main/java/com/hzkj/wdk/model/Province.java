package com.hzkj.wdk.model;

import java.util.List;

/**
 * Created by chenweiqi on 2017/5/10.
 */

public class Province {

    public String name;
    public List<City> city;

    public class City{
        public String name;
        public List<String> area;


    }
}
