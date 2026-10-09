import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';
import { LoginRequest, UserInfo } from '../models/auth.model';

const USER_KEY = 'bergerie_user';
const DEMO_KEY = 'bergerie_is_demo';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly currentUserSignal = signal<UserInfo | null>(this.readStoredUser());
  private readonly isDemoSignal = signal<boolean>(localStorage.getItem(DEMO_KEY) === 'true');

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly isDemo = this.isDemoSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.currentUserSignal() !== null);

  constructor(private http: HttpClient) {}

  // Le serveur crée une session et renvoie un cookie ; le navigateur le renverra
  // automatiquement à chaque requête suivante (grâce à withCredentials sur le HttpClient).
  login(request: LoginRequest): Observable<UserInfo> {
    return this.http.post<UserInfo>(`${API_BASE_URL}/auth/login`, request).pipe(
      tap((user) => {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        localStorage.removeItem(DEMO_KEY);
        this.isDemoSignal.set(false);
        this.currentUserSignal.set(user);
      })
    );
  }

  loginDemo(): Observable<UserInfo> {
    return this.http.post<UserInfo>(`${API_BASE_URL}/auth/demo`, {}).pipe(
      tap((user) => {
        localStorage.setItem(USER_KEY, JSON.stringify(user));
        localStorage.setItem(DEMO_KEY, 'true');
        this.isDemoSignal.set(true);
        this.currentUserSignal.set(user);
      })
    );
  }

  logout(): void {
    this.http.post(`${API_BASE_URL}/auth/logout`, {}).subscribe();
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(DEMO_KEY);
    this.isDemoSignal.set(false);
    this.currentUserSignal.set(null);
  }

  private readStoredUser(): UserInfo | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserInfo;
    } catch {
      return null;
    }
  }
}
