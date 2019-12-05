package edu.ucsb.cs56.mapache_search;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class BasicErrorController implements ErrorController  {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/404";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/500";
            }
            else if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
                return "errors/401";
            }
        }
        return "errors/error";
    }

    @GetMapping("/login")
    public String loginError() {
        return "errors/notLoggedIn";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}