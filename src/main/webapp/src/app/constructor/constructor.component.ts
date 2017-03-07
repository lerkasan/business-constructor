import {Component, OnInit} from '@angular/core';
import {Question} from '../model/question';
import {Option} from '../model/option';
import {QuestionService} from '../service/questions.service';
import {Procedure} from '../model/procedure';
import {ProcedureService} from '../service/procedure.service';
import {BusinessType} from '../model/business.type';
import {Questionnaire} from '../model/questionnaire';
import {BusinessTypeService} from '../service/business.type.service';
import {Response} from '@angular/http';

@Component({
  selector: 'brdo-constructor-panel',
  templateUrl: './constructor.component.html',
  styleUrls: ['./constructor.component.scss']

})

export class ConstructorComponent implements OnInit {

  ERROR_MSG_FIRST_SAVE_FIELD = 'Поле повинно бути заповнене і збережено!';
  ERROR_MSG_EMPTY_FIELD = 'Поле не повинно бути пустим!';
  STYLE_WRONG_SUBMISSION = 'wrongSubmission';
  STYLE_SUCCESS_SUBMISSION = 'successSubmission';
  ATTACHED_QUESTION = 'Приеднати питання';
  ATTACHED_PROCEDURE = 'Приеднати картку';

  canSubmit: boolean;
  optionIndexError: number;
  attachedProcedure: string;
  attachedQuestion: string;
  errorMessage: string;
  errorOptionMessage: string;
  selectedOption: Option;
  selectedQuestion: Question;
  selectedQuestionText: string;
  selectedOptionTitle: string;
  questions: Question[];
  procedures: Procedure[];
  businessType: BusinessType;
  questionnaire: Questionnaire;
  wrongBusinessType = false;
  wrongQuestionnaire = false;
  wrongQuestion = false;
  successQuestionnaire = false;
  successBusinessType = false;
  successQuestionLink = false;
  successProcedureLink = false;
  businessTypes: BusinessType[];
  businessTypeTitleClass = '';
  businessTypeKvedClass = '';
  questionnaireClass = '';
  questionFieldIndexWithChange: number;
  optionFieldIndexWithChange: number;
  questionOptionFieldIndexWithChange: number;
  inputTypeFieldIndexWithChange: number;
  quesIndex: number;
  kvedsSelect: Select[];
  proceduresSelect: Select[];
  questionsSelect: Select[];
  linkedProcedure: string;
  linkedQuestion: string;
  some = false;

  inputType = [
    {value: 'SINGLE_CHOICE'},
    {value: 'MULTI_CHOICE'}
  ];

  constructor(private questionService: QuestionService,
              private procedureService: ProcedureService,
              private businessTypeService: BusinessTypeService) {
  }

  ngOnInit() {
    this.procedures = [];
    this.businessType = new BusinessType();
    this.businessType.title = '';
    this.businessType.codeKved = '';
    this.questionnaire = new Questionnaire();
    this.questionnaire.title = '';
    this.getBusinessTypes();
    this.getProcedure();
    this.errorOptionMessage = '';
    this.optionIndexError = 0.1;
    this.canSubmit = false;
    this.linkedProcedure = this.ATTACHED_PROCEDURE;
    this.linkedQuestion = this.ATTACHED_QUESTION;
  }

  onSelectQuestion(question: Question): void {
    this.selectedQuestion = question;
    this.selectedQuestionText = question.text;
  }

