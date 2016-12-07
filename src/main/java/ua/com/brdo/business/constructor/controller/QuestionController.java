package ua.com.brdo.business.constructor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import ua.com.brdo.business.constructor.model.Option;
import ua.com.brdo.business.constructor.model.Question;
import ua.com.brdo.business.constructor.model.QuestionOption;
import ua.com.brdo.business.constructor.service.OptionService;
import ua.com.brdo.business.constructor.service.QuestionOptionService;
import ua.com.brdo.business.constructor.service.QuestionService;

import static java.lang.Long.parseLong;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/api/questions", produces = APPLICATION_JSON_VALUE)
public class QuestionController {

    private QuestionService questionService;
    private OptionService optionService;
    private QuestionOptionService questionOptionService;

    @Autowired
    public QuestionController(QuestionService questionService, OptionService optionService, QuestionOptionService questionOptionService) {
        this.questionService = questionService;
        this.optionService = optionService;
        this.questionOptionService = questionOptionService;
    }

    @ModelAttribute
    private Question lookupQuestionById(@PathVariable(value = "questionId", required = false) Long id) {
        Question question = null;
        if (id != null) {
            question = questionService.findById(id);
        }
        return question;
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity createQuestion(@Valid @RequestBody Question question) {
        Question createdQuestion = questionService.create(question);
        URI location = ServletUriComponentsBuilder
                .fromUriString("questions").path("/{id}")
                .buildAndExpand(question.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdQuestion);
    }

    @PostMapping(path = "/list", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity createQuestions(@Valid @RequestBody List<Question> questions) {
        if (questions != null) {
            questions.forEach(question -> {
                if (question != null) {
                    questionService.create(question);
                }
            });
        }
//        URI location = ServletUriComponentsBuilder  // TODO Decide how to return location for created list of questions - maybe it should be in Questionnaire controller and return location of new questionnaire
//                .fromUriString("questions").path("/{id}")
//                .buildAndExpand(updatedQuestion.getId())
//                .toUri();
        return ResponseEntity.status(CREATED).build();
    }

    @GetMapping
    public List<Question> listQuestions() {
        return questionService.findAll();
    }

    @GetMapping(path = "/{questionId}")
    public Question getQuestion(@ModelAttribute Question question) {
        return question;
    }

    @PutMapping(path = "/{questionId}", consumes = APPLICATION_JSON_VALUE)
    public Question updateQuestion(@ModelAttribute Question question, @Valid @RequestBody Question updatedQuestion) {
       Long questionId = question.getId();
        updatedQuestion.setId(Long.valueOf(questionId));
        if (updatedQuestion.getQuestionOptions() != null) {
            questionOptionService.deleteByQuestionId(updatedQuestion.getId());
        }
        return questionService.update(updatedQuestion);
    }

    @DeleteMapping(path = "/{questionId}")
    public ResponseEntity deleteQuestion(@ModelAttribute Question question) {
        questionService.delete(question);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(path = "/{questionId}/options", consumes = APPLICATION_JSON_VALUE) //TODO: add controller to consume list of options for given updatedQuestion
    public ResponseEntity createOption(@ModelAttribute Question question, @Valid @RequestBody Option option) {
        Option createdOption = optionService.create(option);
        questionService.addOption(question, createdOption);
        question = questionService.update(question);
        URI location = ServletUriComponentsBuilder
                .fromUriString("questions/{questionId}/options").path("/{id}")
                .buildAndExpand(question.getId(), option.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdOption);
    }

    @GetMapping(path = "/{questionId}/options")
    public List<Option> listOptions(@ModelAttribute Question question) {
        Long questionId = question.getId();
        List<QuestionOption> questionOptions = questionOptionService.findByQuestionId(questionId);
        List<Option> options = new ArrayList<>();
        questionOptions.forEach(questionOption ->
                options.add(optionService
                        .findById(questionOption
                                .getOption().getId())));
        return options;
    }

    @GetMapping(path = "/{questionId}/options/{optionId}")
    public Option getOption(@ModelAttribute Question question, @PathVariable Long optionId) {
        long questionId = question.getId();
        QuestionOption questionOption = questionOptionService.findByQuestionIdAndOptionId(questionId, optionId);
        return questionOption.getOption();
    }

    @PutMapping(path = "/{questionId}/options/{optionId}", consumes = APPLICATION_JSON_VALUE)
    public Option updateOption(@ModelAttribute Question question, @PathVariable Long optionId, @Valid @RequestBody Option modifiedOption) {
        long questionId = question.getId();
        Option option = optionService.findById(optionId);
        questionService.deleteOption(question, option);
        questionOptionService.deleteByQuestionIdAndOptionId(questionId, optionId);
        Option updatedOption = optionService.create(modifiedOption);
        questionService.addOption(question, updatedOption);
        questionService.update(question);
        return updatedOption;
    }

    @DeleteMapping(path = "/{questionId}/options/{optionId}")
    public ResponseEntity deleteOption(@ModelAttribute Question question, @PathVariable Long optionId) {
        Long questionId = question.getId();
        questionOptionService.deleteByQuestionIdAndOptionId(questionId, optionId);
        return ResponseEntity
                .noContent()
                .build();
    }
}