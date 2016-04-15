package com.afgour.repository;

import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Repository
public class HandsRepository {

    private Set<String> handList = Collections.synchronizedSet(new HashSet<>());

    public HandsRepository() {

    }

    public void addHand(String username) {
        handList.add(username);
    }

    public void removeHande(String username) {
        handList.remove(username);
    }

    public boolean isEmpty() {
        return handList.isEmpty();
    }

    public String retrieveRandomlyOneHand() {
        Random generator = new Random();
        String user = (String) handList.toArray()[generator.nextInt(handList.size())];
        handList.remove(user);
        return user;
    }
}
