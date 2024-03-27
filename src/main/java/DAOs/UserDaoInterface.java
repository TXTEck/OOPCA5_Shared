package DAOs;

import DTOs.User;
import Exceptions.DaoException;

import java.util.Comparator;
import java.util.List;

public interface UserDaoInterface {
    List<User> findAllUsers() throws DaoException;

    User findUserByStudentId(int studentId) throws DaoException;

    boolean deleteUserByStudentId(int studentId) throws DaoException;

    User insertUser(User user) throws DaoException;

    void updateUserByStudentId(int studentId, User user) throws DaoException;

    List<User> findUsersUsingFilter(Comparator<User> comparator) throws DaoException;

    String findUserJsonByStudentId(int studentId) throws DaoException;
}
