package DAOs;


import DTOs.User;
import Exceptions.DaoException;

import java.util.List;

public interface UserDaoInterface
{
    public List<User> findAllUsers() throws DaoException;

    public User findUserByUsernamePassword(String username, String password) throws DaoException;

}
