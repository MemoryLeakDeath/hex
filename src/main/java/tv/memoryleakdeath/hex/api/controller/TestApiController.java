package tv.memoryleakdeath.hex.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class TestApiController {
    private static final Logger logger = LoggerFactory.getLogger(TestApiController.class);

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> view(HttpServletRequest request) {
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

}
