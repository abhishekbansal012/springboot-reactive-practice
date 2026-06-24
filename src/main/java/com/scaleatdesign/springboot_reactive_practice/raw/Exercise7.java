package com.scaleatdesign.springboot_reactive_practice.raw;

import com.scaleatdesign.springboot_reactive_practice.utility.ReactiveSources;

import java.io.IOException;

import java.time.Duration;

public class Exercise7 {


    public static void main(String[] args) throws IOException {

        // Use ReactiveSources.intNumberMono() and ReactiveSources.userMono()

        // Print all values from intNumbersFlux that's greater than 5
        /*ReactiveSources.intNumbersFlux()
                .filter(element -> element >5)
                        .subscribe(System.out::println);*/

        // Print 10 times each value from intNumbersFlux that's greater than 5
        /*ReactiveSources.intNumbersFlux()
                .filter(element -> element >5)
                .map(e -> e*10)
                .subscribe(System.out::println);*/

        // Print 10 times each value from intNumbersFlux for the first 3 numbers emitted that's greater than 5
        ReactiveSources.intNumbersFlux()
                .filter(element -> element >5)
                .map(e -> e*10)
                .take(3)
                .subscribe(System.out::println);

        // Print each value from intNumbersFlux that's greater than 20. Print -1 if no elements are found
        ReactiveSources.intNumbersFlux()
                .filter(element -> element >20)
                        .defaultIfEmpty(-1)
                .subscribe(System.out::println);

        // Switch ints from intNumbersFlux to the right user from userFlux
        // TODO: Write code here

        // Print only distinct numbers from intNumbersFluxWithRepeat
        // TODO: Write code here

        // Print from intNumbersFluxWithRepeat excluding immediately repeating numbers
        // TODO: Write code here

        System.out.println("Press a key to end");
        System.in.read();
    }

}