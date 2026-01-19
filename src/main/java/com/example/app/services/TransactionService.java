package com.example.app.services;

import com.example.app.model.Transaction;
import com.example.app.model.User;
import com.example.app.model.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    public void balanceTransfer(String receiverAccount, double amount) {
        try {
            double receiverBalance = userService.getBalanceAccount(receiverAccount);
            double finalBalance = receiverBalance + amount;

            Query query = new Query(Criteria.where("account").is(receiverAccount));
            Update update = new Update().set("balance", finalBalance);
            mongoTemplate.updateFirst(query, update, User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void balanceTransferOnline(String receiverWallet, double amount) {
        try {
            double receiverBalance = userService.getBalanceOnline(receiverWallet);
            double finalBalance = receiverBalance + amount;

            Query query = new Query(Criteria.where("mobile").is(receiverWallet));
            Update update = new Update().set("walletBalance", finalBalance);
            mongoTemplate.updateFirst(query, update, User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void senderBalanceUpdate(String account, double amount) {
        try {
            if (account == null)
                return;

            double senderBalance = userService.getBalanceAccount(account);
            double finalBalance = senderBalance - amount;

            Query query = new Query(Criteria.where("account").is(account));
            Update update = new Update().set("balance", finalBalance);
            mongoTemplate.updateFirst(query, update, User.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void senderBalanceUpdateOnline(String mobile, double amount) {
        try {
            double senderBalance = userService.getBalanceOnline(mobile);
            double finalBalance = senderBalance - amount;

            Query query = new Query(Criteria.where("mobile").is(mobile));
            Update update = new Update().set("walletBalance", finalBalance);
            mongoTemplate.updateFirst(query, update, User.class);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean utilityAccountCheck(String provider, String account, String type) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            Utility utility = mongoTemplate.findOne(query, Utility.class);

            if (utility != null) {
                boolean providerMatch = utility.getProvider() != null &&
                        utility.getProvider().equalsIgnoreCase(provider);

                boolean typeMatch = utility.getType() != null &&
                        utility.getType().equalsIgnoreCase(type);

                return providerMatch && typeMatch;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean utilityAccountCheck(String provider, String account) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            Utility utility = mongoTemplate.findOne(query, Utility.class);

            if (utility != null) {
                return utility.getProvider() != null &&
                        utility.getProvider().equalsIgnoreCase(provider);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public double utilityBillCheck(String account, String provider, String type) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            Utility utility = mongoTemplate.findOne(query, Utility.class);

            if (utility != null) {
                return utility.getBalance() != null ? utility.getBalance() : 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public void utilityBillPay(String account, String provider, String type, double amount) {
        try {
            Query query = new Query(Criteria.where("account").is(account));
            Utility utility = mongoTemplate.findOne(query, Utility.class);

            if (utility != null) {
                double currentBalance = utility.getBalance() != null ? utility.getBalance() : 0.0;
                double finalBalance = currentBalance + amount;

                Update update = new Update().set("balance", finalBalance);
                mongoTemplate.updateFirst(query, update, Utility.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean transactionHistory(Transaction transaction) {
        try {
            if (transaction != null) {
                mongoTemplate.insert(transaction);
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public List<Transaction> getTransactionList(String account, String mobile) {
        List<Transaction> transactionList = new ArrayList<>();
        try {
            List<Criteria> verifyList = new ArrayList<>();
            if (account != null && !account.isEmpty()) {
                verifyList.add(Criteria.where("sender").is(account));
                verifyList.add(Criteria.where("receiver").is(account));
            }
            if (mobile != null && !mobile.isEmpty()) {
                verifyList.add(Criteria.where("sender").is(mobile));
                verifyList.add(Criteria.where("receiver").is(mobile));
            }

            if (verifyList.isEmpty()) {
                return transactionList;
            }

            Criteria criteria = new Criteria().orOperator(verifyList.toArray(new Criteria[0]));
            Query query = new Query(criteria);
            query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                    "createdAt"));

            transactionList = mongoTemplate.find(query, Transaction.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return transactionList;
    }

    public List<Transaction> getTransactionList(String account) {
        return getTransactionList(account, null);
    }

    public List<Transaction> getAllTransactionList() {
        List<Transaction> transactionList = new ArrayList<>();
        try {
            transactionList = mongoTemplate.findAll(Transaction.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return transactionList;
    }

    public String[] getMonthlyWalletExpense(User loggedUser, String monthName) {
        String[] info = new String[2];
        double monthlyExpense = 0;
        int count = 0;

        try {
            if (loggedUser == null)
                return info;

            List<Transaction> transactions = mongoTemplate.findAll(Transaction.class);

            DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");

            for (Transaction txn : transactions) {
                if (txn.getTransactionDate() != null) {
                    boolean isSender = false;
                    if (txn.getSender().equals(loggedUser.getAccount()) ||
                            txn.getSender().equals(loggedUser.getMobile())) {
                        isSender = true;
                    }

                    if (isSender) {
                        String txnMonth = txn.getTransactionDate().format(monthFormatter);
                        String txnType = txn.getType().toLowerCase();

                        if (txnMonth.equalsIgnoreCase(monthName) &&
                                (txnType.equals("utility") || txnType.equals("recharge") ||
                                        txnType.equals("gas bill") || txnType.equals("electricity bill") ||
                                        txnType.equals("wallet to bank") || txnType.equals("wallet to wallet"))) {
                            monthlyExpense += txn.getAmount();
                            count++;
                        }
                    }
                }
            }

            info[0] = String.valueOf(monthlyExpense);
            info[1] = String.valueOf(count);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return info;
    }

    public String[] getDailyWalletExpense(User loggedUser) {
        String[] info = new String[2];
        double monthlyExpense = 0;
        int count = 0;

        try {
            if (loggedUser == null)
                return info;

            LocalDateTime today = LocalDateTime.now();

            List<Transaction> transactions = mongoTemplate.findAll(Transaction.class);

            for (Transaction txn : transactions) {
                if (txn.getTransactionDate() != null) {
                    boolean isSender = false;
                    // Check if user is sender by Account OR Mobile
                    if (txn.getSender().equals(loggedUser.getAccount()) ||
                            txn.getSender().equals(loggedUser.getMobile())) {
                        isSender = true;
                    }

                    if (isSender) {
                        LocalDateTime txnDate = txn.getTransactionDate();
                        String txnType = txn.getType().toLowerCase();

                        if (txnDate.toLocalDate().equals(today.toLocalDate()) &&
                                (txnType.equals("utility") || txnType.equals("recharge") ||
                                        txnType.equals("gas bill") || txnType.equals("electricity bill") ||
                                        txnType.equals("wallet to bank") || txnType.equals("wallet to wallet"))) {
                            monthlyExpense += txn.getAmount();
                            count++;
                        }
                    }
                }
            }

            info[0] = String.valueOf(monthlyExpense);
            info[1] = String.valueOf(count);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return info;
    }

    public String[] getMonthlyAccountExpense(User loggedUser, String monthName) {
        String[] info = new String[2];
        double monthlyExpense = 0;
        int count = 0;

        try {
            if (loggedUser == null)
                return info;

            List<Transaction> transactions = mongoTemplate.findAll(Transaction.class);
            DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");

            for (Transaction txn : transactions) {
                if (txn.getTransactionDate() != null &&
                        txn.getSender().equals(loggedUser.getAccount())) {

                    String txnMonth = txn.getTransactionDate().format(monthFormatter);
                    String txnType = txn.getType().toLowerCase();

                    if (txnMonth.equalsIgnoreCase(monthName) &&
                            (txnType.equals("add to wallet") || txnType.equals("bank to bank") ||
                                    txnType.equals("bank to wallet"))) {
                        monthlyExpense += txn.getAmount();
                        count++;
                    }
                }
            }

            info[0] = String.valueOf(monthlyExpense);
            info[1] = String.valueOf(count);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return info;
    }

    public String[] getDailyAccountExpense(User loggedUser) {
        String[] info = new String[2];
        double monthlyExpense = 0;
        int count = 0;

        try {
            if (loggedUser == null)
                return info;

            LocalDateTime today = LocalDateTime.now();
            List<Transaction> transactions = mongoTemplate.findAll(Transaction.class);

            for (Transaction txn : transactions) {
                if (txn.getTransactionDate() != null &&
                        txn.getSender().equals(loggedUser.getAccount())) {

                    LocalDateTime txnDate = txn.getTransactionDate();
                    String txnType = txn.getType().toLowerCase();

                    if (txnDate.toLocalDate().equals(today.toLocalDate()) &&
                            (txnType.equals("add to wallet") || txnType.equals("bank to bank") ||
                                    txnType.equals("bank to wallet"))) {
                        monthlyExpense += txn.getAmount();
                        count++;
                    }
                }
            }

            info[0] = String.valueOf(monthlyExpense);
            info[1] = String.valueOf(count);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return info;
    }

    public String generateRefID() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder refID = new StringBuilder("TXN-");
        for (int i = 0; i < 7; i++) {
            int index = (int) (Math.random() * characters.length());
            refID.append(characters.charAt(index));
        }
        return refID.toString();
    }
}
