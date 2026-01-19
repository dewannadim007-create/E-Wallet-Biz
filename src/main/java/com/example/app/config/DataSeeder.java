// package com.example.app.config;

// import com.example.app.model.User;
// import com.example.app.services.TransactionService;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.mongodb.core.MongoTemplate;

// import java.time.LocalDateTime;

// /**
//  * DataSeeder - Populates the database with initial sample data.
//  * Useful for testing and setting up a fresh environment.
//  */
// @Configuration
// public class DataSeeder {

//     @Bean
//     CommandLineRunner initDatabase(MongoTemplate mongoTemplate, TransactionService transactionService,
//             org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
//         return args -> {
//             try {
//                 System.out.println("---------- DATA SEEDER STARTED ----------");

//                 // DATA RESET: Start fresh (Clear existing user data only)
//                 System.out.println("Cleaning database (Users)...");
//                 try {
//                     // Use deleteAll for better compatibility than dropCollection
//                     mongoTemplate.dropCollection(User.class);
//                 } catch (Exception e) {
//                     System.out.println("Warning: Copuld not drop collection (might not exist): " + e.getMessage());
//                 }

//                 // 3. Seed Users (7 Total)
//                 System.out.println("Seeding Users...");

//                 // 1. Admin
//                 User admin = new User();
//                 admin.setName("System Admin");
//                 admin.setMobile("01700000000"); // Admin Mobile
//                 admin.setEmail("admin@ewallet.com");
//                 admin.setPassword(passwordEncoder.encode("admin123"));
//                 admin.setAccount("1000000000"); // Special Admin Account
//                 admin.setNid("1990000000000");
//                 admin.setDOB("1990-01-01"); // Added DOB
//                 admin.setAddress("Head Office, Dhaka");
//                 admin.setBalance(100000000.0); // High Bank Balance
//                 admin.setWalletBalance(50000.0);
//                 admin.setAccountType("CURRENT");
//                 admin.setUserRole("ADMIN");
//                 admin.setIsActive(true);
//                 admin.setCreatedAt(LocalDateTime.now());
//                 mongoTemplate.save(admin);
//                 System.out.println("Admin seeded.");

//                 // 3 Users with BOTH Bank & Wallet
//                 for (int i = 1; i <= 3; i++) {
//                     User u = new User();
//                     u.setName("Full User " + i);
//                     u.setMobile("0171111111" + i);
//                     u.setEmail("fulluser" + i + "@test.com");
//                     u.setPassword(passwordEncoder.encode("123456"));
//                     u.setAccount("202300000" + i);
//                     u.setNid("199500000000" + i);
//                     u.setDOB("1995-01-01"); // Added DOB
//                     u.setAddress("Dhaka, Bangladesh");
//                     u.setBalance(50000.0 * i);
//                     u.setWalletBalance(1000.0 * i);
//                     u.setAccountType("SAVINGS");
//                     u.setUserRole("USER");
//                     u.setIsActive(true);
//                     u.setCreatedAt(LocalDateTime.now());
//                     mongoTemplate.save(u);
//                 }
//                 System.out.println("Full users seeded.");

//                 // 3 Users with ONLY Bank (No Wallet yet)
//                 for (int i = 4; i <= 6; i++) {
//                     User u = new User();
//                     u.setName("Bank Only User " + i);
//                     u.setMobile("0162222222" + i); // Different series
//                     u.setEmail("bankuser" + i + "@test.com");
//                     u.setPassword(passwordEncoder.encode("123456"));
//                     u.setAccount("202300000" + i);
//                     u.setNid("199800000000" + i);
//                     u.setDOB("1998-01-01"); // Added DOB
//                     u.setAddress("Chittagong, Bangladesh");
//                     u.setBalance(100000.0);
//                     u.setWalletBalance(0.0);
//                     u.setAccountType("SAVINGS");
//                     u.setUserRole("USER");
//                     u.setIsActive(true);
//                     u.setCreatedAt(LocalDateTime.now());
//                     mongoTemplate.save(u);
//                 }

//                 System.out.println("---------- DATA SEEDING COMPLETE (Users=7) ----------");

//             } catch (Exception e) {
//                 System.err.println("---------- DATA SEEDER FAILED ----------");
//                 e.printStackTrace();
//             }
//         };
//     }
// }
