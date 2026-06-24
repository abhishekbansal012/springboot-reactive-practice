package com.scaleatdesign.springboot_reactive_practice.raw;

import com.scaleatdesign.springboot_reactive_practice.utility.ReactiveSources;

import java.io.IOException;
import java.util.Objects;

public class Exercise8 {


    public static void main(String[] args) throws IOException {

        // Use ReactiveSources.intNumbersFluxWithException()

        // Print values from intNumbersFluxWithException and print a message when error happens
        /*ReactiveSources.intNumbersFluxWithException()
                .doOnError(e -> System.out.println("Error !!" + e.getMessage()))
                .subscribe(System.out::println);*/

        // Print values from intNumbersFluxWithException and continue on errors
        ReactiveSources.intNumbersFluxWithException()
                .onErrorContinue((e,item) -> System.out.println("Error Occurred !!" + e.getMessage() ))
                .subscribe(System.out::println);

        // Print values from intNumbersFluxWithException and when errors
        // happen, replace with a fallback sequence of -1 and -2
        // TODO: Write code here

        System.out.println("Press a key to end");
        System.in.read();
    }

}