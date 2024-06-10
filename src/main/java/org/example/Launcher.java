package org.example;


import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Launcher {

    public static final int MAX_THREADS = Integer.MAX_VALUE;
    public static final int QUERIES_PRE_THREAD = 100;

    public AtomicInteger errorCounter = new AtomicInteger(0);
    public AtomicReference<Exception> lastError = new AtomicReference<>();

    public static void main(String[] args) throws Exception {
        new Launcher().run();
    }

    public void run() throws Exception {
        populateDatabase();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("testPU", new Properties());

        // run queries on all available cores
        int nThreads = Math.min(MAX_THREADS, Runtime.getRuntime().availableProcessors());
        List<Thread> threads = IntStream.range(0, nThreads)
                .mapToObj(i -> Thread.ofPlatform().start(() -> executeQueries(emf)))
                .toList();

        // wait for all threads to finish
        joinAll(threads);

        emf.close();

        if (lastError.get() != null) {
            lastError.get().printStackTrace(System.err);
        }
        System.out.println("Total # of errors: " + errorCounter.get());
    }

    private void executeQueries(EntityManagerFactory emf) {
        for (int i = 0; i < QUERIES_PRE_THREAD; i++) {
            var em = emf.createEntityManager();
            try {
                var cacheBuster = "cacheBuster." + Thread.currentThread().getName() + "." + i;

                var jpql = "SELECT p FROM Person p"

                        // IMPORTANT: we need to call a COALESCE function for the error to appear!
                        // (other functions like CONCAT work normally)
                        + " WHERE p.name = coalesce(p.name, 'x')"

                        // we are busting the query cache otherwise the error would only happen
                        // on the first iterations and not every time
                         + " AND p.name = '" + cacheBuster + "'";

                var query = em.createQuery(jpql, Person.class);
                query.getResultList();

            } catch (Exception e) {
                System.out.println("Error in thread " + Thread.currentThread().getName()
                        + " on iteration #" + (i + 1));
                errorCounter.incrementAndGet();
                lastError.set(e);
            }
            em.close();
        }
    }

    private static void populateDatabase() throws SQLException {
        DriverManager.registerDriver(new org.h2.Driver());

        try (var conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")) {
            var statement = conn.createStatement();
            statement.execute("CREATE TABLE person(id INT PRIMARY KEY, name VARCHAR(255))");
            // database content is not relevant for this test
            //statement.execute("INSERT INTO person VALUES(1, 'Donald')");
            //statement.execute("INSERT INTO person VALUES(2, 'Joe')");
        }
    }

    private static void joinAll(List<Thread> threads) {
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

