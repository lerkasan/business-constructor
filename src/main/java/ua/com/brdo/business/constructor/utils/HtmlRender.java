package ua.com.brdo.business.constructor.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

import ua.com.brdo.business.constructor.model.Business;
import ua.com.brdo.business.constructor.model.Stage;

@Service
public class HtmlRender {

    private static final String FLOW_TEMPLATE = "flow";
    private static final String ERROR_TEMPLATE = "error";
    private TemplateEngine templateEngine;

    @Autowired
    public HtmlRender(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String renderFlow(List<Stage> stages, Business business) {
        final Context context = new Context();
        context.setVariable("stages", stages);
        context.setVariable("business", business);
        return templateEngine.process(FLOW_TEMPLATE, context);
    }

    public String renderError(int code, String message) {
        final Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("message", message);
        return templateEngine.process(ERROR_TEMPLATE, context);
    }
}
