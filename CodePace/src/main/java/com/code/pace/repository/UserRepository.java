package com.code.pace.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.code.pace.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer>{

	User findByEmail(String username);

}
