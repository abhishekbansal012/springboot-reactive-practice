package com.scaleatdesign.springboot_reactive_practice.raw;

import com.scaleatdesign.springboot_reactive_practice.utility.ReactiveSources;

import java.io.IOException;
import java.util.List;

import java.io.IOException;
import java.util.Optional;

public class Exercise4 {

    public static void main(String[] args) throws IOException {

        // Use ReactiveSources.intNumberMono()

        // Print the value from intNumberMono when it emits
        ReactiveSources.intNumberMono().subscribe(System.out::println);

        // Get the value from the Mono into an integer variable
        Optional<Integer> number = ReactiveSources.intNumberMono().blockOptional();
        number.ifPresent(System.out::println);

        System.out.println("Press a key to end");
        System.in.read();
    }

}