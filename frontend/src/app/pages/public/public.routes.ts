import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import {ContactComponent} from './contact/contact.component';

export const PUBLIC_ROUTES: Routes = [
  { path: '', component: HomeComponent },
  { path: 'contact', component: ContactComponent }
];
