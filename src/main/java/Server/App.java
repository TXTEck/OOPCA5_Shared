package Server;

import Server.Comparators.UserGradeComparator;
import Server.DAOs.MySqlUserDao;
import Server.DAOs.UserDaoInterface;
import Server.DTOs.User;
import Server.Exceptions.DaoException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Main Author: Bianca Valicec
 **/

public class App {

    final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        App server = new App();
        server.start();
    }

    public void start() {

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started");
            int clientNumber = 0;

            while (true) {
                System.out.println("Server: Waiting for connections on port ..." + SERVER_PORT);
                clientSocket = serverSocket.accept();
                clientNumber++;
                System.out.println("Server: Listening for connections on port ..." + SERVER_PORT);

                System.out.println("Server: Client " + clientNumber + " has connected.");
                System.out.println("Server: Port number of remote client: " + clientSocket.getPort());
                System.out.println("Server: Port number of the socket used to talk with client " + clientSocket.getLocalPort());

                Thread t = new Thread(new ClientHandler(clientSocket, clientNumber));
                t.start();
            }

        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            System.exit(1);
        } finally {

            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }

            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
        System.out.println("Server: Server has stopped");
    }
}

/**
 * Main Author: Bianca Valicec
 **/
class ClientHandler implements Runnable {
    final int clientNumber;
    static BufferedReader socketReader;
    PrintWriter socketWriter;
    Socket clientSocket;

