package Server;

import Server.Comparators.UserGradeComparator;
import Server.DAOs.MySqlUserDao;
import Server.DAOs.MySqlDao;
import Server.DAOs.UserDaoInterface;
import Server.DTOs.User;
import Server.Exceptions.DaoException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Main Author: Bianca Valicec
 **/

public class Server {

    final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        Server server = new Server();
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
    private final UserDaoInterface IUserDao = new MySqlUserDao();
    BufferedReader socketReader;
    PrintWriter socketWriter;
    Socket clientSocket;

    public ClientHandler(Socket clientSocket, int clientNumber) {
        this.clientSocket = clientSocket;
        this.clientNumber = clientNumber;
        try {
            socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
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
        try {
            while (true) {
                String command = socketReader.readLine();
                if (command == null) {
                    break;
                }
                System.out.println("Server: (ClientHandler): Read command from client " + clientNumber + ": " + command);

                int choice = Integer.parseInt(command);
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
                        displayEntityById();
                        break;
                    case 0:
                        socketWriter.println("Sorry to see you leaving. Goodbye.");
                        System.out.println("Server message: Client has notified us that it is quitting.");
                        break;
                    default:
                        socketWriter.println("Error I'm sorry I don't understand your request");
                        System.out.println("Server message: Invalid request from client.");
                        break;
                }
                System.out.println("Server: (ClientHandler): Command processed.");
            }
        } catch (IOException ex) {
            System.out.println("Error reading from client: " + ex.getMessage());
        } finally {
            try {
                socketWriter.close();
                socketReader.close();
                clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Error closing resources: " + ex.getMessage());
            }
        }
        System.out.println("Server: (ClientHandler): Handler for Client " + clientNumber + " is terminating .....");
    }

    /**
     * Main Author: Liam Moore
     * Other Contributors: Bianca Valicec
     **/
    private void findAllUsers() {
        try {
            List<User> users = IUserDao.findAllUsers();
            if (users.isEmpty()) {
                socketWriter.println("No users found.");
            } else {
                StringBuilder response = new StringBuilder();
                response.append(String.format("%-10s %-10s %-15s %-15s %-10s %-20s %-10s %-10s%n",
                        "ID", "Student ID", "First Name", "Last Name", "Course ID", "Course Name", "Grade", "Semester"));
                for (User user : users) {
                    response.append(formatUser(user)).append("\n");
                }
                socketWriter.println(response.toString());
            }
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        }
    }

    /**
     * Main Author: Liam Moore
     * Other Contributors: Bianca Valicec
     **/
    private void findUserByStudentId() throws IOException {
        User user = null;
        while (user == null) {
            socketWriter.println("Enter student ID: ");
            String studentIdStr = socketReader.readLine();
            int studentId;
            try {
                studentId = Integer.parseInt(studentIdStr);
                user = IUserDao.findUserByStudentId(studentId);
                if (user != null) {
                    displayUser(user);
                    System.out.println("Server: (ClientHandler): User found for student ID " + studentId);
                    break;
                } else {
                    socketWriter.println("User not found. Please try again.");
                }
            } catch (NumberFormatException e) {
                socketWriter.println("Invalid input. Please enter a valid student ID.");
            } catch (DaoException e) {
                socketWriter.println("Error accessing database: " + e.getMessage());
                break;
            }
        }
    }



