import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark px-3">
      <a class="navbar-brand fw-bold" routerLink="/dashboard">🎓 Student Management System</a>
      <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navContent">
        <span class="navbar-toggler-icon"></span>
      </button>
      <div class="collapse navbar-collapse" id="navContent">
        <ul class="navbar-nav ms-auto">
          <li class="nav-item">
            <a class="nav-link" routerLink="/dashboard" routerLinkActive="active fw-bold">Dashboard</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" routerLink="/classes" routerLinkActive="active fw-bold">Classes</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" routerLink="/students" routerLinkActive="active fw-bold">Étudiants</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" routerLink="/subjects" routerLinkActive="active fw-bold">Matières</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" routerLink="/grades/main-session" routerLinkActive="active fw-bold">Notes (Session Principale)</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" routerLink="/grades/control-session" routerLinkActive="active fw-bold">Session de Contrôle</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" routerLink="/results" routerLinkActive="active fw-bold">Résultats</a>
          </li>
        </ul>
      </div>
    </nav>
  `
})
export class NavbarComponent {}
