/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.staff.service.util;

import com.staff.service.models.StaffRequestDto;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 *
 * @author PSB
 */
public class Util {
    public String generateId(String reqCode)
    {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyMMddHHmmss");
                Timestamp timestamp3 = new Timestamp(System.currentTimeMillis());
                String timestampz3=sdf3.format(timestamp3);               
                long number = (long) Math.floor(Math.random() * 9_00_00L) + 1_00_0L;
             String fcinum=reqCode + timestampz3 + number;
             
             return fcinum;
    }
    
    public String CreateUsername(StaffRequestDto req)
    {
        String username = "";
        username = (req.getFirstName().substring(0,1) + req.getSurname()).toUpperCase();
        return username;
    }
    
    public String CreateStaffId(StaffRequestDto req)
    {
        String staffId = "";
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyMMddHHmmss");
        Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
        String timestampz=sdf2.format(timestamp2);
        
        staffId = (req.getFirstName().substring(0, 1) + req.getSurname().substring(0, 1)).toUpperCase() + timestampz ;
        return staffId;
    }
}
