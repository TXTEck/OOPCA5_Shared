package DAOs;

import DAOs.MySqlDao;
import DAOs.UserDaoInterface;
import DTOs.User;
import Exceptions.DaoException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlUserDao extends MySqlDao implements UserDaoInterface
{
    /**
     * Will access and return a List of all users in User database table
     * @return List of User objects
     * @throws DaoException
     */
    @Override
    public List<User> findAllUsers() throws DaoException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> usersList = new ArrayList<>();

        try
        {
            //Get connection object using the getConnection() method inherited
            // from the super class (MySqlDao.java)
            connection = this.getConnection();

            String query = "SELECT * FROM StudentGrades";
            preparedStatement = connection.prepareStatement(query);

            //Using a PreparedStatement to execute SQL...
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                int userId = resultSet.getInt("id");
                int studentId = resultSet.getInt("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                int courseId = resultSet.getInt("course_id");
                String courseName = resultSet.getString("course_name");
                float grade = resultSet.getFloat("grade");
                String semester = resultSet.getString("semester");
                User u = new User(userId, studentId, firstName, lastName, courseId, courseName, grade, semester);
                usersList.add(u);
            }
        } catch (SQLException e)
        {
            throw new DaoException("findAllUseresultSet() " + e.getMessage());
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
                if (connection != null)
                {
                    freeConnection(connection);
                }
            } catch (SQLException e)
            {
                throw new DaoException("findAllUsers() " + e.getMessage());
            }
        }
        return usersList;     // may be empty
    }

    /**
     * Given a username and password, find the corresponding User
     * @param first_name
     * @param student_grade
     * @return User object if found, or null otherwise
     * @throws DaoException
     */
    @Override
    public User findUserByUsernamePassword(String first_name, String student_grade) throws DaoException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        try
        {
            connection = this.getConnection();

            String query = "SELECT * FROM `StudentGrades` WHERE first_name = ? AND grade = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, first_name);
            preparedStatement.setString(2, student_grade);

            resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                int userId = resultSet.getInt("id");
                int studentId = resultSet.getInt("student_Id");
                String firstName = resultSet.getString("first_name");
                float grade = resultSet.getFloat("grade");
                
                user = new User(userId, studentId, firstName, grade);
            }
        } catch (SQLException e)
        {
            throw new DaoException("findUserByUsernamePassword() " + e.getMessage());
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
                if (connection != null)
                {
                    freeConnection(connection);
                }
            } catch (SQLException e)
            {
                throw new DaoException("findUserByUsernamePassword() " + e.getMessage());
            }
        }
        return user;     // reference to User object, or null value
    }
}