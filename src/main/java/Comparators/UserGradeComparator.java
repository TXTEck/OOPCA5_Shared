package Comparators;

import DTOs.User;

import java.util.Comparator;

public class UserGradeComparator implements Comparator<User> {
    @Override
    public int compare(User user1, User user2) {
        // Compare users based on their grades in descending order
        return Float.compare(user2.getGrade(), user1.getGrade());
    }
}
