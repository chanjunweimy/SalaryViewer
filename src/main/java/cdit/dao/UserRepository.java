package cdit.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import cdit.model.User;

public interface UserRepository extends JpaRepository<User, String> {
}
