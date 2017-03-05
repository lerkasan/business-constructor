package ua.com.brdo.business.constructor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import ua.com.brdo.business.constructor.utils.HtmlRender;
import ua.com.brdo.business.constructor.utils.MessageResource;

@RestController
public class ErrorPageController implements ErrorController {

    private static final String PATH = "/error";

    private HtmlRender htmlRender;
    private MessageResource messageResource;

    @Autowired
    public ErrorPageController(ErrorAttributes errorAttributes, HtmlRender htmlRender, MessageResource messageResource) {
        this.htmlRender = htmlRender;
        this.messageResource = messageResource;
    }

    @RequestMapping(value = PATH)
    public String error(final HttpServletRequest request) {
        int statusCode = (int) request.getAttribute("javax.servlet.error.status_code");
        String message = messageResource.get(String.valueOf(statusCode));
        return htmlRender.renderError(statusCode, message);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}