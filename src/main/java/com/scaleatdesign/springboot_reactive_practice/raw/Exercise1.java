package com.scaleatdesign.springboot_reactive_practice.raw;

import com.scaleatdesign.springboot_reactive_practice.model.User;
import com.scaleatdesign.springboot_reactive_practice.utility.StreamSources;

public class Exercise1 {

    public static final String SPACE = " ";

    public static void main(String[] args) {
        // Print all numbers in the intNumbersStream stream
        StreamSources.intNumbersStream().forEach(e -> {
            System.out.print(e.toString().concat(SPACE));
        });
        System.out.println();

        // Print numbers from intNumbersStream that are less than 5
        StreamSources.intNumbersStream()
                .filter(number -> number < 5)
                .forEach(e -> {
                    System.out.print(e.toString().concat(SPACE));
                });

        System.out.println();

        // Print the second and third numbers in intNumbersStream that's greater than 5
        StreamSources.intNumbersStream().filter(number -> number > 5)
                .skip(1)
                .limit(2)
                .forEach(e -> {
                    System.out.print(e.toString().concat(SPACE));
                });
        System.out.println();

        //  Print the first number in intNumbersStream that's greater than 5.
        //  If nothing is found, print -1
        StreamSources.intNumbersStream().filter(number -> number > 5)
                .findFirst()
                .ifPresentOrElse(
                        e -> System.out.print(e.toString().concat(SPACE)),
                        () -> System.out.print("-1".concat(SPACE))
                );
        System.out.println();

        // Print first names of all users in userStream
        StreamSources.userStream()
                .map(User::getFirstName)
                .forEach(System.out::println);

        // Print first names in userStream for users that have IDs from number stream
        //Way-1
        StreamSources.userStream()
                .filter(user -> StreamSources.intNumbersStream()
                        .anyMatch(num -> num.equals(user.getId())))
                .forEach(System.out::println);

        //Way-2
        StreamSources.intNumbersStream()
                .flatMap(id -> StreamSources.userStream().filter(user -> user.getId() == id))
                .map(User::getFirstName)
                .forEach(System.out::println);


    }

}
