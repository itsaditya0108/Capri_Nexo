package com.company.usermicroservice.repository;

import com.company.usermicroservice.entity.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDetails, String> {

    @Query("SELECT u FROM UserDetails u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<UserDetails> searchByNameLike(@Param("name") String name);
}