    public ClientHandler(Socket clientSocket, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        try {
            this.socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error creating client handler: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    @Override
    public void run() {
        String request;
        try {
            while ((request = socketReader.readLine()) != null) {
                System.out.println("Server: (ClientHandler): Read command from client " + clientNumber + ": " + request);

                int choice = Integer.parseInt(request);
                switch (choice) {
                    case 1:
                        findAllUsers();
                        break;
                    case 2:
                        findUserByStudentId();
                        break;
                    case 3:
                        deleteUserByStudentId();
                        break;
                    case 4:
                        insertNewUser();
                        break;
                    case 5:
                        updateUserByStudentId();
                        break;
                    case 6:
                        findUsersUsingFilter();
                        break;
                    case 7:
                        convertUserToJson();
                        break;
                    case 8:
                        convertUsersListToJson();
                        break;
                    case 9:
                        socketWriter.println("Sorry to see you leaving. Goodbye.");
                        System.out.println("Server message: Client has notified us that it is quitting.");
                        break;
                    default:
                        socketWriter.println("error I'm sorry I don't understand your request");
                        System.out.println("Server message: Invalid request from client.");
                        break;
                }

            }
        } catch (IOException ex) {
            System.out.println("Error reading from client: " + ex.getMessage());
        } finally {
            this.socketWriter.close();
            try {
                socketReader.close();
                this.clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
        System.out.println("Server: (ClientHandler): Handler for Client " + clientNumber + " is terminating .....");

    }

    private static final UserDaoInterface IUserDao = new MySqlUserDao();

    /**
     * Main Author: Bianca Valicec
     **/
    private static void displayUsers(List<User> users) {
        // Display header
        System.out.printf("%-10s %-10s %-15s %-15s %-10s %-20s %-10s %-10s%n", "ID", "Student ID", "First Name", "Last Name", "Course ID", "Course Name", "Grade", "Semester");
        // Display each user
        for (User user : users) {
            displayUser(user);
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private static void displayUser(User user) {
        if (user != null) {
            System.out.printf("%-10d %-10d %-15s %-15s %-10d %-20s %-10.2f %-10s%n",
                    user.getId(), user.getStudentId(), user.getFirstName(), user.getLastName(),
                    user.getCourseId(), user.getCourseName(), user.getGrade(), user.getSemester());
        } else {
            System.out.println("User not found.");
        }
    }

    /**
     * Main Author: Liam Moore
     **/
    private static void findAllUsers() {
        try {
            System.out.println("\n--- Finding all users ---");
            List<User> users = IUserDao.findAllUsers();
            displayUsers(users);
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Main Author: Liam Moore
     * Other Contributors: Bianca Valicec
     **/
    private static void findUserByStudentId() throws IOException {
        try {
            System.out.println("\n--- Finding user by student ID ---");
            System.out.print("Enter student ID: ");
            String studentIdInput = socketReader.readLine();
            int studentId = Integer.parseInt(studentIdInput);// Consume newline
            socketReader.readLine(); // Consume newline
            User user = IUserDao.findUserByStudentId(studentId);
            displayUser(user);
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException | IOException e) {
            System.out.println("Invalid input. Please enter a valid student ID.");
            socketReader.readLine(); // Clear the invalid input from the scanner
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private static void deleteUserByStudentId() throws IOException {
        try {
            System.out.println("\n--- Deleting user by student ID ---");
            System.out.print("Enter student ID to delete: ");
            String studentIdInput = socketReader.readLine();
            int studentId = Integer.parseInt(studentIdInput);
            socketReader.readLine(); // Consume newline
            boolean deletionResult = IUserDao.deleteUserByStudentId(studentId);
            if (deletionResult) {
                System.out.println("User with student ID " + studentId + " deleted successfully.");
            } else {
                System.out.println("No user found with student ID " + studentId + ".");
            }
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException | IOException e) {
            System.out.println("Invalid input. Please enter a valid student ID.");
            socketReader.readLine(); // Clear the invalid input from the scanner
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private static void insertNewUser() throws IOException {
        try {
            System.out.println("\n--- Inserting new user ---");
            System.out.print("Enter student ID: ");
            String studentIdInput = socketReader.readLine();
            int studentId = Integer.parseInt(studentIdInput);
            socketReader.readLine(); // Consume newline
            System.out.print("Enter first name: ");
            String firstName = socketReader.readLine();
            System.out.print("Enter last name: ");
            String lastName = socketReader.readLine();
            System.out.print("Enter course ID: ");
            int courseId = socketReader.read();
            socketReader.readLine(); // Consume newline
            System.out.print("Enter course name: ");
            String courseName = socketReader.readLine();
            System.out.print("Enter grade: ");
            String gradeInput = socketReader.readLine();
            float grade = Float.parseFloat(gradeInput);
            socketReader.readLine();// Consume newline
            System.out.print("Enter semester: ");
            String semester = socketReader.readLine();

            User newUser = new User(studentId, firstName, lastName, courseId, courseName, grade, semester);
            User insertedUser = IUserDao.insertUser(newUser);
            System.out.println("User inserted successfully. ID: " + insertedUser.getId());
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid data.");
            socketReader.readLine(); // Clear the invalid input from the scanner
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private static void updateUserByStudentId() throws IOException {
        try {
            System.out.println("\n--- Updating user by student ID ---");
            System.out.print("Enter student ID to update: ");
            String studentIdInput = socketReader.readLine();
            int studentIdToUpdate = Integer.parseInt(studentIdInput);
            socketReader.readLine(); // Consume newline

            System.out.print("Enter new first name: ");
            String newFirstName = socketReader.readLine();
            System.out.print("Enter new last name: ");
            String newLastName = socketReader.readLine();
            System.out.print("Enter new course ID: ");
            String courseId = socketReader.readLine();
            int newCourseId = Integer.parseInt(courseId);
            socketReader.readLine(); // Consume newline
            System.out.print("Enter new course name: ");
            String newCourseName = socketReader.readLine();
            System.out.print("Enter new grade: ");
            String grade = socketReader.readLine();
            float newGrade = Float.parseFloat(grade);
            socketReader.readLine(); // Consume newline
            System.out.print("Enter new semester: ");
            String newSemester = socketReader.readLine();

            User updatedUser = new User(newFirstName, newLastName, newCourseId, newCourseName, newGrade, newSemester);
            IUserDao.updateUserByStudentId(studentIdToUpdate, updatedUser);
            System.out.println("User updated successfully.");
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid data.");
            socketReader.readLine(); // Clear the invalid input from the scanner
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private static void findUsersUsingFilter() {
        try {
            System.out.println("\n--- Finding users using filter ---");
            // Call DAO method to find users using a filter
            List<User> filteredUsers = IUserDao.findUsersUsingFilter(new UserGradeComparator());
            // Display filtered users
            displayUsers(filteredUsers);
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private static void convertUserToJson() throws IOException {
        try {
            System.out.println("\n--- Converting user to JSON ---");
            System.out.print("Enter student ID: ");
            String studentIdInput = socketReader.readLine();
            int studentId = Integer.parseInt(studentIdInput);
            socketReader.readLine(); // Consume newline
            String userJson = IUserDao.findUserJsonByStudentId(studentId);
            System.out.println(userJson);
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InputMismatchException | IOException e) {
            System.out.println("Invalid input. Please enter a valid student ID.");
            socketReader.readLine(); // Clear the invalid input from the scanner
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private static void convertUsersListToJson() {
        try {
            System.out.println("\n--- Converting list of users to JSON ---");
            // Call DAO method to get list of users
            List<User> users = IUserDao.findAllUsers();
            // Convert list of users to JSON
            String json = IUserDao.usersListToJson(users);
            // Display JSON
            System.out.println("JSON representation of users:\n" + json);
        } catch (DaoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}