/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.staff.service.util;

import com.staff.service.config.TokenAuthService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;


/**
 *
 * @author PSB
 */
public class RequestValidator {
    
    public Map<String, String> validateHeader(HttpHeaders headers, String SECRET, String usern, String pwd) {
       System.out.println("Header validation ongoing..............");

        String responseCode = "96";
        String responseMessage = "";

        String Authorize = headers.getFirst("Authorization");

        if(Authorize !=null && !Authorize.isEmpty())
        {
         Map<String, String> auth = new TokenAuthService().getAuthentication(Authorize, SECRET);   
         
         if((auth.get("user")!=null && auth.get("user").equalsIgnoreCase(usern)) && (auth.get("pwds")!=null && auth.get("pwds").equalsIgnoreCase(pwd)))
         {
             responseCode = "00";
         }
        }

        Map resp =  new HashMap<String, String>();
       resp.put("responseCode", responseCode); 
       resp.put("responseMessage", responseMessage);
       
       System.out.println("responseCode: " + responseCode);
       System.out.println("responseMessage: " + responseMessage);
       
       return resp;
    }
    
    public Map<String, String> viewLoginUser(HttpHeaders headers, String SECRET, String usern, String pwd) {
       System.out.println("Header validation ongoing..............");

        String loginusername = "96";
        String loginpassword = "";

        String Authorize = headers.getFirst("Authorization");

        if(Authorize !=null && !Authorize.isEmpty())
        {
         Map<String, String> auth = new TokenAuthService().getAuthentication(Authorize, SECRET);   
         
         if((auth.get("user")!=null && auth.get("user").equalsIgnoreCase(usern)) && (auth.get("pwds")!=null && auth.get("pwds").equalsIgnoreCase(pwd)))
         {
             loginusername = auth.get("user");
             loginpassword = auth.get("pwds");
         }
        }

        Map resp =  new HashMap<String, String>();
       resp.put("username", loginusername); 
       resp.put("password", loginpassword);

       return resp;
    }
}
