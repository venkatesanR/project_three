package com.techmania.spboot.controllers;

import com.techmania.common.util.FileUtl;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

@RestController
public class UserController {
    @RequestMapping("/apex/api/query")
    public String greet(@RequestParam("filtername") String filtername) {
        System.out.println("Requesting data for filter name: " + filtername);
        String response = null;
        try {
            response = FileUtl.readFile(FileUtl.locateResourceFile(getClass(), "query-response.xml"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return response;
    }

}