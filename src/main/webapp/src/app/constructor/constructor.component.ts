import {Component, OnInit} from '@angular/core';
import {Question} from '../model/question';
import {Option} from '../model/option';
import {QuestionService} from '../service/questions.service';
import {Procedure} from '../model/procedure';
import {ProcedureService} from '../service/procedure.service';

@Component({
  selector: 'brdo-constructor-panel',
  templateUrl: './constructor.component.html',
  styleUrls: ['./constructor.component.scss']

})

export class ConstructorComponent implements OnInit {

  businesType = '';
  selectedOption;
  selectedQuestion;
  selectedText;
  selectedTitle;
  questions: Question[];
  procedures: Procedure[];

  inputType = [
    {value: 'SINGLE_CHOICE'},
    {value: 'MULTI_CHOICE'}
  ];

  constructor(private questionService: QuestionService, private prcedureService: ProcedureService) {
  }

  ngOnInit() {
    this.procedures = [];
    // this.getProcedure();
  }

  onSelectQuestion(question: Question): void {
    this.selectedQuestion = question;
    this.selectedText = question.text;
  }

  onSelectOption(option: Option): void {
    this.selectedOption = option;
    this.selectedTitle = option.title;
  }

  addQuestion(): void {
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
    console.log('Start save inputType');
    for (let i = 0; i < question.options.length; i++) {
      if (question.options[i].title === undefined || question.options[i].title === '') {
        console.log('the field option cant save inputType');
        return;
      }
    }
    if (question.text === undefined || question.text === '') {
      console.log('the field question cant save inputType');
      return;
    }

    let elementNumber = this.questions.indexOf(question);
    if (question.id !== undefined) {
      this.questionService.updateQuestion(question)
        .subscribe(
          (ques: Question) => {
            this.questions[elementNumber] = ques;
            console.log(ques.id);
          },
          error => console.log(<any>error)
        );
    }
    console.log('Dont save inputType');
  }

  addOption(options: Option[]): void {
    options.push(this.newOption());
  }

  deleteLastOption(options: Option[]): void {
    options.pop();
  }

  saveQuestion(question: Question): void {
    if (question.text === '' || question === undefined) {
      console.log('The question enpty or undefined');
      return;
    }
    if (question.text === this.selectedText) {
      console.log('The question is not changed');
      return;
    }

    console.log(JSON.stringify(question));
    let elementNumber = this.questions.indexOf(question);
    if (question.id !== undefined) {
      this.questionService.updateQuestion(question)
        .subscribe(
          (ques: Question) => {
            this.questions[elementNumber] = ques;
          },
          error => console.log(<any>error)
        );
      console.log('Question saved');
      return;
    }
  }

  saveOption(question: Question, option: Option): void {
    let elementNumber = this.questions.indexOf(question);
    console.log(JSON.stringify(question));

    if (this.selectedTitle === option.title) {
      return;
    }
    for (let i = 0; i < elementNumber; i++) {
      if (question.options[i].title === undefined || question.options[i].title === '') {
        return;
      }
    }
    if (question.text === undefined || question.text === '') {
      console.log('The question is undefined or is empty');
      return;
    }
    if (question.id === undefined) {
      this.questionService.createQuestion(question)
        .subscribe(
          (ques: Question) => {
            this.questions[elementNumber] = ques;
            console.log(ques.id);
          },
          error => console.log(<any>error)
        );
      return;
    }
    if (question.id !== undefined) {
      console.log('send put question with this changed option');
      this.questionService.updateQuestion(question)
        .subscribe(
          (ques: Question) => {
            this.questions[elementNumber] = ques;
            console.log('Sended put');
          },
          error => console.log(<any>error)
        );
    }
  }

  questionLinker(dropDownQuestion: Question, option: Option): void {
    console.log('Start save link to question');
    if (dropDownQuestion.id === undefined || option.id === undefined) {
      return;
    }

    option.nextQuestion = new Id(dropDownQuestion.id);
    console.log(JSON.stringify(option));

    this.questionService.createLinkFromOption(option, this.selectedQuestion.id)
      .subscribe(
        (status) => {
          if (status === 200) {
            console.log('Link to next question saved!');
          }
        },
        error => console.log(<any>error)
      );
  }

  procedureLinker(procedure: Procedure) {
    console.log('save link to procedure');
    if (this.selectedQuestion.id === undefined || this.selectedOption.id === undefined) {
      return;
    }

    this.selectedOption.procedure = new Id(procedure.id);

    this.questionService.createLinkFromOption(this.selectedOption, this.selectedQuestion.id)
      .subscribe(
        (status) => {
          if (status === 200) {
            console.log('link to procedure added');
          }
        }
      );
  }

  getProcedure() {
    console.log('Start load procedure');
    this.prcedureService.getAllProcedure()
      .subscribe(
        (response: Procedure[]) => {
          console.log(response);
          this.procedures = response;
        },
        error => console.log(<any>error)
      );
  }
}

class Id {
  id: number;

  constructor(id: number) {
    this.id = id;
  }
}
