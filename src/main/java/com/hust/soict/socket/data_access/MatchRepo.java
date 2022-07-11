package com.hust.soict.socket.data_access;

import com.hust.soict.socket.domain.Matches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepo extends JpaRepository<Matches, Long> {
}
