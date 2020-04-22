package com.techmania.spboot.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MapTest {

    private static Map<String, Class> testTypes = new ConcurrentHashMap<>();

    static {
        testTypes.put("K", List.class);
    }

    private ResponseEntity<Object> response;

    @BeforeEach
    public void initFormat() throws IOException {
        String file = asString("sample_list_map.json");
        response = new ResponseEntity(new ObjectMapper().readValue(file, testTypes.get("K")), HttpStatus.OK);
    }

    @Test
    public void data() {
        System.out.println(response.getBody());
    }

    @Test
    public void data2() throws IOException {
        String asXML = asString("test.xml");
        String response = new ObjectMapper().readValue(asXML, String.class);
        System.out.println(response);
    }

    private String asString(String fileName) throws IOException {
        return IOUtils.toString(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName)), "UTF-8");
    }
}


