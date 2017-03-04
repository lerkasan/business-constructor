import {Injectable} from '@angular/core';
import {Http, Headers, RequestOptions} from '@angular/http';
import {User} from '../model/user';

@Injectable()
export class UserService {
  constructor(private http: Http) {
  }

  public getRegisteredUsers() {
    let path = '/api/users';
    let headers = new Headers({'Content-Type': 'application/json'});
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

  public getCurrentUser() {
    let path = '/api/users/current';
    let headers = new Headers({'Content-Type': 'application/json'});
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

  public updateUser(user: User) {
    let path = '/api/users/' + user.id;
    let headers = new Headers({'Content-Type': 'application/json'});
    let options = new RequestOptions({headers: headers});

    return this.http.put(path, JSON.stringify(user), options)
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
}
