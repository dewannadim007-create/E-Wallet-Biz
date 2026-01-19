package com.example.app.services;

import com.example.app.model.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BranchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Branch> getBranchList() {
        List<Branch> branchList = new ArrayList<>();
        try {
            branchList = mongoTemplate.findAll(Branch.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return branchList;
    }

    public void saveBranch(Branch branch) {
        if (branch != null) {
            mongoTemplate.save(branch);
        }
    }

    public void deleteBranch(String id) {
        if (id != null) {
            Branch branch = mongoTemplate.findById(id, Branch.class);
            if (branch != null) {
                mongoTemplate.remove(branch);
            }
        }
    }

    public Branch getBranchById(String id) {
        if (id != null) {
            return mongoTemplate.findById(id, Branch.class);
        }
        return null;
    }
}
