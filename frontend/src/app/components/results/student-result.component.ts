import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { ResultService } from '../../services/result.service';
import { Student } from '../../models/student.model';
import { StudentResult } from '../../models/result.model';

@Component({
  selector: 'app-student-result',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2 class="page-title">Consultation des résultats</h2>

    <div class="card mb-3">
      <div class="card-body">
        <label class="form-label">Sélectionner un étudiant</label>
        <select class="form-select" [(ngModel)]="selectedStudentId" (ngModelChange)="onStudentChange()">
          <option [ngValue]="null" disabled>-- Choisir un étudiant --</option>
          <option *ngFor="let s of allStudents" [ngValue]="s.id">
            {{ s.firstName }} {{ s.lastName }} ({{ s.registrationNumber }}) — {{ s.academicClassName }}
          </option>
        </select>
      </div>
    </div>

    <div *ngIf="result">
      <!-- Informations personnelles -->
      <div class="card mb-3">
        <div class="card-body">
          <h5>{{ result.firstName }} {{ result.lastName }}</h5>
          <p class="text-muted mb-0">
            Matricule : {{ result.registrationNumber }} — Classe : {{ result.academicClassName }}
          </p>
        </div>
      </div>

      <!-- Semestre 1 -->
      <div class="card mb-3">
        <div class="card-header fw-bold">Semestre 1</div>
        <div class="card-body p-0">
          <table class="table mb-0">
            <thead><tr><th>Matière</th><th>Coefficient</th><th>Note Principale</th><th>Note Contrôle</th></tr></thead>
            <tbody>
              <tr *ngFor="let row of result.semester1.subjectGrades">
                <td>{{ row.subjectName }}</td>
                <td>{{ row.coefficient }}</td>
                <td>{{ row.mainGrade ?? '—' }}</td>
                <td>{{ row.controlGrade ?? '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="card-footer">
          Moyenne S1 : <strong>{{ result.semester1.semesterAverage ?? '—' }}</strong> / 20
        </div>
      </div>

      <!-- Semestre 2 -->
      <div class="card mb-3">
        <div class="card-header fw-bold">Semestre 2</div>
        <div class="card-body p-0">
          <table class="table mb-0">
            <thead><tr><th>Matière</th><th>Coefficient</th><th>Note Principale</th><th>Note Contrôle</th></tr></thead>
            <tbody>
              <tr *ngFor="let row of result.semester2.subjectGrades">
                <td>{{ row.subjectName }}</td>
                <td>{{ row.coefficient }}</td>
                <td>{{ row.mainGrade ?? '—' }}</td>
                <td>{{ row.controlGrade ?? '—' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="card-footer">
          Moyenne S2 : <strong>{{ result.semester2.semesterAverage ?? '—' }}</strong> / 20
        </div>
      </div>

      <!-- Résultat final -->
      <div class="card">
        <div class="card-body">
          <p>Moyenne annuelle (session principale) : <strong>{{ result.annualAverage ?? '—' }}</strong> / 20</p>
          <p>
            Résultat session principale :
            <span class="badge"
                  [ngClass]="{
                    'badge-admitted': result.mainSessionResult === 'ADMITTED',
                    'badge-control': result.mainSessionResult === 'CONTROL_SESSION',
                    'badge-pending': !result.mainSessionResult
                  }">
              {{ mainResultLabel(result.mainSessionResult) }}
            </span>
          </p>

          <div *ngIf="result.mainSessionResult === 'CONTROL_SESSION'">
            <p>Moyenne annuelle après contrôle : <strong>{{ result.annualAverageAfterControl ?? '—' }}</strong> / 20</p>
            <p>
              Résultat final :
              <span class="badge"
                    [ngClass]="{
                      'badge-admitted': result.finalResult === 'ADMITTED',
                      'badge-failed': result.finalResult === 'FAILED',
                      'badge-pending': !result.finalResult
                    }">
                {{ finalResultLabel(result.finalResult) }}
              </span>
            </p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class StudentResultComponent implements OnInit {

  allStudents: Student[] = [];
  selectedStudentId: number | null = null;
  result: StudentResult | null = null;

  constructor(private studentService: StudentService, private resultService: ResultService) {}

  ngOnInit(): void {
    this.studentService.getAll().subscribe(data => this.allStudents = data);
  }

  onStudentChange(): void {
    this.result = null;
    if (!this.selectedStudentId) return;

    this.resultService.getStudentResult(this.selectedStudentId).subscribe(data => this.result = data);
  }

  mainResultLabel(status: string | null): string {
    switch (status) {
      case 'ADMITTED': return 'Admis';
      case 'CONTROL_SESSION': return 'Session de contrôle requise';
      default: return 'En attente de notes';
    }
  }

  finalResultLabel(status: string | null): string {
    switch (status) {
      case 'ADMITTED': return 'Admis';
      case 'FAILED': return 'Ajourné (redouble)';
      default: return 'En attente des notes de contrôle';
    }
  }
}
