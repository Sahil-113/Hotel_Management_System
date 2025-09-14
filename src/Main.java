import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/collegedb";
    private static final String user = "root";
    private static final String password = "12345678";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully!");
            createTable(connection);
            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an Option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        connection.close();
                        return;
                    default:
                        System.out.println("Invalid option! Please try again.");
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS Reservation (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "room INT NOT NULL, " +
                "mobile VARCHAR(15) NOT NULL, " +
                "UNIQUE(room))";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
            System.out.println("Table checked/created successfully!");
        }
    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your Room no.: ");
        int room = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter your Mobile No.: ");
        String mobile = scanner.nextLine();
        String query = "INSERT INTO Reservation (name, room, mobile) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, room);
            preparedStatement.setString(3, mobile);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Reservation Successful!");
            } else {
                System.out.println("Reservation Failed!");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("Room is already reserved!");
            }
        }
    }

    private static void viewReservation(Connection connection) {
        String query = "SELECT * FROM Reservation";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            System.out.println("\nCurrent Reservations:");
            System.out.println("+----+----------------------+------+-----------------+");
            System.out.println("| ID | Name                 | Room | Mobile          |");
            System.out.println("+----+----------------------+------+-----------------+");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int room = resultSet.getInt("room");
                String mobile = resultSet.getString("mobile");

                System.out.printf("| %-2d | %-20s | %-4d | %-15s |\n", id, name, room, mobile);
            }
            System.out.println("+----+----------------------+------+-----------------+");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void getRoomNumber(Connection connection, Scanner scanner) {
        System.out.print("Enter reservation ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String query = "SELECT room FROM Reservation WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int room = resultSet.getInt("room");
                    System.out.println("Room number for reservation ID " + id + " is: " + room);
                } else {
                    System.out.println("No reservation found with ID: " + id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner) {
        System.out.print("Enter reservation ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter new Room no.: ");
        int room = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new Mobile No.: ");
        String mobile = scanner.nextLine();

        String query = "UPDATE Reservation SET name = ?, room = ?, mobile = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, room);
            preparedStatement.setString(3, mobile);
            preparedStatement.setInt(4, id);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Reservation updated successfully!");
            } else {
                System.out.println("Failed to update reservation! ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner) {
        System.out.print("Enter reservation ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String query = "DELETE FROM Reservation WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Failed to delete reservation! ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void exit() {
        System.out.println("Thank you for using Hotel Management System!");
        System.out.println("Exiting...");
    }
}