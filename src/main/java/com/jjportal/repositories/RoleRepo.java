package com.jjportal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjportal.entites.Role;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

}
