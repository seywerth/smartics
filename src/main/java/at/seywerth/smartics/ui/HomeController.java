package at.seywerth.smartics.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * mvc controller for application.
 * 
 * @author Raphael Seywerth
 *
 */
@Controller
public class HomeController {

   @GetMapping(value = "/")
   public String index() {
      return "static/index";
   }

   @GetMapping(value = "/*.jsx")
   public String page() {
      return "static/index";
   }
}