    /**
     * Main Author: Bianca Valicec
     **/
    private void deleteUserByStudentId() {
        try {
            socketWriter.println("Enter student ID to delete: ");
            String studentIdStr = socketReader.readLine();
            int studentId = Integer.parseInt(studentIdStr);
            boolean deletionResult = IUserDao.deleteUserByStudentId(studentId);
            if (deletionResult) {
                socketWriter.println("User with student ID " + studentId + " deleted successfully.");
            } else {
                socketWriter.println("No user found with student ID " + studentId + ".");
            }
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        } catch (NumberFormatException | IOException e) {
            socketWriter.println("Invalid input. Please enter a valid student ID.");
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private void insertNewUser() {
        try {
            socketWriter.println("Enter student ID: ");
            int studentId = Integer.parseInt(socketReader.readLine());
            socketWriter.println("Enter first name: ");
            String firstName = socketReader.readLine();
            socketWriter.println("Enter last name: ");
            String lastName = socketReader.readLine();
            socketWriter.println("Enter course ID: ");
            int courseId = Integer.parseInt(socketReader.readLine());
            socketWriter.println("Enter course name: ");
            String courseName = socketReader.readLine();
            socketWriter.println("Enter grade: ");
            float grade = Float.parseFloat(socketReader.readLine());
            socketWriter.println("Enter semester: ");
            String semester = socketReader.readLine();

            User newUser = new User(studentId, firstName, lastName, courseId, courseName, grade, semester);
            User insertedUser = IUserDao.insertUser(newUser);
            socketWriter.println("User inserted successfully. ID: " + insertedUser.getId());
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        } catch (NumberFormatException | IOException e) {
            socketWriter.println("Invalid input. Please enter valid data.");
        }
    }
    /**
     * Main Author: Bianca Valicec
     **/
    private void updateUserByStudentId() {
        try {
            socketWriter.println("Enter student ID to update: ");
            int studentIdToUpdate = Integer.parseInt(socketReader.readLine());

            socketWriter.println("Enter new first name: ");
            String newFirstName = socketReader.readLine();
            socketWriter.println("Enter new last name: ");
            String newLastName = socketReader.readLine();
            socketWriter.println("Enter new course ID: ");
            int newCourseId = Integer.parseInt(socketReader.readLine());
            socketWriter.println("Enter new course name: ");
            String newCourseName = socketReader.readLine();
            socketWriter.println("Enter new grade: ");
            float newGrade = Float.parseFloat(socketReader.readLine());
            socketWriter.println("Enter new semester: ");
            String newSemester = socketReader.readLine();

            User updatedUser = new User(newFirstName, newLastName, newCourseId, newCourseName, newGrade, newSemester);
            IUserDao.updateUserByStudentId(studentIdToUpdate, updatedUser);
            socketWriter.println("User updated successfully.");
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        } catch (NumberFormatException | IOException e) {
            socketWriter.println("Invalid input. Please enter valid data.");
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private void findUsersUsingFilter() {
        try {
            List<User> filteredUsers = IUserDao.findUsersUsingFilter(new UserGradeComparator());
            displayUsers(filteredUsers);
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private void convertUserToJson() {
        try {
            socketWriter.println("Enter student ID: ");
            int studentId = Integer.parseInt(socketReader.readLine());
            String userJson = IUserDao.findUserJsonByStudentId(studentId);
            socketWriter.println(userJson);
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        } catch (NumberFormatException | IOException e) {
            socketWriter.println("Invalid input. Please enter a valid student ID.");
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private void convertUsersListToJson() {
        try {
            List<User> users = IUserDao.findAllUsers();
            String json = IUserDao.usersListToJson(users);
            socketWriter.println("JSON representation of users:\n" + json);
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    // Helper methods for displaying users
    private void displayUsers(List<User> users) {
        StringBuilder response = new StringBuilder();
        if (users.isEmpty()) {
            response.append("No users found.");
        } else {
            // Append header
            response.append(String.format("%-10s %-10s %-15s %-15s %-10s %-20s %-10s %-10s%n",
                    "ID", "Student ID", "First Name", "Last Name", "Course ID", "Course Name", "Grade", "Semester"));
            // Append each user
            for (User user : users) {
                response.append(String.format("%-10d %-10d %-15s %-15s %-10d %-20s %-10.2f %-10s%n",
                        user.getId(), user.getStudentId(), user.getFirstName(), user.getLastName(),
                        user.getCourseId(), user.getCourseName(), user.getGrade(), user.getSemester()));
            }
        }
        socketWriter.println(response.toString());
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private void displayUser(User user) {
        if (user != null) {
            StringBuilder response = new StringBuilder();
            // Append header
            response.append(String.format("%-10s %-10s %-15s %-15s %-10s %-20s %-10s %-10s%n",
                    "ID", "Student ID", "First Name", "Last Name", "Course ID", "Course Name", "Grade", "Semester"));
            // Append user
            response.append(String.format("%-10d %-10d %-15s %-15s %-10d %-20s %-10.2f %-10s%n",
                    user.getId(), user.getStudentId(), user.getFirstName(), user.getLastName(),
                    user.getCourseId(), user.getCourseName(), user.getGrade(), user.getSemester()));
            socketWriter.println(response.toString());
        } else {
            socketWriter.println("User not found.");
        }
    }

    /**
     * Main Author: Bianca Valicec
     **/
    private String formatUser(User user) {
        return String.format("%-10d %-10d %-15s %-15s %-10d %-20s %-10.2f %-10s",
                user.getId(), user.getStudentId(), user.getFirstName(), user.getLastName(),
                user.getCourseId(), user.getCourseName(), user.getGrade(), user.getSemester());
    }

    private void displayEntityById() {
        try {
            socketWriter.println("Enter ID of the entity to display: ");
            int entityId = Integer.parseInt(socketReader.readLine());
            // Assuming you have a method in UserDaoInterface to find entity by ID
            User entity = IUserDao.findUserByStudentId(entityId);
            if (entity != null) {
                String json = IUserDao.findUserJsonByStudentId(entityId);
                socketWriter.println(json);
            } else {
                socketWriter.println("Entity not found.");
            }
        } catch (DaoException e) {
            socketWriter.println("Error: " + e.getMessage());
        } catch (NumberFormatException | IOException e) {
            socketWriter.println("Invalid input. Please enter a valid ID.");
        }
    }
}