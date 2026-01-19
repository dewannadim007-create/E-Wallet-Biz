package com.example.app.config;

import com.example.app.model.User;
import com.example.app.services.TransactionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;

/**
 * DataSeeder - Populates the database with initial sample data.
 * Useful for testing and setting up a fresh environment.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(MongoTemplate mongoTemplate, TransactionService transactionService,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            // DATA RESET: Start fresh (Clear existing user data only)
            System.out.println("Cleaning database (Users only)...");
            // mongoTemplate.dropCollection(Branch.class); // Preserved
            mongoTemplate.dropCollection(User.class); // Reset Users
            // mongoTemplate.dropCollection(Utility.class);// Preserved
            // Transactions & Checkbooks are PRESERVED (Not dropped/seeded)

            // 1. Seed Branches (SKIPPED)
            /*
             * System.out.println("Seeding 15 Branches...");
             * List<Branch> branches = Arrays.asList(
             * // Dhaka North
             * new Branch("Gulshan Head Office", "Main", "Gulshan-1, Dhaka", "BR001",
             * "Dhaka"),
             * new Branch("Banani Branch", "Sub-branch", "Banani Road 11", "BR002",
             * "Dhaka"),
             * new Branch("Uttara Sector 4", "Sub-branch", "Uttara, Dhaka", "BR003",
             * "Dhaka"),
             * new Branch("Uttara Sector 13", "ATM", "Uttara, Dhaka", "ATM001", "Dhaka"),
             * new Branch("Mirpur 10", "Sub-branch", "Mirpur 10 Roundabout", "BR004",
             * "Dhaka"),
             * new Branch("Mirpur DOHS", "ATM", "Mirpur DOHS Shopping Complex", "ATM002",
             * "Dhaka"),
             * new Branch("Bashundhara R/A", "Sub-branch", "Bashundhara Gate", "BR005",
             * "Dhaka"),
             * 
             * // Dhaka South
             * new Branch("Motijheel Principal", "Main", "Motijheel C/A", "BR006", "Dhaka"),
             * new Branch("Dhanmondi 27", "Sub-branch", "Dhanmondi, Dhaka", "BR007",
             * "Dhaka"),
             * new Branch("Dhanmondi 8", "ATM", "Dhanmondi 8, Dhaka", "ATM003", "Dhaka"),
             * new Branch("Lalbagh Branch", "Sub-branch", "Lalbagh Fort Road", "BR008",
             * "Dhaka"),
             * 
             * // Regional
             * new Branch("Agrabad Branch", "Main", "Agrabad C/A, Chittagong", "BR009",
             * "Chittagong"),
             * new Branch("GEC Circle ATM", "ATM", "GEC Circle, Chittagong", "ATM004",
             * "Chittagong"),
             * new Branch("Zindabazar Branch", "Main", "Zindabazar, Sylhet", "BR010",
             * "Sylhet"),
             * new Branch("Rajshahi Zero Point", "Sub-branch", "Zero Point, Rajshahi",
             * "BR011", "Rajshahi"));
             * // Example coordinates for a few key ones
             * branches.get(0).setLatitude(23.7925);
             * branches.get(0).setLongitude(90.4078); // Gulshan
             * branches.get(7).setLatitude(23.7330);
             * branches.get(7).setLongitude(90.4172); // Motijheel
             * 
             * mongoTemplate.insertAll(branches);
             */

            // 2. Seed Utilities (SKIPPED)
            /*
             * System.out.println("Seeding Utilities...");
             * List<Utility> utilities = new ArrayList<>();
             * 
             * // Electricity (4 Types)
             * utilities.add(new Utility("DESCO", "Prepaid", "Electricity"));
             * utilities.add(new Utility("DESCO", "Postpaid", "Electricity"));
             * utilities.add(new Utility("DPDC", "Prepaid", "Electricity"));
             * utilities.add(new Utility("DPDC", "Postpaid", "Electricity"));
             * // Gas (4 Types)
             * utilities.add(new Utility("Titas", "Prepaid", "Gas"));
             * utilities.add(new Utility("Titas", "Postpaid", "Gas"));
             * utilities.add(new Utility("Karnaphuli", "Prepaid", "Gas"));
             * utilities.add(new Utility("Karnaphuli", "Postpaid", "Gas"));
             * // Mobile (7 Operators/Types)
             * utilities.add(new Utility("Grameenphone", "Prepaid", "Mobile"));
             * utilities.add(new Utility("Grameenphone", "Postpaid", "Mobile"));
             * utilities.add(new Utility("Robi", "Prepaid", "Mobile"));
             * utilities.add(new Utility("Robi", "Postpaid", "Mobile"));
             * utilities.add(new Utility("Banglalink", "Prepaid", "Mobile"));
             * utilities.add(new Utility("Banglalink", "Postpaid", "Mobile"));
             * utilities.add(new Utility("Teletalk", "Prepaid", "Mobile"));
             * 
             * mongoTemplate.insertAll(utilities);
             */

            // 3. Seed Users (7 Total)
            System.out.println("Seeding Users...");

            // 1. Admin
            User admin = new User();
            admin.setName("System Admin");
            admin.setMobile("01700000000"); // Admin Mobile
            admin.setEmail("admin@ewallet.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setAccount("1000000000"); // Special Admin Account
            admin.setNid("1990000000000");
            admin.setAddress("Head Office, Dhaka");
            admin.setBalance(100000000.0); // High Bank Balance
            admin.setWalletBalance(50000.0);
            admin.setAccountType("CURRENT");
            admin.setUserRole("ADMIN");
            admin.setIsActive(true);
            admin.setCreatedAt(LocalDateTime.now());
            mongoTemplate.save(admin);

            // 3 Users with BOTH Bank & Wallet
            for (int i = 1; i <= 3; i++) {
                User u = new User();
                u.setName("Full User " + i);
                u.setMobile("0171111111" + i);
                u.setEmail("fulluser" + i + "@test.com");
                u.setPassword(passwordEncoder.encode("123456"));
                u.setAccount("202300000" + i);
                u.setNid("199500000000" + i);
                u.setAddress("Dhaka, Bangladesh");
                u.setBalance(50000.0 * i); // Has Bank Balance
                u.setWalletBalance(1000.0 * i); // Has Wallet Balance
                u.setAccountType("SAVINGS");
                u.setUserRole("USER");
                u.setIsActive(true);
                u.setCreatedAt(LocalDateTime.now());
                mongoTemplate.save(u);
            }

            // 3 Users with ONLY Bank (No Wallet yet)
            for (int i = 4; i <= 6; i++) {
                User u = new User();
                u.setName("Bank Only User " + i);
                u.setMobile("0162222222" + i); // Different series
                u.setEmail("bankuser" + i + "@test.com");
                u.setPassword(passwordEncoder.encode("123456"));
                u.setAccount("202300000" + i);
                u.setNid("199800000000" + i);
                u.setAddress("Chittagong, Bangladesh");
                u.setBalance(100000.0); // Has Bank Balance
                u.setWalletBalance(0.0); // Empty/Zero Wallet
                u.setAccountType("SAVINGS");
                u.setUserRole("USER");
                u.setIsActive(true);
                u.setCreatedAt(LocalDateTime.now());
                mongoTemplate.save(u);
            }

            System.out.println("Database user seeding completed. (Users=7)");
        };
    }
}
