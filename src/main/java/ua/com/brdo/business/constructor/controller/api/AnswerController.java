package ua.com.brdo.business.constructor.controller.api;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;
import ua.com.brdo.business.constructor.model.Answer;
import ua.com.brdo.business.constructor.model.Business;
import ua.com.brdo.business.constructor.model.BusinessType;
import ua.com.brdo.business.constructor.model.Question;
import ua.com.brdo.business.constructor.model.Questionnaire;
import ua.com.brdo.business.constructor.model.Stage;
import ua.com.brdo.business.constructor.model.User;
import ua.com.brdo.business.constructor.service.impl.AnswerService;
import ua.com.brdo.business.constructor.service.impl.BusinessService;
import ua.com.brdo.business.constructor.service.impl.StageService;
import ua.com.brdo.business.constructor.utils.HtmlRender;
import ua.com.brdo.business.constructor.utils.Mailer;

@RestController
@RequestMapping(value = "/api/business", produces = APPLICATION_JSON_VALUE)
public class AnswerController {

    private AnswerService answerService;
    private BusinessService businessService;
    private StageService stageService;
    private HtmlRender htmlRender;
    private Mailer mailer;

    @Autowired
    public AnswerController(AnswerService answerService, BusinessService businessService, StageService stageService, HtmlRender htmlRender, Mailer mailer) {
        this.answerService = answerService;
        this.businessService = businessService;
        this.stageService = stageService;
        this.htmlRender = htmlRender;
        this.mailer = mailer;
    }

    @ApiIgnore
    @ModelAttribute
    private Business lookupBusinessById(@PathVariable(value = "businessId", required = false) Long businessId, Authentication authentication) {
        Business business = new Business();
        if (businessId != null) {
            business = businessService.findById(businessId);
            businessService.checkBusinessOwnership(business, authentication);
        }
        return business;
    }

    @PostMapping(path = "/{businessId}/answers", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity createAnswer(@ModelAttribute Business business, @Valid @RequestBody Answer answer) {
        answer.setBusiness(business);
        Question question = answerService.getQuestion(answer);
        checkQuestionIsAppropriateToBusiness(question, business);
        if (question.isSingleChoice()) {
            answerService.checkUniqueAnswer(question, business);
        }
        Answer createdAnswer = answerService.create(answer);

        URI location = ServletUriComponentsBuilder
                .fromUriString("/api/business/{businessId}/answers").path("/{id}")
                .buildAndExpand(business.getId(), answer.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdAnswer);
    }

    private void checkQuestionIsAppropriateToBusiness(Question question, Business business) {
        Questionnaire questionnaire = question.getQuestionnaire();
        BusinessType questionnaireBusinessType = questionnaire.getBusinessType();
        BusinessType businessType = business.getBusinessType();
        if (!businessType.equals(questionnaireBusinessType)) {
            throw new IllegalArgumentException("The answered question doesn't correspond to chosen business.");
        }
    }

    @GetMapping(path = "/{businessId}/answers")
    public List<Answer> listAnswersByBusiness(@ModelAttribute Business business, Authentication authentication) {
        return answerService.findByBusiness(business);
    }

    @GetMapping(path = "/{businessId}/answers/{answerId}")
    public Answer getAnswer(@ApiIgnore @ModelAttribute Business business, @PathVariable Long answerId) {
        Long businessId = business.getId();
        return answerService.findByIdAndBusinessId(answerId, businessId);
    }

    @PutMapping(path = "/{businessId}/answers/{answerId}")
    public Answer updateAnswer(@ApiIgnore @ModelAttribute Business business, @PathVariable Long answerId, @Valid @RequestBody Answer updatedAnswer) {
        Long businessId = business.getId();
        Answer persistedAnswer = answerService.findByIdAndBusinessId(answerId, businessId);
        Question persistedQuestion = persistedAnswer.getQuestion();
        Question updatedQuestion = answerService.getQuestion(updatedAnswer);
        if (!persistedQuestion.equals(updatedQuestion)) {
            throw new IllegalArgumentException("You can't update answer to other question.");
        }
        checkQuestionIsAppropriateToBusiness(updatedQuestion, business);
        updatedAnswer.setId(answerId);
        return answerService.update(updatedAnswer);
    }

    @DeleteMapping(path = "/{businessId}/answers/{answerId}")
    public ResponseEntity deleteAnswer(@ApiIgnore @ModelAttribute Business business, @PathVariable Long answerId) {
        Long businessId = business.getId();
        answerService.deleteByIdAndBusinessId(answerId, businessId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping(path = "/{businessId}/flow")
    public List<Stage> listFlow(@ApiIgnore @ModelAttribute Business business) {
        return stageService.findByBusiness(business);
    }

    @GetMapping(path = "/{businessId}/flow/{stageId}")
    public Stage getFlowStage(@ApiIgnore @ModelAttribute Business business, @PathVariable Long stageId) {
        return stageService.findByIdAndBusiness(stageId, business);
    }

    @GetMapping(path = "/{businessId}/send")
    public void sendFlowToEmail(@ApiIgnore @ModelAttribute Business business) {
        List<Stage> flow = stageService.findByBusiness(business);
        User user = business.getUser();
        String userEmail = user.getEmail();
        String message = htmlRender.renderFlow(flow, business);
        mailer.send(userEmail, "Етапи відкриття бізнесу", message);
    }
}
