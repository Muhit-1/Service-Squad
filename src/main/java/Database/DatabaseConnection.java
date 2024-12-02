package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL_MAIN = "jdbc:sqlite:src/main/java/Database/db_service_squad.db";
    private static final String DB_URL_DHAKA_PROVIDER = "jdbc:sqlite:src/main/java/Database/db_dhaka_provider.db";
    private static final String DB_URL_DHAKA_CUSTOMER = "jdbc:sqlite:src/main/java/Database/db_dhaka_customer.db";
    private static final String DB_URL_OTHER_PROVIDER = "jdbc:sqlite:src/main/java/Database/db_other_provider.db";
    private static final String DB_URL_OTHER_CUSTOMER = "jdbc:sqlite:src/main/java/Database/db_other_customer.db";

    public static Connection connect(String dbUrl) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(dbUrl);
            System.out.println("Connection to SQLite has been established for: " + dbUrl);
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
        return conn;
    }

    public static void initializeDatabase() {
        String[] dbUrls = {
                DB_URL_MAIN,
                DB_URL_DHAKA_PROVIDER,
                DB_URL_DHAKA_CUSTOMER,
                DB_URL_OTHER_PROVIDER,
                DB_URL_OTHER_CUSTOMER
        };


        String locationTable = """
                    CREATE TABLE IF NOT EXISTS Location (
                        location_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        city TEXT NOT NULL,
                        zipcode TEXT NOT NULL,
                        area TEXT NOT NULL
                    );
                """;

        String userTable = """
                    CREATE TABLE IF NOT EXISTS User (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        phone TEXT NOT NULL,
                        address TEXT,
                        location_id INTEGER,
                        role TEXT CHECK(role IN ('Provider', 'Customer')),
                        rating REAL,
                        registration_date DATE DEFAULT CURRENT_DATE,
                        FOREIGN KEY (location_id) REFERENCES Location(location_id)
                    );
                """;

        String serviceTable = """
                    CREATE TABLE IF NOT EXISTS Service (
                        service_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        description TEXT,
                        category TEXT,
                        location_id INTEGER,
                        price REAL,
                        FOREIGN KEY (location_id) REFERENCES Location(location_id)
                    );
                """;

        String jobPostTable = """
                    CREATE TABLE IF NOT EXISTS Job_Post (
                        post_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        service_id INTEGER NOT NULL,
                        job_description TEXT,
                        status TEXT CHECK(status IN ('Open', 'Closed', 'In Progress')),
                        created_at DATE DEFAULT CURRENT_DATE,
                        FOREIGN KEY (user_id) REFERENCES User(user_id),
                        FOREIGN KEY (service_id) REFERENCES Service(service_id)
                    );
                """;

        String applicationTable = """
                    CREATE TABLE IF NOT EXISTS Application (
                        application_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        post_id INTEGER NOT NULL,
                        user_id INTEGER NOT NULL,
                        application_date DATE DEFAULT CURRENT_DATE,
                        application_status TEXT CHECK(application_status IN ('Pending', 'Accepted', 'Rejected')),
                        FOREIGN KEY (post_id) REFERENCES Job_Post(post_id),
                        FOREIGN KEY (user_id) REFERENCES User(user_id)
                    );
                """;

        String paymentTable = """
                    CREATE TABLE IF NOT EXISTS Payment (
                        payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        post_id INTEGER NOT NULL,
                        amount REAL NOT NULL,
                        payment_method TEXT CHECK(payment_method IN ('Credit Card', 'Debit Card', 'Bank Transfer', 'Cash')),
                        payment_status TEXT CHECK(payment_status IN ('Completed', 'Pending', 'Failed')),
                        payment_date DATE DEFAULT CURRENT_DATE,
                        FOREIGN KEY (user_id) REFERENCES User(user_id),
                        FOREIGN KEY (post_id) REFERENCES Job_Post(post_id)
                    );
                """;

        String messageTable = """
                    CREATE TABLE IF NOT EXISTS Message (
                        message_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        sender_id INTEGER NOT NULL,
                        receiver_id INTEGER NOT NULL,
                        message_content TEXT NOT NULL,
                        sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (sender_id) REFERENCES User(user_id),
                        FOREIGN KEY (receiver_id) REFERENCES User(user_id)
                    );
                """;

        String reviewTable = """
                    CREATE TABLE IF NOT EXISTS Review (
                        review_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        reviewer_id INTEGER NOT NULL,
                        reviewed_id INTEGER NOT NULL,
                        rating REAL CHECK(rating BETWEEN 0 AND 5),
                        review_text TEXT,
                        review_date DATE DEFAULT CURRENT_DATE,
                        FOREIGN KEY (reviewer_id) REFERENCES User(user_id),
                        FOREIGN KEY (reviewed_id) REFERENCES User(user_id)
                    );
                """;

        String historyTable = """
                    CREATE TABLE IF NOT EXISTS History (
                        history_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id INTEGER NOT NULL,
                        post_id INTEGER NOT NULL,
                        service_name TEXT NOT NULL,
                        service_date DATE NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES User(user_id),
                        FOREIGN KEY (post_id) REFERENCES Job_Post(post_id)
                    );
                """;

        String[] tableStatements = {
                locationTable, userTable, serviceTable, jobPostTable,
                applicationTable, paymentTable, messageTable, reviewTable, historyTable
        };

        // Initialize all databases
        for (String dbUrl : dbUrls) {
            try (Connection conn = connect(dbUrl);
                 Statement stmt = conn.createStatement()) {
                for (String tableSql : tableStatements) {
                    stmt.execute(tableSql);
                }
                System.out.println("Tables created or already exist for: " + dbUrl);
            } catch (SQLException e) {
                System.out.println("Error creating tables for " + dbUrl + ": " + e.getMessage());
            }
        }
    }
}
