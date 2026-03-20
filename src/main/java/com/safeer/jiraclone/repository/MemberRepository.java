package com.safeer.jiraclone.repository;

import com.safeer.jiraclone.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, String > {

    List<Member> findAllByUserId(String userId);

    Member findByUserIdAndWorkspaceId(String userId, String workspaceId);

    List<Member> findAllByWorkspaceId(String workspaceId);
}
