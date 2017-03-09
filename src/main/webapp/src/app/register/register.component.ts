import {Component, OnInit} from '@angular/core';
import {User} from '../model/user';
import {Router} from '@angular/router';
import {RegistrationService} from '../service/registration.service';

@Component({
  selector: 'brdo-my-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {

  user = new User();
  duplicatePassword = new DuplicatePassword();
  message = '';
  succes = false;


  constructor(private registrationService: RegistrationService, private router: Router) {
  }

  ngOnInit() {
  }

  registrationAway() {
    this.router.navigate(['/']);
  }

  registrationRequest() {

    this.user.rawPassword = this.user.password;
    this.registrationService.registrationRequest(this.user)
      .subscribe((status) => {
          if (status === 200) {
            this.user.password = '';
            this.user.rawPassword = '';
            this.message = this.user.username;
            this.succes = true;
          }
        },
        (error) => {
           this.message = <any>error;
        }
      );
  }

}

class DuplicatePassword {
  duplicatePassword: string;

  constructor() {
  }
}
