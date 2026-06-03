import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import {BehaviorSubject, Observable, catchError, map, of, tap, finalize} from 'rxjs';

// Interface for user data
export interface User {
  name: string;
  email: string;
  googleId?: string;
  picture?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  public currentUser$ = this.currentUserSubject.asObservable();

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Check authentication status when service is initialized
    this.checkAuthStatus().subscribe(isAuthenticated => {
      if (isAuthenticated && window.location.pathname === "/") {
        this.router.navigate(['/app/dashboard']);
      }
    });
  }


  checkAuthStatus(): Observable<boolean> {
    // Try to get tasks, which is a protected endpoint
    return this.http.get<any>('/api/tasks').pipe(
      map(response => {
        // If a response is received, the user is authenticated
        this.isAuthenticatedSubject.next(true);

        return true;
      }),
      catchError(error => {
        console.log('Not authenticated', error);
        this.isAuthenticatedSubject.next(false);
        this.currentUserSubject.next(null);
        return of(false);
      })
    );
  }

  login(): void {
    localStorage.setItem('returnUrl', this.router.url);
    window.location.href = '/oauth2/authorization/google';
  }

  logout(): void {
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);

    this.http.post<void>('/logout', {}).pipe(
      finalize(() => {

        window.location.href = '/';
      }),
      catchError(error => {
        console.error('Logout error', error);
        return of(void 0);
      })
    ).subscribe();
  }

  isLoggedIn(): boolean {
    return this.isAuthenticatedSubject.getValue();
  }
}
