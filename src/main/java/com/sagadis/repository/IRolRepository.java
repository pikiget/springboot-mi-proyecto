package com.sagadis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sagadis.model.Rol;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Integer>{

}
