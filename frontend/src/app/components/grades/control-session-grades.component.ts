import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResultService } from '../../services/result.service';
import { GradeService } from '../../services/grade.service';
import { Student } from '../../models/student.model';
import { StudentResult, SubjectGradeRow } from '../../models/result.model';
import { GradeEntry } from '../../models/grade.model';

/**
 * Page "Session de Contrôle".
 * N'affiche QUE les étudiants dont le résultat de session principale
 * est CONTROL_SESSION (règle métier appliquée côté backend).
 */
@Component({
  selector: 'app-control-session-grades',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2 class="page-title">Session de Contrôle</h2>

    <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <div class="card mb-3">
      <div class="card-body">
        <label class="form-label">Étudiant en session de contrôle</label>
        <select class="form-select" [(ngModel)]="selectedStudentId" (ngModelChange)="onStudentChange()">
          <option [ngValue]="null" disabled>-- Choisir un étudiant --</option>
          <option *ngFor="let s of controlStudents" [ngValue]="s.id">
            {{ s.firstName }} {{ s.lastName }} ({{ s.registrationNumber }}) — {{ s.academicClassName }}
          </option>
        </select>
      </div>
    </div>

    <div *ngIf="controlStudents.length === 0" class="alert alert-info">
      Aucun étudiant n'est actuellement en session de contrôle.
    </div>

    <div *ngIf="result">
      <p class="text-muted">
        Moyenne annuelle (session principale) : <strong>{{ result.annualAverage }}</strong> / 20
        — seules les matières où la moyenne est insuffisante nécessitent une note de contrôle,
        mais tu peux saisir une note de contrôle pour n'importe quelle matière si besoin.
      </p>

      <div class="card mb-3">
        <div class="card-header fw-bold">Semestre 1</div>
        <div class="card-body p-0">
          <table class="table mb-0">
            <thead><tr><th>Matière</th><th>Coeff.</th><th>Note Principale</th><th style="width:150px">Note Contrôle</th></tr></thead>
            <tbody>
              <tr *ngFor="let row of result.semester1.subjectGrades">
                <td>{{ row.subjectName }}</td>
                <td>{{ row.coefficient }}</td>
                <td>{{ row.mainGrade ?? '—' }}</td>
                <td>
                  <input type="number" class="form-control" min="0" max="20" step="0.25"
                         [(ngModel)]="controlGradesMap[row.subjectId]" [ngModelOptions]="{standalone: true}">
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div class="card mb-3">
        <div class="card-header fw-bold">Semestre 2</div>
        <div class="card-body p-0">
          <table class="table mb-0">
            <thead><tr><th>Matière</th><th>Coeff.</th><th>Note Principale</th><th style="width:150px">Note Contrôle</th></tr></thead>
            <tbody>
              <tr *ngFor="let row of result.semester2.subjectGrades">
                <td>{{ row.subjectName }}</td>
                <td>{{ row.coefficient }}</td>
                <td>{{ row.mainGrade ?? '—' }}</td>
                <td>
                  <input type="number" class="form-control" min="0" max="20" step="0.25"
                         [(ngModel)]="controlGradesMap[row.subjectId]" [ngModelOptions]="{standalone: true}">
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <button class="btn btn-success" (click)="saveControlGrades()">Enregistrer les notes de contrôle</button>
    </div>
  `
})
export class ControlSessionGradesComponent implements OnInit {

  controlStudents: Student[] = [];
  selectedStudentId: number | null = null;
  result: StudentResult | null = null;
  controlGradesMap: { [subjectId: number]: number | null } = {};

  errorMessage = '';
  successMessage = '';

  constructor(private resultService: ResultService, private gradeService: GradeService) {}

  ngOnInit(): void {
    this.loadControlStudents();
  }

  loadControlStudents(): void {
    this.resultService.getStudentsRequiringControlSession().subscribe(data => this.controlStudents = data);
  }

  onStudentChange(): void {
    this.result = null;
    this.controlGradesMap = {};
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.selectedStudentId) return;

    this.resultService.getStudentResult(this.selectedStudentId).subscribe(data => {
      this.result = data;
      // Pré-remplit avec les notes de contrôle déjà saisies, s'il y en a
      [...data.semester1.subjectGrades, ...data.semester2.subjectGrades].forEach((row: SubjectGradeRow) => {
        if (row.controlGrade !== null) {
          this.controlGradesMap[row.subjectId] = row.controlGrade;
        }
      });
    });
  }

  saveControlGrades(): void {
    if (!this.selectedStudentId || !this.result) return;

    const allRows = [...this.result.semester1.subjectGrades, ...this.result.semester2.subjectGrades];
    const entries: GradeEntry[] = allRows
      .filter(row => this.controlGradesMap[row.subjectId] !== null && this.controlGradesMap[row.subjectId] !== undefined)
      .map(row => ({ subjectId: row.subjectId, grade: this.controlGradesMap[row.subjectId] }));

    if (entries.length === 0) {
      this.errorMessage = 'Saisis au moins une note de contrôle avant d\'enregistrer.';
      return;
    }

    this.gradeService.saveBatch({
      studentId: this.selectedStudentId,
      sessionType: 'CONTROL',
      grades: entries
    }).subscribe({
      next: () => {
        this.successMessage = 'Notes de contrôle enregistrées avec succès.';
        this.errorMessage = '';
        // Recharge le résultat et la liste (l'étudiant peut sortir de la liste si désormais admis)
        this.onStudentChange();
        this.loadControlStudents();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Erreur lors de l\'enregistrement.';
        this.successMessage = '';
      }
    });
  }
}
