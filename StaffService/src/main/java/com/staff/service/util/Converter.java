/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.staff.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author PSB
 */
public class Converter {

     public static <T> void toJson(OutputStream os, T obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(os, obj);
        } catch (IOException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public static <T> String toJson(T obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static <T> T toObj(InputStream is, Class<T> objClass) {
        T obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            obj = mapper.readValue(is, objClass);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    public static <T> T toObj(String is, Class<T> objClass) {
        T obj = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            obj = mapper.readValue(is, objClass);
        } catch (IOException ex) {
            Logger.getLogger(Converter.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return obj;
    }
    

}
