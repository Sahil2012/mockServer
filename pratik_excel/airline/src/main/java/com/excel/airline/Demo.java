package com.excel.airline;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Demo {
    
    @GetMapping("/p")
    public String getMethodName() {
        return "Hi";
    }
    
}
