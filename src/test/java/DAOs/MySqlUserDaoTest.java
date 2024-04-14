package DAOs;

import Server.Comparators.UserGradeComparator;
import Server.DAOs.MySqlUserDao;
import Server.DAOs.UserDaoInterface;
import Server.DTOs.User;
import Server.Exceptions.DaoException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.swing.text.html.parser.Entity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MySqlUserDaoTest {
    private UserDaoInterface userDao;
    private User userToDelete; // Store the user to delete for the test

    private static final int SERVER_PORT = 8080;
    private static final String SERVER_HOST = "localhost";

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader consoleInput;

    /**
     * Main Author: Bianca Valicec
     **/
    @BeforeEach
    public void setup() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        consoleInput = new BufferedReader(new InputStreamReader(System.in));
    }

    @AfterEach
    public void tearDown() throws IOException {
        socket.close();
        out.close();
        in.close();
        consoleInput.close();
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testFindAllUsers() throws DaoException {
        // Act: Call the method to be tested
        List<User> users = userDao.findAllUsers();
        // Assert: Verify the results
        assertNotNull("Returned list of users should not be null", users);
        assertFalse("Returned list of users should not be empty", users.isEmpty());
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void findUserByStudentIdTrue() throws DaoException {
        // Act: Call the method to be tested
        User user = userDao.findUserByStudentId(12345);
        // Assert: Verify the results
        assertNotNull("Returned user should not be null", user);
        assertEquals("Unexpected studentId", 12345, user.getStudentId());
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void findUserByStudentIdFalse() throws DaoException {
        // Act: Call the method to be tested
        User user = userDao.findUserByStudentId(2568);
        // Assert: Verify the results
        assertNull("Returned user should be null", user);
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testDeleteUserByStudentId_ExistingUser() throws DaoException {
        // Act: Call the method to be tested
        userDao.deleteUserByStudentId(12345);
        // Assert: Verify the effects of deletion
        assertNull("User should be deleted if it exists", userDao.findUserByStudentId(12345));
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testDeleteUserByStudentId_NonExistingUser() throws DaoException {
        // Act: Call the method to be tested
        userDao.deleteUserByStudentId(-1);
        // Assert: Verify the effects of deletion
        assertNotNull("User count should remain unchanged if user does not exist", userDao.findUserByStudentId(12345));
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testInsertUser_Success() throws DaoException {
        // Act: Call the method to be tested
        User userToInsert = new User(111, "Amy", "O'Neill", 304, "Math", 95.5f, "Semester 1");
        userDao.insertUser(userToInsert);
        // Assert: Verify the effects of insertion
        assertNotNull("User should be inserted successfully", userDao.findUserByStudentId(111));
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testUpdateUserByStudentId_Success() throws DaoException {
        // Act: Call the method to be tested
        User userToUpdate = new User(123456, "Alice", "Smith", 306, "Biology", 92.0f, "Semester 1");
        userDao.insertUser(userToUpdate);
        String updatedFirstName = "UpdatedFirstName";
        userToUpdate.setFirstName(updatedFirstName);
        userDao.updateUserByStudentId(123456, userToUpdate);
        // Assert: Verify the effects of the update
        assertEquals("First name should be updated", updatedFirstName, userDao.findUserByStudentId(123456).getFirstName());
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testFindUsersUsingFilter_GradeComparator() throws DaoException {
        // Act: Call the method to be tested
        List<User> usersFilteredByGrade = userDao.findUsersUsingFilter(new UserGradeComparator());
        // Assert: Verify the results
        assertNotNull("Filtered user list should not be null", usersFilteredByGrade);
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testConvertListToJson() throws DaoException {
        // Arrange: Prepare test data
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "John", "Doe", 101, "Math", 85, "Semester 1"));
        userList.add(new User(2, "Jane", "Smith", 102, "English", 90, "Semester 2"));

        // Convert the test data to JSON using Gson
        Gson gson = new Gson();
        String expectedJson = gson.toJson(userList);

        // Act: Call the method to be tested
        String json = userDao.usersListToJson(userList);

        // Assert: Verify the results
        assertNotNull("Converted JSON should not be null", json);
        assertEquals("Output JSON should match the expected JSON", expectedJson.replaceAll("\\s", ""), json.replaceAll("\\s", ""));
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Test
    public void testConvertUserToJson() throws DaoException {
        // Arrange: Prepare test data and dependencies
        UserDaoInterface userDao = new MySqlUserDao(); // Instantiate your UserDao implementation
        int studentId = 101; // Provide a valid student ID for an existing user

        // Act: Call the method to be tested
        String json = userDao.findUserJsonByStudentId(studentId);

        // Assert: Verify the results
        assertNotNull("Converted JSON should not be null", json);

        // Verify that the JSON string is not empty
        assertFalse("JSON string should not be empty", json.isEmpty());

        // Verify that the JSON string contains all expected fields and values for the user
        assertTrue("JSON string should contain studentId", json.contains("\"studentId\":" + studentId));
        assertTrue("JSON string should contain firstName", json.contains("\"firstName\":\"Jane\""));
        assertTrue("JSON string should contain lastName", json.contains("\"lastName\":\"Doe\""));
        assertTrue("JSON string should contain courseId", json.contains("\"courseId\":302"));
        assertTrue("JSON string should contain courseName", json.contains("\"courseName\":\"Nursing\""));
        assertTrue("JSON string should contain grade", json.contains("\"grade\":70.2"));
        assertTrue("JSON string should contain semester", json.contains("\"semester\":\"Semester 1\""));

        // Add more specific assertions if needed
    }

    /**
     * Main Author: Bianca Valicec
     * @throws IOException
     */
    @Test
    public void testDisplayEntityById_EntityExists() throws IOException {
        // Sending command for Display Entity by ID
        out.println(9);
        // Sending entity ID to display
        out.println(101);
        // Expecting a JSON response containing entity details
        String jsonResponse = in.readLine();
        assertEquals("{\"id\":101,\"studentId\":101,\"firstName\":\"John\",\"lastName\":\"Doe\",\"courseId\":1,\"courseName\":\"Mathematics\",\"grade\":85.5,\"semester\":\"Spring\"}", jsonResponse);
    }

    /**
     * Main Author: Bianca Valicec
     * @throws IOException
     */
    @Test
    public void testDisplayEntityById_EntityNotExists() throws IOException {
        // Sending command for Display Entity by ID
        out.println(9);
        // Sending a non-existent entity ID to display
        out.println(999);
        // Expecting a response indicating that the entity was not found
        String response = in.readLine();
        assertEquals("Entity not found.", response);
    }


    @Test
    public void displayAllEntitys() throws IOException {
        // Sending command for Display all Entitys
        out.println(10);

        String jsonResponseFromServer = in.readLine();

        Gson gson = new Gson();

        List<User> entitys = gson.fromJson(jsonResponseFromServer, new TypeToken<List<User>>(){}.getType());

        assertNotNull("Entitys should not be null", entitys);
        assertFalse("Entitys should not be empty",  entitys.isEmpty());
    }

}