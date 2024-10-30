package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:src/main/java/Database/service_squade.db";


    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
        return conn;
    }




    public static void initializeDatabase() {

        String dropUserTable = "DROP TABLE IF EXISTS User;";

        String userTable = """
            CREATE TABLE IF NOT EXISTS User (
                user_id INTEGER PRIMARY KEY,
                name TEXT,
                email TEXT,
                phone TEXT,
                address TEXT,
                password TEXT,
                role TEXT,
                rating REAL,
                registration_date TEXT
            );
        """;
        String serviceTable = """
            CREATE TABLE IF NOT EXISTS Service (
                service_id INTEGER PRIMARY KEY,
                name TEXT,
                description TEXT,
                category TEXT,
                location TEXT,
                price REAL
            );
        """;
        String jobPostTable = """
            CREATE TABLE IF NOT EXISTS Job_Post (
                post_id INTEGER PRIMARY KEY,
                user_id INTEGER,
                service_id INTEGER,
                job_description TEXT,
                status TEXT,
                created_at TEXT,
                FOREIGN KEY (user_id) REFERENCES User(user_id),
                FOREIGN KEY (service_id) REFERENCES Service(service_id)
            );
        """;
        String applicationTable = """
            CREATE TABLE IF NOT EXISTS Application (
                application_id INTEGER PRIMARY KEY,
                post_id INTEGER,
                user_id INTEGER,
                application_date TEXT,
                application_status TEXT,
                FOREIGN KEY (post_id) REFERENCES Job_Post(post_id),
                FOREIGN KEY (user_id) REFERENCES User(user_id)
            );
        """;
        String paymentTable = """
            CREATE TABLE IF NOT EXISTS Payment (
                payment_id INTEGER PRIMARY KEY,
                user_id INTEGER,
                post_id INTEGER,
                amount REAL,
                payment_method TEXT,
                payment_status TEXT,
                payment_date TEXT,
                FOREIGN KEY (user_id) REFERENCES User(user_id),
                FOREIGN KEY (post_id) REFERENCES Job_Post(post_id)
            );
        """;
        String messageTable = """
            CREATE TABLE IF NOT EXISTS Message (
                message_id INTEGER PRIMARY KEY,
                sender_id INTEGER,
                receiver_id INTEGER,
                message_content TEXT,
                sent_at TEXT,
                FOREIGN KEY (sender_id) REFERENCES User(user_id),
                FOREIGN KEY (receiver_id) REFERENCES User(user_id)
            );
        """;
        String reviewTable = """
            CREATE TABLE IF NOT EXISTS Review (
                review_id INTEGER PRIMARY KEY,
                reviewer_id INTEGER,
                reviewed_id INTEGER,
                rating REAL,
                review_text TEXT,
                review_date TEXT,
                FOREIGN KEY (reviewer_id) REFERENCES User(user_id),
                FOREIGN KEY (reviewed_id) REFERENCES User(user_id)
            );
        """;
        String historyTable = """
            CREATE TABLE IF NOT EXISTS History (
                history_id INTEGER PRIMARY KEY,
                user_id INTEGER,
                post_id INTEGER,
                service_name TEXT,
                service_date TEXT,
                FOREIGN KEY (user_id) REFERENCES User(user_id),
                FOREIGN KEY (post_id) REFERENCES Job_Post(post_id)
            );
        """;



        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // Drop the User table if it exists
            stmt.execute(dropUserTable);
            // Then create the tables
            stmt.execute(userTable);
            // (Execute other table creation statements here...)
            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }


        // Executing all table creation statements
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(userTable);
            stmt.execute(serviceTable);
            stmt.execute(jobPostTable);
            stmt.execute(applicationTable);
            stmt.execute(paymentTable);
            stmt.execute(messageTable);
            stmt.execute(reviewTable);
            stmt.execute(historyTable);
            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}
