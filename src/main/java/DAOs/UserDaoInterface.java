package DAOs;

import DTOs.User;
import Exceptions.DaoException;

import java.util.Comparator;
import java.util.List;
public interface UserDaoInterface {
    public List<User> findAllUsers() throws DaoException;

    public User findUserByStudentId(int studentId) throws DaoException;

    boolean deleteUserByStudentId(int studentId) throws DaoException;

    public User insertUser(User user) throws DaoException;

    void updateUserByStudentId(int studentId, User user) throws DaoException;

    public List<User> findUsersUsingFilter(Comparator<User> comparator) throws DaoException;
}
