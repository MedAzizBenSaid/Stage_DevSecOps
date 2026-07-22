import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../services/dashboard.service';
import { DashboardStats } from '../../models/dashboard-stats.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2 class="page-title">Tableau de bord</h2>

    <div *ngIf="stats" class="row g-3">
      <div class="col-md-4">
        <div class="stat-card" style="background-color:#0d6efd;">
          <div>Total Classes</div>
          <h2>{{ stats.totalClasses }}</h2>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card" style="background-color:#6610f2;">
          <div>Total Étudiants</div>
          <h2>{{ stats.totalStudents }}</h2>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card" style="background-color:#20c997;">
          <div>Total Matières</div>
          <h2>{{ stats.totalSubjects }}</h2>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card" style="background-color:#198754;">
          <div>Admis</div>
          <h2>{{ stats.totalAdmitted }}</h2>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card" style="background-color:#fd7e14;">
          <div>Session de Contrôle</div>
          <h2>{{ stats.totalControlSession }}</h2>
        </div>
      </div>
      <div class="col-md-4">
        <div class="stat-card" style="background-color:#dc3545;">
          <div>Ajournés (redoublent)</div>
          <h2>{{ stats.totalFailed }}</h2>
        </div>
      </div>
    </div>

    <div *ngIf="!stats" class="text-muted">Chargement des statistiques...</div>
  `
})
export class DashboardComponent implements OnInit {

  stats: DashboardStats | null = null;

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dashboardService.getStats().subscribe(data => this.stats = data);
  }
}
