package ee.lhv.tryout.blacklist.controller;

import ee.lhv.tryout.blacklist.service.BlackListCheckerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin
@Controller
public class BlackListController {

    private BlackListCheckerService blackListCheckerService;

    @Autowired
    public BlackListController(BlackListCheckerService blackListCheckerService) {
        this.blackListCheckerService = blackListCheckerService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/matches")
    public String getMatchingNamesFor(@RequestParam(name = "name") String name, Model model) {
        if (StringUtils.isEmpty(name)) {
            model.addAttribute("error", "Name cannot be empty");
        } else {
            model.addAttribute("matches", blackListCheckerService.getMatches(name));
        }
        return "index";
    }
}