  onSelectOption(option: Option): void {
    if (option.nextQuestion !== undefined) {
      this.linkedQuestion = this.findeQuestionById(option.nextQuestion.id).text;
    }
    if (option.procedure !== undefined) {
      this.linkedProcedure = this.findeProcedureById(option.procedure.id).name;
    }
    if (option.nextQuestion === undefined) {
      this.linkedQuestion = this.ATTACHED_QUESTION;
    }
    if (option.procedure === undefined) {
      this.linkedProcedure = this.ATTACHED_PROCEDURE;
    }
    this.selectedOption = option;
    this.selectedOptionTitle = option.title;
    if (option.nextQuestion !== undefined) {
      for (let question of this.questions) {
        if (question.id === option.nextQuestion.id) {
          this.attachedQuestion = question.text;
        }
      }
    } else {
      this.attachedQuestion = this.ATTACHED_QUESTION;
    }
    if (option.procedure !== undefined) {
      for (let procedure of this.procedures) {
        if (procedure.id === option.procedure.id) {
          this.attachedProcedure = procedure.name;
        }
      }
    } else {
      this.attachedProcedure = this.ATTACHED_PROCEDURE;
    }
  }

  addQuestion(): void {
    this.resetErrorStatus();
    this.canSubmit = false;
    if (this.questionnaire.id === undefined) {
      this.errorMessage = this.ERROR_MSG_FIRST_SAVE_FIELD;
      this.questionnaireClass = this.STYLE_WRONG_SUBMISSION;
      this.wrongQuestionnaire = true;
      return;
    }
    if (this.businessType.id === undefined) {
      this.errorMessage = this.ERROR_MSG_FIRST_SAVE_FIELD;
      this.businessTypeTitleClass = this.STYLE_WRONG_SUBMISSION;
      this.businessTypeKvedClass = this.STYLE_WRONG_SUBMISSION;
      this.wrongBusinessType = true;
    }
    if (this.questions === undefined) {
      this.questions = [this.newQuestion()];
    } else {
      this.questions.push(this.newQuestion());
    }
  }

  deleteLastQuestion(): void {
    this.questions.pop();
  }

  newQuestion() {
    let option = new Option();
    option.title = '';
    let question = new Question();
    question.text = '';
    question.questionnaire = new Id(this.questionnaire.id);
    question.options = [option];
    question.inputType = this.inputType[0].value;
    return question;
  }

  newOption(): Option {
    let option = new Option;
    option.title = '';
    return option;
  }

  changeInputType(question: Question): void {
    if (question.inputType === this.inputType[0].value) {
      question.inputType = this.inputType[1].value;
    } else {
      question.inputType = this.inputType[0].value;
    }
    for (let i = 0; i < question.options.length; i++) {
      if (question.options[i].title === undefined || question.options[i].title === '') {
        return;
      }
    }
    if (question.text === undefined || question.text === '') {
      return;
    }

    let elementNumber = this.questions.indexOf(question);
    if (question.id !== undefined) {
      this.questionService.updateQuestion(question)
        .subscribe(
          (response: Response) => {
            if (response.status === 200) {
              this.inputTypeFieldIndexWithChange = elementNumber;
              this.resetStatusSubmissionWithDelay();
              let resQuestion = response.json() as Question;
              for (let locOption of this.questions[elementNumber].options) {
                for (let resOption of resQuestion.options) {
                  if (locOption.title === resOption.title) {
                    locOption.id = resOption.id;
                  }
                }
              }
            }
          },
          error => console.log(<any>error)
        );
    }
  }

  addOption(options: Option[]): void {
    options.push(this.newOption());
    this.canSubmit = false;
  }

  deleteLastOption(options: Option[]): void {
    options.pop();
  }

  saveQuestion(question: Question): void {
    for (let option of question.options) {
      if (option.title === '' || option.title === undefined) {
        return;
      }
    }
    if (question.text === '' || question === undefined) {
      return;
    }
    if (question.text === this.selectedQuestionText) {
      return;
    }
    if (question.id !== undefined) {
      this.questionService.updateQuestion(question)
        .subscribe(
          (response: Response) => {
            if (response.status === 200) {
              let elementNumber = this.questions.indexOf(question);
              this.questionFieldIndexWithChange = elementNumber;
              this.resetStatusSubmissionWithDelay();
              this.resetErrorStatus();
              let resQuestion = response.json() as Question;
              for (let locOption of this.questions[elementNumber].options) {
                for (let resOption of resQuestion.options) {
                  if (locOption.title === resOption.title) {
                    locOption.id = resOption.id;
                  }
                }
              }
            }
          },
          error => console.log(<any>error)
        );
      return;
    }
  }

