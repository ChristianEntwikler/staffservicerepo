/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.staff.service.repositories;

import com.staff.service.models.StaffRequestDto;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author PSB
 */
public interface ListStaff extends JpaRepository<StaffRequestDto, Long>{
    
}
