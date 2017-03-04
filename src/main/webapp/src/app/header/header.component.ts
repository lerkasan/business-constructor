import {Component, OnInit} from '@angular/core';
import {AuthService} from '../service/auth.service';
import {User} from '../model/user';
import {Router} from '@angular/router';


@Component({
  selector: 'brdo-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})

export class HeaderComponent implements OnInit {

  loggedin = true;
  user = new User;
  model = this.user;
  adminBoard: boolean = true;

  constructor(private authService: AuthService, private router: Router) {
  }

  ngOnInit() {
    this.authService.autoLogin();
    if (sessionStorage.getItem('currentUser')) {
      this.loggedin = false;
      this.user = JSON.parse(sessionStorage.getItem('currentUser'));
      this.model = this.user;
      this.adminBoard = this.showAdminPanel();

    }
  }

  logout() {
    this.loggedin = true;
    this.adminBoard = true;
    this.authService.logout();
    this.router.navigate(['/']);
  }

  showAdminPanel() {
    if (this.authService.isLoggedAsExpert()) {
      return false;
    }
    if (this.authService.isLoggedAsAdmin()) {
      return false;
    }
    return true;
  }
}
