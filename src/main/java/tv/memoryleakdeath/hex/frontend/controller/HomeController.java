package tv.memoryleakdeath.hex.frontend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.TestDao;

@Controller
@RequestMapping("/")
public class HomeController extends BaseFrontendController {

    @Autowired
    private TestDao testDao;

    @GetMapping("/")
    public String view(HttpServletRequest request, Model model) {
        setPageTitle(request, model, "title.home");
        model.addAttribute("helloMsg", "Hello World!");
        return "index";
    }

    @GetMapping("/test")
    public String testDatabase(HttpServletRequest request, Model model) {
        boolean testDatabaseResult = testDao.testDatabase();
        model.addAttribute("testResults", testDatabaseResult);
        return "test";
    }

    @PostMapping("/testupload")
    public String testUpload(HttpServletRequest request, Model model,
            @RequestParam(name = "file", required = true) MultipartFile file) {
        setLayout(model, "layout/minimal");
        model.addAttribute("fileName", file.getOriginalFilename());
        model.addAttribute("contentType", file.getContentType());
        model.addAttribute("size", file.getSize());
        return "uploadtest";
    }
}
