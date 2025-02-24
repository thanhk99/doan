package com.example.doan.Controller;
import java.util.*;
public class gameController {
    public int result() {
        Random random = new Random();
        int number = random.nextInt(6);
        return number;
    }
}
