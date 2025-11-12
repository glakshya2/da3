package com.library.dao;

import com.library.entities.Member;

public class MemberDao extends AbstractJpaDao<Member, Long> {
    public MemberDao() {
        super();
    }

    // You can add specific Member-related DAO methods here if needed
}
