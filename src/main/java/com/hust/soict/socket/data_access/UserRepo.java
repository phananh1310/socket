package com.hust.soict.socket.data_access;

import com.hust.soict.socket.domain.User;
import com.hust.soict.socket.domain.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndPassword(String username, String password);
    Optional<User> findByInGame(String ingame);

    boolean existsUserByUsername(String username);

    boolean existsUserByInGame(String inGame);

    List<User> findByIdIn(List<Long> idList);
}