  saveOption(question: Question, option: Option): void {
    let elementNumber = this.questions.indexOf(question);
    for (let optione of question.options) {
      if (optione.title === undefined || optione.title === '') {
        this.optionIndexError = optione.id;
        this.errorOptionMessage = 'Поле не повинно бути пустим!';
        this.resetStatusSubmissionWithDelay();
        return;
      }
    }
    if (this.selectedOptionTitle === option.title) {
      return;
    }
    if (question.text === undefined || question.text === '') {
      return;
    }
    if (question.id === undefined) {
      this.questionService.createQuestion(question)
        .subscribe(
          (response: Response) => {
            if (response.status === 201) {
              let optionNumber = question.options.indexOf(option);
              let questionNumber = this.questions.indexOf(question);
              this.questionFieldIndexWithChange = questionNumber;
              this.optionFieldIndexWithChange = optionNumber;
              this.questionOptionFieldIndexWithChange = questionNumber;
              this.inputTypeFieldIndexWithChange = questionNumber;
              this.resetStatusSubmissionWithDelay();
              this.resetErrorStatus();
              let resQuestion = response.json() as Question;
              this.questions[elementNumber].id = resQuestion.id;
              for (let locOption of this.questions[elementNumber].options) {
                for (let resOption of resQuestion.options) {
                  if (locOption.title === resOption.title) {
                    locOption.id = resOption.id;
                  }
                }
              }
            }
          },
          error => console.log(<any>error)
        );
      return;
    }
    if (question.id !== undefined) {
      this.questionService.updateQuestion(question)
        .subscribe(
          (response: Response) => {
            if (response.status === 200) {
              let questionNumber = this.questions.indexOf(question);
              let optionNumber = question.options.indexOf(option);
              this.optionFieldIndexWithChange = optionNumber;
              this.questionOptionFieldIndexWithChange = questionNumber;
              this.resetStatusSubmissionWithDelay();
              this.resetErrorStatus();
              let resQuestion = response.json() as Question;
              for (let locOption of this.questions[elementNumber].options) {
                for (let resOption of resQuestion.options) {
                  if (locOption.title === resOption.title) {
                    locOption.id = resOption.id;
                  }
                }
              }
            }
          },
          error => console.log(<any>error)
        );
    }
  }

  questionLinker(questionText: Select, option: Option): void {
    let id: number;
    for (let question of this.questions) {
      if (question.text === questionText.label) {
        id = question.id;
      }
    }
    if (id === undefined) {
      return;
    }
    let question = new Question();
    question.id = id;
    option.nextQuestion = question;
    this.resetStatusSubmissionWithDelay();

    if (option.id === undefined) {
      return;
    }

    this.questionService.createLinkFromOption(option, this.selectedQuestion.id)
      .subscribe(
        (response: Response) => {
          if (response.status === 200) {
            let resOption = response.json() as Option;
            option.id = resOption.id;
          }
        },
        error => {
          option.nextQuestion = undefined;
          this.optionIndexError = option.id;
          this.errorOptionMessage = 'Це питання приєднати неможливо, воно призведе до зациклювання в опитувальнику!';
          this.attachedQuestion = this.ATTACHED_QUESTION;
          console.log(<any>error);
        }
      );
  }

