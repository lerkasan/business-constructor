import { NgModule, ApplicationRef } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule, JsonpModule } from '@angular/http';
import {FormsModule} from '@angular/forms';


import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './register/register.component';
import { LoginComponent } from './login/login.component';
import { HeaderComponent } from './header/header.component';
import { CarouselComponent } from './carousel/carousel.component';
import { AdminComponent } from './admin/admin.component';
import { ConstructorComponent } from './constructor/constructor.component';
import { UserAdministrationComponent } from './user-administration/user-administration.component';
import { UserStatisticsComponent } from './user-statistics/user-statistics.component';

import { routing } from './app.routing';

import { removeNgStyles, createNewHosts } from '@angularclass/hmr';
import {AuthService} from './service/auth.service';
import {RegistrationService} from './service/registration.service';
import {QuestionService} from './service/questions.service';
import {ProcedureService} from './service/procedure.service';
import {BusinessTypeService} from './service/business.type.service';
import {ApiService} from './shared/api.service';
import {BoardAuthGuard} from './guards/board.auth.guard';
import {UserService} from './service/user.service';
import {SelectModule} from 'angular2-select';

@NgModule({
  imports: [
    BrowserModule,
    HttpModule,
    FormsModule,
    routing,
    JsonpModule,
    SelectModule
  ],
  declarations: [
    AppComponent,
    HomeComponent,
    RegisterComponent,
    LoginComponent,
    HeaderComponent,
    CarouselComponent,
    AdminComponent,
    ConstructorComponent,
    UserAdministrationComponent,
    UserStatisticsComponent
  ],
  providers: [
    ApiService,
    AuthService,
    RegistrationService,
    QuestionService,
    ProcedureService,
    UserService,
    BusinessTypeService,
    BoardAuthGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(public appRef: ApplicationRef) {}
  hmrOnInit(store) {
    console.log('HMR store', store);
  }
  hmrOnDestroy(store) {
    let cmpLocation = this.appRef.components.map(cmp => cmp.location.nativeElement);
    // recreate elements
    store.disposeOldHosts = createNewHosts(cmpLocation);
    // remove styles
    removeNgStyles();
  }
  hmrAfterDestroy(store) {
    // display new elements
    store.disposeOldHosts();
    delete store.disposeOldHosts;
  }
}
