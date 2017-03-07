import {Component, OnInit, ElementRef} from '@angular/core';
import {BusinessTypeService} from '../service/business.type.service';
import {BusinessType} from '../model/business.type';
import {Questionnaire} from '../model/questionnaire';
import {QuestionService} from '../service/questions.service';
import {Question} from '../model/question';
import {Procedure} from '../model/procedure';
import {ProcedureService} from '../service/procedure.service';
import {Option} from '../model/option';
import {Business} from '../model/business';
import {Answer} from '../model/answer';
import {Response} from '@angular/http';
import {Stage} from '../model/stage';

declare var $: any;

@Component({
  selector: 'brdo-my-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  PROCEDURE_IS_NOT_SELCTED = 'Виберіть будь ласка процедуру у лівому списку!';


  answers: Answer[];
  canSaveAnswers: boolean;
  business: Business;
  businessType: BusinessType;
  procedure: Procedure;
  procedures: Procedure[];
  businessTypes: BusinessType[];
  questionnaires: Questionnaire[];
  questions: Question[];
  selectedQuestionnaire: Questionnaire;
  selectedBusinessType: BusinessType;
  submitAnswers: boolean;
  questionsSelect: Select[];

  constructor(private _elmRef: ElementRef, private businessTypeService: BusinessTypeService,
              private questionService: QuestionService, private procedureService: ProcedureService) {
    let numOptions = 100;
    let opts = new Array(numOptions);
  }

  ngOnInit() {
    $(this._elmRef.nativeElement).find('[data-toggle="tooltip"]').tooltip();
    $(this._elmRef.nativeElement).find('.spoiler-trigger').click(function () {
      $(this).parent().next().collapse('toggle');
    });
    this.procedures = [];
    this.getBusinessTypes();
    this.getQuestionnair();
    this.procedureInit();
    this.businessType = new BusinessType();
    this.businessType.codeKved = '';
    this.businessType.title = '';
    this.canSaveAnswers = false;
    this.business = new Business();
    this.business.businessType = new BusinessType();
    this.answers = [];
    this.submitAnswers = false;
  }

  onSingleOpened() {
    console.log('- opened');
  }

  onSingleClosed() {
    console.log('- closed');
  }

  onSingleSelected(item) {
    console.log(item);
  }

  onSingleDeselected(item) {
    console.log(item);
  }

  getBusinessTypes() {
    if (this.businessTypes === undefined) {
      this.businessTypes = [new BusinessType()];
    }
    this.businessTypeService.getBusinessTypes()
      .subscribe(
        (response: BusinessType[]) => {
          this.businessTypes = response;
        },
        (error) => {
          console.log(error);
        }
      );
  }

  getQuestionnair() {
    if (this.questionnaires === undefined) {
      let questionnaire = new Questionnaire();
      questionnaire.questions = [new Question];
      questionnaire.businessType = new BusinessType;
      this.questionnaires = [questionnaire];
    }
    this.questionService.listQuestionnaires()
      .subscribe(
        (response) => {
          if (response.status === 200) {
            this.questionnaires = response.json() as Questionnaire[];
            this.questionsSelect = [];
            for (let question of this.questionnaires) {
              let select = new Select();
              select.label = question.title;
              select.value = '' + question.id;
              this.questionsSelect.push(select);
            }
          }
        },
        (error) => {
          console.log(error);
        }
      );
  }

  onSelectQuestionnaire(selectedQuestion: Select) {
    this.questions = [];
    this.procedures = [];
    for (let questionnaire of this.questionnaires) {
      if (questionnaire.id.toString() === selectedQuestion.value) {
        this.business.business = questionnaire.title;
        this.business.businessType.id = questionnaire.businessType.id;
        this.businessType = questionnaire.businessType;
        this.selectedQuestionnaire = questionnaire;
        this.selectedQuestionnaire.questions.sort(
          function (a, b) {
            return a.id - b.id;
          }
        );
        this.questions.push(this.selectedQuestionnaire.questions[0]);
      }
    }
  }

  onSelectBusinessType(id) {
    for (let businessType of this.businessTypes) {
      if (businessType.id.toString() === id) {
        this.selectedBusinessType = businessType;
      }
    }
  }

  nextQuestion(option, question) {
    let needAddNextQuestion = this.deleteUnselectedAndLinkedOptionAnswers(option, question);
    if (!needAddNextQuestion) {
      this.canSubmitAnswers(this.questions, this.answers);
      return;
    }
    this.answers.push(this.answerInit(option));
    if (option.nextQuestion === undefined) {
      this.canSubmitAnswers(this.questions, this.answers);
      return;
    }
    if (this.ifQuestionIsPresent(this.findQuestionById(option.nextQuestion.id,
        this.selectedQuestionnaire.questions))) {
      this.canSubmitAnswers(this.questions, this.answers);
      return;
    }
    if (question.inputType === 'MULTI_CHOICE') {
      this.questions.push(this.findQuestionById(option.nextQuestion.id,
        this.selectedQuestionnaire.questions));
      this.canSaveAnswers = false;
      return;
    }
    if (question.inputType === 'SINGLE_CHOICE') {
      this.questions.push(this.findQuestionById(option.nextQuestion.id,
        this.selectedQuestionnaire.questions));
      this.canSaveAnswers = false;
      return;
    }
  }

  onSelectProcedure(procedure: Procedure) {
    this.procedure = procedure;
  }

  findQuestionById(id: number, questions: Question[]) {
    for (let question of questions) {
      if (question.id === id) {
        return question;
      }
    }
  }

  findAnswerByOptionId(id: number, ans: Answer[]): Answer {
    for (let an of ans) {
      if (an.option.id === id) {
        return an;
      }
    }
    return undefined;
  }

  ifQuestionIsPresent(question): boolean {
    for (let q of this.questions) {
      if (q === question) {
        return true;
      }
    }
    return false;
  }

  ifProcedureIsPresent(id: number): boolean {
    for (let p of this.procedures) {
      if (id === p.id) {
        return true;
      }
    }
    return false;
  }

  procedureInit() {
    this.procedure = new Procedure();
    this.procedure.reason = this.PROCEDURE_IS_NOT_SELCTED;
    this.procedure.result = this.PROCEDURE_IS_NOT_SELCTED;
    this.procedure.cost = this.PROCEDURE_IS_NOT_SELCTED;
    this.procedure.term = this.PROCEDURE_IS_NOT_SELCTED;
    this.procedure.method = this.PROCEDURE_IS_NOT_SELCTED;
    this.procedure.decision = this.PROCEDURE_IS_NOT_SELCTED;
    this.procedure.deny = this.PROCEDURE_IS_NOT_SELCTED;
    this.procedure.abuse = this.PROCEDURE_IS_NOT_SELCTED;
  }

  answerInit(option: Option): Answer {
    let answer = new Answer();
    answer.business = new Business();
    answer.business.id = this.business.id;
    answer.option = option;
    return answer;
  }

  deleteUnselectedAndLinkedOptionAnswers(option: Option, question: Question): boolean {
    if (this.answers[0] === undefined) {
      return true;
    }
    if (question.inputType === 'SINGLE_CHOICE') {
      this.deleteQuestionAnswerForSingleChoice(question);
      return true;
    }
    if (question.inputType === 'MULTI_CHOICE') {
      for (let answer of this.answers) {
        if (answer.option.id === option.id) {
          this.deleteQuestionAnswerForMultiChoice(option, question);
          return false;                                                   // unselected
        }
      }
      return true;                                                        // selected
    }
  }

  deleteQuestionAnswerForSingleChoice(question: Question) {
    let old_answers = [];
    for (let opt of question.options) {                                 // 1)search old answer
      let answer = this.findAnswerByOptionId(opt.id, this.answers);
      if (answer !== undefined) {
        if (answer.option.nextQuestion === undefined) {
          this.answers.splice(this.answers.indexOf(answer), 1);
          return;
        }
        old_answers.push(answer.option.nextQuestion.id);                // 2)save old answer
        this.answers.splice(this.answers.indexOf(answer), 1);           // 3)delete old answer
        break;
      }
    }
    for (let id of old_answers) {                                       // 4)search question
      let ques = this.findQuestionById(id, this.questions);
      let answer;
      for (let opt of ques.options) {                                   // 5)search it answers
        answer = this.findAnswerByOptionId(opt.id, this.answers);
        if (answer !== undefined) {
          if (answer.option.nextQuestion === undefined) {
            continue;
          }
          old_answers.push(answer.option.nextQuestion.id);              // 6)save answers
        }
      }
      for (let a of this.answers) {                                    // 7)check if answer has enather link
        if (a.option.nextQuestion === undefined) {
          continue;
        }
        if (id === a.option.nextQuestion.id) {
          return;                                                      // break if has link
        }
      }
      this.questions.splice(this.questions.indexOf(ques), 1);           // 8)delete question
      if (answer === undefined) {
        return;
      }
      this.answers.splice(this.answers.indexOf(answer), 1);             // 9)delete answer
    }
  }

  deleteQuestionAnswerForMultiChoice(option: Option, question: Question) {
    let old_answers = [];
    let answer = this.findAnswerByOptionId(option.id, this.answers);
    if (answer.option.nextQuestion === undefined) {
      this.answers.splice(this.answers.indexOf(answer), 1);
      return;
    }
    old_answers.push(answer.option.nextQuestion.id);                    // 2)save old answer
    this.answers.splice(this.answers.indexOf(answer), 1);               // 3)delete old answer
    for (let id of old_answers) {                                       // 4)search question
      let ques = this.findQuestionById(id, this.questions);
      for (let opt of ques.options) {                                   // 5)search it answers
        answer = this.findAnswerByOptionId(opt.id, this.answers);
        if (answer !== undefined) {
          if (answer.option.nextQuestion === undefined) {
            continue;
          }
          old_answers.push(answer.option.nextQuestion.id);              // 6)save answers
        }
      }
      for (let a of this.answers) {                                    // 7)check if answer has enather link
        if (a.option.nextQuestion === undefined) {
          continue;
        }
        if (id === a.option.nextQuestion.id) {
          return;                                                      // break if has link
        }
      }
      this.questions.splice(this.questions.indexOf(ques), 1);           // 8)delete question
      if (answer === undefined) {
        return;
      }
      this.answers.splice(this.answers.indexOf(answer), 1);             // 9)delete answer
    }
  }

  canSubmitAnswers(questions: Question[], answers: Answer[]) {
    this.canSaveAnswers = false;
    for (let question of questions) {
      out:
        for (let option of question.options) {
          for (let answer of answers) {
            if (option.id === answer.option.id) {
              this.canSaveAnswers = true;
              break out;
            }
            if (question.options[question.options.length - 1].id !== option.id) {
              this.canSaveAnswers = false;
            }
          }
        }
    }
  }

  saveAnswers() {
    this.submitAnswers = true;
    if (this.submitAnswers) {
      this.buildProcedureArray();
    }
  }

  buildProcedureArray() {
    for (let answer of this.answers) {
      if (answer.option.procedure !== undefined) {
        if (!this.ifProcedureIsPresent(answer.option.procedure.id)) {
          this.procedureService.getProcedure(answer.option.procedure.id)
            .subscribe(
              (response: Response) => {
                if (response.status === 200) {
                  let procedure: Procedure = response.json() as Procedure;
                  procedure.stage = this.stageInit();
                  this.procedures.push(procedure);
                }
              },
              (response: Response) => {
                console.log(response);
              }
            );

        }
      }
    }
  }

  stageInit(): Stage {
    let stage = new Stage();
    stage.finished = false;
    return stage;
  }

  changeStage() {
    if (this.procedure.reason === this.PROCEDURE_IS_NOT_SELCTED) {
      return;
    }
    let index = this.procedures.indexOf(this.procedure);
    if (this.procedures[index].stage.finished) {
      this.procedures[index].stage.finished = false;
    } else {
      this.procedures[index].stage.finished = true;
    }
  }

  onKvedSelected($event) {

  }
}

class Select {
  value: string;
  label: string;
  disabled = false;
}
