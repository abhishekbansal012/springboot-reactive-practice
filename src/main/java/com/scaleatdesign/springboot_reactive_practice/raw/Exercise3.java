package com.scaleatdesign.springboot_reactive_practice.raw;

import com.scaleatdesign.springboot_reactive_practice.utility.ReactiveSources;

import java.io.IOException;
import java.util.List;

public class Exercise3 {

    public static void main(String[] args) throws IOException {

        // Use ReactiveSources.intNumbersFlux()

        // Get all numbers in the ReactiveSources.intNumbersFlux stream
        // into a List and print the list and its size
        List<Integer> numbers = ReactiveSources.intNumbersFlux().toStream().toList();
        System.out.println("List is : "+numbers);
        System.out.println("Size is : "+numbers.size());
    }

}