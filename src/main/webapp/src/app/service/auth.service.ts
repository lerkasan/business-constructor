import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Response, Http, Headers, RequestOptions} from '@angular/http';
import {User} from '../model/user';
import {UserService} from '../service/user.service';

@Injectable()
export class AuthService {

  private authenticatedUser = new User;

  constructor(private http: Http, private userService: UserService) {
  }

  public authRequest(username: string, password: string, rememberme: boolean) {
    let body = this.authBodyGenerator(username, password, rememberme);
    let headers = new Headers({'Content-Type': 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'});
    headers.append('cache-control', 'no-cache');
    let options = new RequestOptions({headers: headers});

    return this.http.post('/login', body, options)
      .map((res) => {
        if (res.status === 200) {
          this.authenticatedUser = res.json() as User;
          sessionStorage.setItem('currentUser', JSON.stringify(this.authenticatedUser));
        }
        return res.status;
      })
      .catch(this.handleError);
  }

  public logout() {
    sessionStorage.removeItem('currentUser');
    sessionStorage.clear();
    this.sendLogout().subscribe();
  }

  public sendLogout() {
    let path = '/api/logout';
    let headers = new Headers({'Content-Type': 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'});
    headers.append('cache-control', 'no-cache');
    let options = new RequestOptions({headers: headers});
    return this.http.get(path, options)
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

  public autoLogin() {
    this.userService.getCurrentUser()
      .subscribe(
        (response: Response) => {
          if (response.status === 200) {
            this.authenticatedUser = response.json() as User;
            sessionStorage.setItem('currentUser', JSON.stringify(this.authenticatedUser));
          }
        },
        (response: Response) => {
          console.log(response.json);
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

  private authBodyGenerator(username: string, password: string, rememberme: boolean) {
    let body = '------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n';
    body += 'Content-Disposition: form-data; name=\"username\"\r\n\r\n' + username + '\r\n';
    body += '------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n';
    body += 'Content-Disposition: form-data; name=\"password\"\r\n\r\n' + password + '\r\n';
    body += '------WebKitFormBoundary7MA4YWxkTrZu0gW\r\n';
    body += 'Content-Disposition: form-data; name=\"rememberme\"\r\n\r\n' + rememberme + '\r\n';
    body += '------WebKitFormBoundary7MA4YWxkTrZu0gW--';
    return body;
  }

  public getUser(): any {
    return JSON.parse(sessionStorage.getItem('currentUser'));
  }

  public isLoggedAsExpert(): boolean {
    if (this.getUser() === null) {
      return false;
    }
    for (let role of this.getUser().roles) {
      if (role.title === 'ROLE_EXPERT') {
        return true;
      }
    }
    return false;
  }

  public isLoggedAsAdmin(): boolean {
    if (this.getUser() === null) {
      return false;
    }
    for (let role of this.getUser().roles) {
      if (role.title === 'ROLE_ADMIN') {
        return true;
      }
    }
    return false;
  }

  public isLoggedAsUser(): boolean {
    if (this.getUser() === null) {
      return false;
    }
    for (let role of this.getUser().roles) {
      if (role.title === 'ROLE_USER') {
        return true;
      }
    }
    return false;
  }
}
