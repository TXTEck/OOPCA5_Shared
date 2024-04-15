package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import Server.DTOs.User;
import com.google.gson.Gson;

public class Client {

    private static final int SERVER_PORT = 8080;
    private static final String SERVER_HOST = "localhost";

    private static final Map<Integer, String> COMMANDS = new HashMap<>();
    static {
        COMMANDS.put(1, "Find All Users");
        COMMANDS.put(2, "Find User By Student ID");
        COMMANDS.put(3, "Delete User By Student ID");
        COMMANDS.put(4, "Insert New User");
        COMMANDS.put(5, "Update User By Student ID");
        COMMANDS.put(6, "Find Users Using Filter");
        COMMANDS.put(7, "Convert List of Users to JSON");
        COMMANDS.put(8, "Convert a single user to JSON");
        COMMANDS.put(9, "Display Entity by Id"); // Added for Feature 9
        COMMANDS.put(11, "Insert New User"); // Added for Feature 11
        COMMANDS.put(0, "Exit");
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Client running and Connected to Server");

            while (true) {
                printCommands();
                System.out.print("Enter the number of the command you would like to run: ");
                String command = consoleInput.readLine();
                int commandInt = Integer.parseInt(command);
                out.println(commandInt);

                if (commandInt == 0) {
                    break;  // Exit the loop if the command is 0 (Exit)
                }

                switch (commandInt) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        handleStandardCommand(in);
                        break;
                    case 9:
                        displayEntityById(out, in, consoleInput);
                        break;
                    case 11:
                        insertNewUser(out, in);
                        break;
                    default:
                        System.out.println("Invalid command.");
                        break;
                }
            }
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
        System.out.println("Client Closed");
    }

    private void printCommands() {
        System.out.println("\n---- Commands ----");
        COMMANDS.forEach((key, value) -> System.out.println(key + ". " + value));
    }

    private void handleStandardCommand(BufferedReader in) throws IOException {
        String response;
        while ((response = in.readLine()) != null) {
            System.out.println(response);
            if (response.isEmpty()) {
                break;  // Exit the loop when an empty line is received
            }
        }
    }

    private void displayEntityById(PrintWriter out, BufferedReader in, BufferedReader consoleInput) throws IOException {
        try {
            out.println("9"); // Send command for Display Entity by id
            System.out.print("Enter the ID of the entity to display: ");
            String entityId = consoleInput.readLine();
            out.println(entityId);

            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
                if (response.isEmpty()) {
                    break;  // Exit the loop when an empty line is received
                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    private void insertNewUser(PrintWriter out, BufferedReader in) {
        Scanner keyboard = new Scanner(System.in);
        try{
            System.out.println("Enter student name: ");
            String studentName = keyboard.nextLine();
            System.out.println("Enter student last name: ");
            String studentLastName = keyboard.nextLine();
            System.out.println("Enter course ID: ");
            int courseId = keyboard.nextInt();
            System.out.println("Enter course name: ");
            String courseName = keyboard.nextLine();
            System.out.println("Enter grade: ");
            float grade = keyboard.nextFloat();
            System.out.println("Enter semester: ");
            String semester = keyboard.nextLine();

            User newUser = new User(studentName, studentLastName, courseId, courseName, grade, semester);
            Gson gson = new Gson();
            String json = gson.toJson(newUser);
            out.println(json);

            String response = in.readLine();
            System.out.println(response);
        }catch (IOException e){
            System.out.println("IO Exception: " + e.getMessage());
        }
    }
}