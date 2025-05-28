package com.boanni_back.project.user.repository;

import com.boanni_back.project.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
