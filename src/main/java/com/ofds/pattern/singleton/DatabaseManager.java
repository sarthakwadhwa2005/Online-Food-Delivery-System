package com.ofds.pattern.singleton;

import org.springframework.stereotype.Component;
import java.util.logging.Logger;

/**
 * DESIGN PATTERN: Singleton (Creational)
 * Ensures only one instance of DatabaseManager exists throughout the application.
 * Demonstrates the Singleton pattern; actual DB connections are managed by Spring/Hibernate.
 */
@Component
public class DatabaseManager {

    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private static volatile DatabaseManager instance;
    private boolean connected = false;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public void connect() {
        if (!connected) {
            connected = true;
            logger.info("[Singleton] DatabaseManager: connection established.");
        }
    }

    public void disconnect() {
        if (connected) {
            connected = false;
            logger.info("[Singleton] DatabaseManager: connection closed.");
        }
    }

    public boolean isConnected() { return connected; }
}