  procedureLinker(selected: Select, option) {
    let procedure = new Procedure();
    procedure.id = +selected.value;
    option.procedure = procedure;
    this.resetStatusSubmissionWithDelay();
    if (option.id === undefined) {
      return;
    }

    this.questionService.createLinkFromOption(option, this.selectedQuestion.id)
      .subscribe(
        (response) => {
          if (response === 200) {
            let resOption = response.json() as Option;
            option.id = resOption.id;
          }
        },
        error => {
          option.procedure = undefined;
          this.optionIndexError = option.id;
          this.errorOptionMessage = 'Не можу приеднати цю картку, зверніться до адміністратора';
          this.attachedProcedure = this.ATTACHED_PROCEDURE;
          console.log(<any>error);
        }
      );
  }

  getProcedure() {
    this.procedureService.getAllProcedure()
      .subscribe(
        (response: Response) => {
          if (response.status === 200) {
            this.procedures = response.json() as Procedure[];
            this.proceduresSelect = [];
            for (let procedure of this.procedures) {
              let select = new Select();
              select.value = '' + procedure.id;
              select.label = procedure.name;
              this.proceduresSelect.push(select);
            }
          }
        },
        error => console.log(<any>error)
      );
  }

  saveBusinessType() {
    this.resetErrorStatus();
    if (this.businessType.title === undefined || this.businessType.title === '') {
      this.errorMessage = this.ERROR_MSG_EMPTY_FIELD;
      this.wrongBusinessType = true;
      this.businessTypeTitleClass = this.STYLE_WRONG_SUBMISSION;
      return;
    }
    if (this.businessType.codeKved === undefined || this.businessType.codeKved === '') {
      this.errorMessage = this.ERROR_MSG_EMPTY_FIELD;
      this.wrongBusinessType = true;
      this.businessTypeKvedClass = this.STYLE_WRONG_SUBMISSION;
      return;
    }
    this.businessTypeService.createBusinessType(this.businessType)
      .subscribe(
        (response: Response) => {
          if (response.status === 201) {
            this.businessType = response.json() as BusinessType;
            this.wrongBusinessType = false;
            this.businessTypeTitleClass = this.STYLE_SUCCESS_SUBMISSION;
            this.businessTypeKvedClass = this.STYLE_SUCCESS_SUBMISSION;
            this.successBusinessType = true;
            this.resetStatusSubmissionWithDelay();
          }
        },
        (failResponse) => {
          if (failResponse !== 201) {
            this.wrongBusinessType = true;
            this.errorMessage = 'Поле КВЄД пара чисел розділених крапкою, або такі поля вже існують!';
            this.businessTypeKvedClass = this.STYLE_WRONG_SUBMISSION;
            this.businessTypeTitleClass = this.STYLE_WRONG_SUBMISSION;
          }
        }
      );
  }

  saveQuestionnare() {
    this.resetErrorStatus();
    if (this.businessType.id === undefined) {
      this.errorMessage = 'Спочатку виберіть тип бізнесу!';
      this.businessTypeKvedClass = this.STYLE_WRONG_SUBMISSION;
      this.businessTypeTitleClass = this.STYLE_WRONG_SUBMISSION;
      this.wrongBusinessType = true;
      return;
    }
    if (this.questionnaire.title === undefined || this.questionnaire.title === '') {
      this.errorMessage = this.ERROR_MSG_EMPTY_FIELD;
      this.questionnaireClass = this.STYLE_WRONG_SUBMISSION;
      this.wrongQuestionnaire = true;
      return;
    }
    this.questionnaire.businessType = this.businessType;
    this.questionService.createQuestionare(this.questionnaire)
      .subscribe(
        (response: Response) => {
          if (response.status === 201) {
            let questionnaire = response.json() as Questionnaire;
            this.questionnaire.id = questionnaire.id;
            this.wrongQuestionnaire = false;
            this.questionnaireClass = this.STYLE_SUCCESS_SUBMISSION;
            this.successQuestionnaire = true;
            this.resetStatusSubmissionWithDelay();
          }
        },
        (failResponse: Response) => {
          if (failResponse.status !== 201) {
            this.errorMessage = 'Назва опитувальника вже існує!';
            this.wrongQuestionnaire = true;
            this.questionnaireClass = this.STYLE_WRONG_SUBMISSION;
          }
        }
      );
  }

