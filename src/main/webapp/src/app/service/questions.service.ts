import {Injectable} from '@angular/core';
import {Http, Response, Headers, RequestOptions} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {Question} from '../model/question';
import {Option} from '../model/option';
import {Questionnaire} from '../model/questionnaire';
import {Business} from '../model/business';
import {Answer} from '../model/answer';

@Injectable()
export class QuestionService {

  constructor(private http: Http) {
  }

  public createQuestion(question: Question) {
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post('/api/questions', JSON.stringify(question), options)
      .map((response) => {
        return response;
      })
      .catch(this.handleError);
  }

  public updateQuestion(question: Question) {
    let path = '/api/questions/' + question.id;
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.put(path, JSON.stringify(question), options)
      .map((response) => {
        return response;
      })
      .catch(this.handleError);
  }

  public createLinkFromOption(option: Option, questionId: number) {
    let path = '/api/questions/' + questionId + '/options/' + option.id;
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.put(path, JSON.stringify(option), options)
      .map((response) => {
        return response;
      })
      .catch(this.handleError);

  }

  public createQuestionare(questionare: Questionnaire) {
    let path = '/api/questionnaires';
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post(path, JSON.stringify(questionare), options)
      .map((response) => {
          return response;
        }
      )
      .catch((response) => {
        return response;
      });
  }

  public listQuestionnaires() {
    let path = '/api/questionnaires';
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.get(path, options)
      .map(
        (response) => {
          return response;
        }
      )
      .catch(this.handleError);
  }

  public saveBusiness (business: Business) {
    let path = '/api/businesses';
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post(path, JSON.stringify(business), options)
      .map(
        (response) => {
          return response;
        }
      )
      .catch(
        (response) => {
          return response;
        }
      );
  }

  public saveAnswer (answer: Answer) {
    let path = '/api/busineess/' + answer.business.id + '/answers';
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.post(path, JSON.stringify(answer), options)
      .map(
        (response) => {
          return response;
        }
      )
      .catch(
        (response) => {
          return response;
        }
      );
  }

  private handleError(error: Response | any) {
    let errMsg: string;
    if (error instanceof Response) {
      const body = error.json() || '';
      const err = body.error || JSON.stringify(body);
      errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
    } else {
      errMsg = error.message ? error.message : error.toString();
    }
    console.error(errMsg);
    return Observable.throw(errMsg);
  }
}
