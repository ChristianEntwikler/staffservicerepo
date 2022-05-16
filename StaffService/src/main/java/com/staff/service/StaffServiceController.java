/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.staff.service;

import com.staff.service.config.TokenAuthService;
import com.staff.service.models.ActivityLogDto;
import com.staff.service.models.ResponseDto;
import com.staff.service.models.StaffRequestDto;
import com.staff.service.models.TokenRequestDto;
import com.staff.service.repositories.ChangeSupervisor;
import com.staff.service.repositories.FetchByStaffId;
import com.staff.service.repositories.InsertLog;
import com.staff.service.repositories.InsertStaff;
import com.staff.service.repositories.ListStaff;
import com.staff.service.repositories.RemoveStaff;
import static com.staff.service.util.Converter.toJson;
import com.staff.service.util.RequestValidator;
import com.staff.service.util.Util;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author PSB
 */
@RestController
//@RequestMapping(name="/api/emp")
public class StaffServiceController {
    
    @Value("${security.user.sec}")
    private String SECRET;
    @Value("${security.user.password}")
    private String pwd;
    @Value("${security.user.name}")
    private String usern;
    
    @Autowired InsertStaff addStaff;
    @Autowired InsertLog addLog;
    @Autowired ChangeSupervisor updateSupervisor;
    @Autowired ListStaff fetchStaff;
    @Autowired FetchByStaffId fetchByStaffId;
    @Autowired RemoveStaff deleteStaff;
    
    
    @RequestMapping(value ="/api/generate/token/{username},{password}",produces = "application/json",method=RequestMethod.GET)
    public ResponseEntity<ResponseDto> generateToken(@PathVariable("username") String username, @PathVariable("password") String password, @RequestHeader HttpHeaders headers)
        {     
            System.out.println("usern: " + usern);
            System.out.println("pwd: " + pwd);
            System.out.println("SECRET: " + SECRET);
        String res = "";
        ResponseDto resp = new ResponseDto();
            if(usern.equalsIgnoreCase(username) && pwd.equalsIgnoreCase(password))
        {
            System.out.println("request.getUsername(): " + username);
            System.out.println("request.getPassword(): " + password);
            res = new TokenAuthService().generateToken(username, password, SECRET);   
            resp.setResponseCode("00");
            }
        else
        {
            res = "Invalid credentials provided";
            resp.setResponseCode("01");
        }
            
            resp.setResponseMessage(res);
            return ResponseEntity.ok().body(resp);           
        }
    
    @RequestMapping(value ="/api/generate/token",produces = "application/json",method=RequestMethod.POST)
    public ResponseEntity<ResponseDto> generateToken(@RequestBody TokenRequestDto request, @RequestHeader HttpHeaders headers)
        {     
                 
            System.out.println("usern: " + usern);
            System.out.println("pwd: " + pwd);
            System.out.println("SECRET: " + SECRET);
        String res = "";
        ResponseDto resp = new ResponseDto();
            if(usern.equalsIgnoreCase(request.getUsername()) && pwd.equalsIgnoreCase(request.getPassword()))
        {
            System.out.println("request.getUsername(): " + request.getUsername());
            System.out.println("request.getPassword(): " + request.getPassword());
            
            res = new TokenAuthService().generateToken(request.getUsername(), request.getPassword(), SECRET);   
            resp.setResponseCode("00");
            }
        else
        {
            res = "Invalid credentials provided";
            resp.setResponseCode("01");
        }
            resp.setResponseMessage(res);
            return ResponseEntity.ok().body(resp);           
        }
    
