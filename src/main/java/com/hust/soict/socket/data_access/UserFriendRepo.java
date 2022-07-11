package com.hust.soict.socket.data_access;

import com.hust.soict.socket.domain.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFriendRepo extends JpaRepository<UserFriend, Long> {
    List<UserFriend> findByUserId(Long userId);
}
