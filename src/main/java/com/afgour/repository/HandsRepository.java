package com.afgour.repository;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HandsRepository {

    private Set<String> handList = Collections.synchronizedSet(new HashSet<>());

    public HandsRepository() {

    }

    public void addHand(String username) {
        handList.add(username);
    }

    public boolean isEmpty() {
        return handList.isEmpty();
    }

    public Optional<String> retrieveRandomlyOneHand(String username) {
        Optional<String> anyUser = handList.stream().filter(user -> !user.equals(username)).findAny();
        anyUser.ifPresent(user->handList.remove(user));
        return anyUser;
    }

    public void removeHand(String username) {
        handList.remove(username);
    }
}