    @RequestMapping(value ="/api/emp/staff/create",produces = "application/json",method=RequestMethod.POST)
    public ResponseEntity<ResponseDto> createStaff(@RequestBody StaffRequestDto request, @RequestHeader HttpHeaders headers)
        {
            Map <String , String> AuthVal = new RequestValidator().validateHeader(headers, SECRET, usern, pwd);
            if(AuthVal.get("responseCode").equalsIgnoreCase("00"))
            {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
            System.out.println("request: " + toJson(request));
            ResponseDto resp = new ResponseDto();
            try
            {
            if((request.getFirstName()!=null && !request.getFirstName().isEmpty()) 
                    && (request.getSurname()!=null && !request.getSurname().isEmpty()) 
              )
            {
                request.setStaffid(new Util().CreateStaffId(request));
                request.setUsername(new Util().CreateUsername(request));
            request.setDateUploaded(timestamp2);
            if(fetchStaff.findAll().stream().filter(p->(p.getStaffid()!=null && p.getStaffid().equalsIgnoreCase(request.getStaffid())) || (p.getUsername()!=null && p.getUsername().equalsIgnoreCase(request.getUsername()))).count() > 0)
            {
            addStaff.save(request);
            
            resp.setResponseCode("00");
            resp.setResponseMessage("Data submitted successfully. Username: " + request.getUsername() + ", StaffId: " + request.getStaffid());
            System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
            try
            {
            ActivityLogDto req = new ActivityLogDto();
            req.setRequestid(new Util().generateId("EM"));
            System.out.println("req.getRequestid(): " + req.getRequestid());
            req.setDescription("Staff " + request.getUsername() + " created");
            req.setRequestType("STAFF CREATION");
            req.setUsername(request.getUsername());
            req.setDateUploaded(timestamp2);
            addLog.save(req);          
            }
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ResponseEntity<ResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
            
            return ResponseEntity.ok().body(resp);
            
            }
            else
            {
                resp.setResponseCode("02");
                resp.setResponseMessage("Duplicate User");
                System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
                return ResponseEntity.ok().body(resp);
            }
            
            }
            else
            {
                resp.setResponseCode("02");
                resp.setResponseMessage("First name and Surname required");
                System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
                return ResponseEntity.ok().body(resp);
            }
            
            }
            catch(DataIntegrityViolationException dup)
		{
			ResponseDto reply = new ResponseDto();
			reply.setResponseCode("94");
			reply.setResponseMessage("Data flagged as duplicate");			
			return new ResponseEntity<ResponseDto>(reply, HttpStatus.OK);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<ResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
            
            }
            else
            {
               return new ResponseEntity<ResponseDto>(HttpStatus.UNAUTHORIZED);
            }


        }
    
    @RequestMapping(value ="/api/emp/staff/remove",produces = "application/json",method=RequestMethod.POST)
    public ResponseEntity<ResponseDto> removeStaff(@RequestBody StaffRequestDto request, @RequestHeader HttpHeaders headers)
        {
            Map <String , String> AuthVal = new RequestValidator().validateHeader(headers, SECRET, usern, pwd);
            if(AuthVal.get("responseCode").equalsIgnoreCase("00"))
            {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
            System.out.println("request: " + toJson(request));
            ResponseDto resp = new ResponseDto();
            try
            {
            if((request.getFirstName()!=null && !request.getFirstName().isEmpty()) 
                    && (request.getSurname()!=null && !request.getSurname().isEmpty()) 
                    && (request.getStaffid()!=null && !request.getStaffid().isEmpty()) 
              )
            {
            if(fetchStaff.findAll().stream().filter(p->p.getSupervisorId()!=null && p.getSupervisorId().equalsIgnoreCase(request.getStaffid())).count() > 0)
            {
            resp.setResponseCode("01");
            resp.setResponseMessage("Staff is a supervisor and cannot be removed.");  
            System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
            }
            else
            {
            deleteStaff.deleteByStaffid(request.getStaffid());
            
            resp.setResponseCode("00");
            resp.setResponseMessage("Staff removed successfully");
            System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
            try
            {
            ActivityLogDto req = new ActivityLogDto();
            req.setRequestid(new Util().generateId("EM"));
            System.out.println("req.getRequestid(): " + req.getRequestid());
            req.setDescription("Staff " + request.getUsername() + " created");
            req.setRequestType("STAFF REMOVAL");
            req.setUsername(request.getUsername());
            req.setDateUploaded(timestamp2);
            addLog.save(req);          
            }
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ResponseEntity<ResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
            }
            
            return ResponseEntity.ok().body(resp);
            }
            else
            {
                resp.setResponseCode("02");
                resp.setResponseMessage("Staff Id, First name and Surname required");
                System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
                return ResponseEntity.ok().body(resp);
            }
            
            }
            catch(DataIntegrityViolationException dup)
		{
			ResponseDto reply = new ResponseDto();
			reply.setResponseCode("94");
			reply.setResponseMessage("Data flagged as duplicate");			
			return new ResponseEntity<ResponseDto>(reply, HttpStatus.OK);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<ResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
            
                }
            else
            {
               return new ResponseEntity<ResponseDto>(HttpStatus.UNAUTHORIZED);
            }

        }

@RequestMapping(value ="/api/emp/staff/update",produces = "application/json",method=RequestMethod.POST)
    public ResponseEntity<ResponseDto> updateStaff(@RequestBody StaffRequestDto request, @RequestHeader HttpHeaders headers)
        {
            Map <String , String> AuthVal = new RequestValidator().validateHeader(headers, SECRET, usern, pwd);
            if(AuthVal.get("responseCode").equalsIgnoreCase("00"))
            {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
            System.out.println("request: " + toJson(request));
            ResponseDto resp = new ResponseDto();
            try
            {
                
            if((request.getStaffid()!=null && !request.getStaffid().isEmpty()) && 
                    (request.getFirstName()!=null && !request.getFirstName().isEmpty()) 
                    && (request.getSurname()!=null && !request.getSurname().isEmpty()) 
              )
            {
                StaffRequestDto requpdt = new StaffRequestDto();
             try
            {
            requpdt = fetchByStaffId.findByStaffid(request.getStaffid());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            
             if(requpdt.getStaffid() != null && !requpdt.getStaffid().isEmpty())
             {
            requpdt.setSupervisorId(request.getSupervisorId());
            updateSupervisor.save(requpdt);
            
            resp.setResponseCode("00");
            resp.setResponseMessage("Data updated successfully");
            System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
            try
            {
            ActivityLogDto req = new ActivityLogDto();
            req.setRequestid(new Util().generateId("EM"));
            System.out.println("req.getRequestid(): " + req.getRequestid());
            req.setDescription("Staff " + request.getUsername() + " updated");
            req.setRequestType("STAFF UPDATED");
            req.setUsername(request.getUsername());
            req.setDateUploaded(timestamp2);
            addLog.save(req);          
            }
		catch(Exception ex)
		{
			ex.printStackTrace();
			return new ResponseEntity<ResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
            
            return ResponseEntity.ok().body(resp);
            }
            else
            {
                resp.setResponseCode("02");
                resp.setResponseMessage("Staff Id, Username, First name and Surname required");
                System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
                return ResponseEntity.ok().body(resp);
            }
             
            }
            else
            {
              resp.setResponseCode("03");
                resp.setResponseMessage("Staff not found");
                System.out.println("resp.getResponseMessage(): " + resp.getResponseMessage());
                return ResponseEntity.ok().body(resp);  
            }
            
            }
            catch(DataIntegrityViolationException dup)
		{
			ResponseDto reply = new ResponseDto();
			reply.setResponseCode("94");
			reply.setResponseMessage("Data flagged as duplicate");			
			return new ResponseEntity<ResponseDto>(reply, HttpStatus.OK);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return new ResponseEntity<ResponseDto>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
                
            }
            else
            {
               return new ResponseEntity<ResponseDto>(HttpStatus.UNAUTHORIZED);
            }

        }

    @RequestMapping(value ="/api/emp/list/employees",produces = "application/json",method=RequestMethod.GET)
    public Page<StaffRequestDto> listStaff(Pageable pageable, @RequestHeader HttpHeaders headers)
        {     
            Map <String , String> AuthVal = new RequestValidator().validateHeader(headers, SECRET, usern, pwd);
            if(AuthVal.get("responseCode").equalsIgnoreCase("00"))
            {
            List<StaffRequestDto> staff = new ArrayList<StaffRequestDto>();
            try
            {
            return fetchStaff.findAll(pageable);
//            resp = accounts.stream().filter(p -> (p.getPregexid()!=null && !p.getPregexid().equals(""))).collect(Collectors.toList());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
//            return ResponseEntity.ok().body(staff);
                //}
                
                }
            else
            {
               return null;
            }
        }
    
    @RequestMapping(value ="/api/emp/list/employees/bysupervisor/{supervisorid}",produces = "application/json",method=RequestMethod.GET)
    public ResponseEntity<List<StaffRequestDto>> listStaffBySupervisor(@PathVariable("supervisorid") String supervisorid,  @RequestHeader HttpHeaders headers)
        {     
            Map <String , String> AuthVal = new RequestValidator().validateHeader(headers, SECRET, usern, pwd);
            if(AuthVal.get("responseCode").equalsIgnoreCase("00"))
            {
            List<StaffRequestDto> resp = new ArrayList<StaffRequestDto>();
            try
            {
            List<StaffRequestDto> staff = fetchStaff.findAll(Sort.by(Sort.Direction.DESC, "dateUploaded"));
            resp = staff.stream().filter(p -> (p.getSupervisorId()!=null && !p.getSupervisorId().isEmpty() && p.getSupervisorId().equals(supervisorid))).collect(Collectors.toList());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            return ResponseEntity.ok().body(resp);
            
            }
            else
            {
               return new ResponseEntity<List<StaffRequestDto>>(HttpStatus.UNAUTHORIZED);
            }
            
        }
    
    @RequestMapping(value ="/api/me",produces = "application/json",method=RequestMethod.GET)
    public ResponseEntity<TokenRequestDto> loginUser(@RequestHeader HttpHeaders headers)
        {     
            Map <String , String> AuthVal = new RequestValidator().validateHeader(headers, SECRET, usern, pwd);
            if(AuthVal.get("responseCode").equalsIgnoreCase("00"))
            {
            TokenRequestDto resp = new TokenRequestDto();
            try
            {
               Map <String , String> viewVal = new RequestValidator().viewLoginUser(headers, SECRET, usern, pwd);
               resp.setUsername(viewVal.get("username"));
               resp.setPassword(viewVal.get("password"));
            
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            return ResponseEntity.ok().body(resp);
            
            }
            else
            {
               return new ResponseEntity<TokenRequestDto>(HttpStatus.UNAUTHORIZED);
            }
            
        }
}
