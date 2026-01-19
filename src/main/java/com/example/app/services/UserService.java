package com.example.app.services;

import com.example.app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public boolean registration(User user) {
        try {
            if (user != null) {
                Query query = new Query(Criteria.where("account").is(user.getAccount()));
                User existingUser = mongoTemplate.findOne(query, User.class);

                if (existingUser != null) {
                    existingUser.setName(user.getName());
                    existingUser.setMobile(user.getMobile());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setNid(user.getNid());
                    existingUser.setDOB(user.getDOB());

                    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));

                    existingUser.setIsActive(true);
                    existingUser.setWalletBalance(0.0);

                    mongoTemplate.save(existingUser);
                    return true;
                } else {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    mongoTemplate.insert(user);
                    return true;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public User login(String mobile, String password) {
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null) {
                if (passwordEncoder.matches(password, user.getPassword())) {
                    return user;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public User deleteUser(String mobile) {
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile));
            mongoTemplate.remove(query, User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public User adminLogin(String mobile, String password) {
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile).and("userRole").is("ADMIN"));
            User admin = mongoTemplate.findOne(query, User.class);

            if (admin != null) {
                if (passwordEncoder.matches(password, admin.getPassword())) {
                    return admin;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean changePassword(String mobile, String newPassword) {
        try {
            String encryptedPassword = passwordEncoder.encode(newPassword);

            Query query = new Query(Criteria.where("mobile").is(mobile));
            Update update = new Update().set("password", encryptedPassword);
            mongoTemplate.updateFirst(query, update, User.class);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkAccount(String account) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null) {
                return account.equals(user.getAccount());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public User getUserByAccount(String account) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            return mongoTemplate.findOne(query, User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public boolean checkAccountOnline(String mobile) {
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null) {
                return mobile.equals(user.getMobile());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String[] userInfo(String mobile, MongoTemplate mongoTemplate) {
        String[] info = new String[6];
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null) {
                info[0] = user.getName();
                info[1] = user.getMobile();
                info[2] = user.getAccount();
                info[3] = user.getEmail();
                info[4] = user.getDOB();
                info[5] = user.getNid();
                return info;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean existingAccount(String mobile, String account, MongoTemplate mongoTemplate) {
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null) {
                return account.equals(user.getAccount());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean changePassword(String mobile, String newPassword, MongoTemplate mongoTemplate) {
        try {
            org.springframework.security.crypto.password.PasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
            String encryptedPassword = encoder.encode(newPassword);

            Query query = new Query(Criteria.where("mobile").is(mobile));
            Update update = new Update().set("password", encryptedPassword);
            mongoTemplate.updateFirst(query, update, User.class);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void createOnlineBankingAccount(String account, String mobile, double balance,
            MongoTemplate mongoTemplate) {
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile));
            Update update = new Update().set("account", account).set("walletBalance", balance);
            mongoTemplate.updateFirst(query, update, User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static double getBalanceAccount(String account, MongoTemplate mongoTemplate) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null && user.getBalance() != null) {
                return user.getBalance();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public double getBalanceAccount(String account) {
        return getBalanceAccount(account, mongoTemplate);
    }

    public static double getBalanceOnline(String wallet, MongoTemplate mongoTemplate) {
        try {
            Query query = new Query(Criteria.where("mobile").is(wallet));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null && user.getWalletBalance() != null) {
                return user.getWalletBalance();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public double getBalanceOnline(String wallet) {
        return getBalanceOnline(wallet, mongoTemplate);
    }

    public static boolean verifyPin(String password, String mobile, MongoTemplate mongoTemplate) {
        try {
            Query query = new Query(Criteria.where("mobile").is(mobile));
            User user = mongoTemplate.findOne(query, User.class);

            if (user != null) {
                org.springframework.security.crypto.password.PasswordEncoder encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                return encoder.matches(password, user.getPassword());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static void chequeApply(String account, String applied, int page, int chequeBook, String name,
            MongoTemplate mongoTemplate) {
        try {
            org.bson.Document cheque = new org.bson.Document();
            cheque.append("account", account);
            cheque.append("page", page);
            cheque.append("chequeBook", chequeBook);
            cheque.append("applied", applied);
            cheque.append("name", name);
            cheque.append("status", "Pending");

            mongoTemplate.insert(cheque, "cheque");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean lastApplied(String account, MongoTemplate mongoTemplate) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            org.bson.Document cheque = mongoTemplate.findOne(query, org.bson.Document.class, "cheque");

            if (cheque != null) {
                String acc = cheque.getString("account");
                return acc.equals(account);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static List<User> getUserList(MongoTemplate mongoTemplate) {
        List<User> userList = new ArrayList<>();
        try {
            userList = mongoTemplate.findAll(User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public List<User> getUserList() {
        return getUserList(mongoTemplate);
    }

    public static List<User> getChequeList(MongoTemplate mongoTemplate) {
        List<User> userList = new ArrayList<>();
        try {
            Query query = new Query();
            List<org.bson.Document> cheques = mongoTemplate.find(query, org.bson.Document.class, "cheque");

            for (org.bson.Document cheque : cheques) {
                String name = cheque.getString("name");
                String account = cheque.getString("account");
                String page = String.valueOf(cheque.get("page"));
                String quantity = String.valueOf(cheque.get("chequeBook"));
                String applied = cheque.getString("applied");
                String status = cheque.getString("status");
                if (status == null)
                    status = "Pending";

                User user = new User();
                user.setName(name);
                user.setId(account);
                user.setNid(page);
                user.setEmail(quantity);
                user.setAddress(applied);
                user.setAccountType(status);

                userList.add(user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userList;
    }

    public User deleteCheque(String account) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            mongoTemplate.remove(query, "cheque");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void updateChequeStatus(String account, String status) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            Update update = new Update().set("status", status);
            mongoTemplate.updateFirst(query, update, "cheque");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
