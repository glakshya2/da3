package com.library.beans;

import com.library.dao.MemberDao;
import com.library.entities.Member;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class MemberBean implements Serializable {

    private Member newMember;
    private List<Member> allMembers;
    private MemberDao memberDao;

    @PostConstruct
    public void init() {
        memberDao = new MemberDao();
        newMember = new Member();
        loadAllMembers();
    }

    public void loadAllMembers() {
        allMembers = memberDao.findAll();
    }

    public String addMember() {
        memberDao.save(newMember);
        newMember = new Member(); // Reset for next entry
        loadAllMembers(); // Refresh the list
        return "addMember?faces-redirect=true"; // Redirect to prevent double submission
    }

    // Getters and Setters
    public Member getNewMember() {
        return newMember;
    }

    public void setNewMember(Member newMember) {
        this.newMember = newMember;
    }

    public List<Member> getAllMembers() {
        return allMembers;
    }

    public void setAllMembers(List<Member> allMembers) {
        this.allMembers = allMembers;
    }
}