  getBusinessTypes() {
    console.log('get Business Type');
    if (this.businessTypes === undefined) {
      this.businessTypes = [];
    }
    this.businessTypeService.getBusinessTypes()
      .subscribe(
        (response: BusinessType[]) => {
          this.businessTypes = response;
          this.kvedsSelect = [];
          for (let type of this.businessTypes) {
            let sel = new Select();
            sel.label = type.codeKved + ' : ' + type.title;
            sel.value = '' + type.id;
            this.kvedsSelect.push(sel);
          }
          console.log('its ok');
        },
        (error) => {
          console.log(error);
        }
      );
  }

  chooseTypeBusinessFromServer(selectedKved) {
    this.resetErrorStatus();
    for (let businessType of this.businessTypes) {
      if (businessType.id.toString() === selectedKved.value) {
        this.businessType = businessType;
      }
    }
  }

  resetErrorStatus() {
    if (this.businessTypeKvedClass !== this.STYLE_SUCCESS_SUBMISSION) {
      this.businessTypeKvedClass = '';
    }
    if (this.businessTypeTitleClass !== this.STYLE_SUCCESS_SUBMISSION) {
      this.businessTypeTitleClass = '';
    }
    if (this.questionnaireClass !== this.STYLE_SUCCESS_SUBMISSION) {
      this.questionnaireClass = '';
    }
    this.errorMessage = '';
    this.wrongBusinessType = false;
    this.wrongQuestionnaire = false;
    this.wrongQuestion = false;
    this.errorOptionMessage = '';
    this.optionIndexError = 0.1;
    this.canSubmit = true;
  }

  resetStatusSubmissionWithDelay() {
    setTimeout(() => {
        this.businessTypeKvedClass = '';
        this.businessTypeTitleClass = '';
        this.questionnaireClass = '';
        this.successBusinessType = false;
        this.successQuestionnaire = false;
        this.successQuestionLink = false;
        this.successProcedureLink = false;
        this.questionFieldIndexWithChange = undefined;
        this.optionFieldIndexWithChange = undefined;
        this.questionOptionFieldIndexWithChange = undefined;
        this.inputTypeFieldIndexWithChange = undefined;
      }, 5000
    );
  }

  formingQuestionSelect(item: Question) {
    this.questionsSelect = [];
    for (let question of this.questions) {
      if (item.id === question.id) {
        continue;
      }
      let select = new Select();
      select.value = '' + question.id;
      select.label = question.text;
      this.questionsSelect.push(select);
    }
  }

  findeQuestionById(id: number): Question {
    for (let question of this.questions) {
      if (question.id === id) {
        return question;
      }
    }
  }

  findeProcedureById(id: number): Procedure {
    for (let procedure of this.procedures) {
      if (procedure.id === id) {
        return procedure;
      }
    }
  }

  onDeselectedNextQuestion(option, question) {
    if (option.nextQuestion === undefined) {
      return;
    }
    option.nextQuestion = undefined;
    this.questionService.updateQuestion(question)
      .subscribe(
        (response: Response) => {
          if (response.status === 200) {
            this.linkedQuestion = this.ATTACHED_PROCEDURE;
          }
        },
        (response: Response) => {
          console.log(response);
        }
      );
  }

  onDeselectedProcedure(option, question) {
    console.log('deselected');
    if (option.procedure === undefined) {
      return;
    }
    this.some = true;
    option.procedure = undefined;
    this.questionService.updateQuestion(question)
      .subscribe(
        (response: Response) => {
          if (response.status === 200) {
            this.linkedProcedure = this.ATTACHED_PROCEDURE;
          }
        },
        (response: Response) => {
          console.log(response);
        }
      );

  }
}

class Id {
  id: number;

  constructor(id: number) {
    this.id = id;
  }
}

class Select {
  value: string;
  label: string;
  disabled = false;
}
