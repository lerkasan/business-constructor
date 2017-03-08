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
  loggedin = false;
  user = new User;
  model = this.user;
  adminBoard = false;

  constructor(private authService: AuthService, private router: Router) {
  }

  ngOnInit() {
    this.authService.autoLogin();
    if (sessionStorage.getItem('currentUser')) {
      this.loggedin = true;
      this.user = JSON.parse(sessionStorage.getItem('currentUser'));
      this.model = this.user;
      this.adminBoard = this.showAdminPanel();
    }
  }

  logout() {
    this.loggedin = false;
    this.adminBoard = false;
    this.authService.logout();
    this.router.navigate(['/']);
  }

  showAdminPanel() {
    if (this.authService.isLoggedAsExpert()) {
      return true;
    }
    if (this.authService.isLoggedAsAdmin()) {
      return true;
    }
    return false;
  }
}
