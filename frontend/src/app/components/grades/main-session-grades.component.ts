import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AcademicClassService } from '../../services/academic-class.service';
import { StudentService } from '../../services/student.service';
import { SubjectService } from '../../services/subject.service';
import { GradeService } from '../../services/grade.service';
import { AcademicClass } from '../../models/academic-class.model';
import { Student } from '../../models/student.model';
import { Subject } from '../../models/subject.model';
import { StudentGrade, GradeEntry } from '../../models/grade.model';

/**
 * Formulaire de saisie des notes de session principale.
 *
 * Fonctionnement en cascade demandé dans le cahier des charges :
 * 1. Choisir la classe -> charge les étudiants de cette classe
 * 2. Choisir un étudiant -> charge toutes les matières de sa classe,
 *    groupées par semestre (S1 / S2), avec les notes déjà existantes
 * 3. Saisir/modifier les notes
 * 4. Enregistrer -> toutes les notes sont envoyées en une seule requête batch
 */
@Component({
  selector: 'app-main-session-grades',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2 class="page-title">Saisie des notes — Session Principale</h2>

    <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
    <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

    <div class="card mb-3">
      <div class="card-body row g-3">
        <div class="col-md-4">
          <label class="form-label">1. Classe</label>
          <select class="form-select" [(ngModel)]="selectedClassId" (ngModelChange)="onClassChange()">
            <option [ngValue]="null" disabled>-- Choisir une classe --</option>
            <option *ngFor="let c of classes" [ngValue]="c.id">{{ c.name }}</option>
          </select>
        </div>
        <div class="col-md-4">
          <label class="form-label">2. Étudiant</label>
          <select class="form-select" [(ngModel)]="selectedStudentId" (ngModelChange)="onStudentChange()" [disabled]="!selectedClassId">
            <option [ngValue]="null" disabled>-- Choisir un étudiant --</option>
            <option *ngFor="let s of studentsInClass" [ngValue]="s.id">{{ s.firstName }} {{ s.lastName }} ({{ s.registrationNumber }})</option>
          </select>
        </div>
      </div>
    </div>

    <div *ngIf="selectedStudentId && subjectsS1.length + subjectsS2.length > 0">
      <div class="card mb-3">
        <div class="card-header fw-bold">Semestre 1</div>
        <div class="card-body p-0">
          <table class="table mb-0">
            <thead><tr><th>Matière</th><th>Coefficient</th><th style="width:150px">Note / 20</th></tr></thead>
            <tbody>
              <tr *ngFor="let subj of subjectsS1">
                <td>{{ subj.name }}</td>
                <td>{{ subj.coefficient }}</td>
                <td>
                  <input type="number" class="form-control" min="0" max="20" step="0.25"
                         [(ngModel)]="gradesMap[subj.id!]" [ngModelOptions]="{standalone: true}">
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
            <thead><tr><th>Matière</th><th>Coefficient</th><th style="width:150px">Note / 20</th></tr></thead>
            <tbody>
              <tr *ngFor="let subj of subjectsS2">
                <td>{{ subj.name }}</td>
                <td>{{ subj.coefficient }}</td>
                <td>
                  <input type="number" class="form-control" min="0" max="20" step="0.25"
                         [(ngModel)]="gradesMap[subj.id!]" [ngModelOptions]="{standalone: true}">
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <button class="btn btn-success" (click)="saveGrades()">Enregistrer toutes les notes</button>
    </div>

    <div *ngIf="selectedStudentId && subjectsS1.length + subjectsS2.length === 0" class="alert alert-info">
      Aucune matière n'est encore définie pour cette classe. Ajoute des matières dans "Gestion des matières".
    </div>
  `
})
export class MainSessionGradesComponent implements OnInit {

  classes: AcademicClass[] = [];
  studentsInClass: Student[] = [];
  subjectsS1: Subject[] = [];
  subjectsS2: Subject[] = [];

  selectedClassId: number | null = null;
  selectedStudentId: number | null = null;

  // Map subjectId -> note saisie (utilisé par les inputs)
  gradesMap: { [subjectId: number]: number | null } = {};

  errorMessage = '';
  successMessage = '';

  constructor(
    private academicClassService: AcademicClassService,
    private studentService: StudentService,
    private subjectService: SubjectService,
    private gradeService: GradeService
  ) {}

  ngOnInit(): void {
    this.academicClassService.getAll().subscribe(data => this.classes = data);
  }

  onClassChange(): void {
    this.selectedStudentId = null;
    this.subjectsS1 = [];
    this.subjectsS2 = [];
    this.gradesMap = {};

    if (!this.selectedClassId) return;

    this.studentService.getByClass(this.selectedClassId).subscribe(data => this.studentsInClass = data);
    this.subjectService.getByClassAndSemester(this.selectedClassId, 'S1').subscribe(data => this.subjectsS1 = data);
    this.subjectService.getByClassAndSemester(this.selectedClassId, 'S2').subscribe(data => this.subjectsS2 = data);
  }

  onStudentChange(): void {
    this.gradesMap = {};
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.selectedStudentId) return;

    // Charge les notes déjà existantes (session MAIN) pour pré-remplir le formulaire
    this.gradeService.getByStudent(this.selectedStudentId).subscribe((grades: StudentGrade[]) => {
      grades
        .filter(g => g.sessionType === 'MAIN')
        .forEach(g => this.gradesMap[g.subjectId] = g.grade);
    });
  }

  saveGrades(): void {
    if (!this.selectedStudentId) return;

    const allSubjects = [...this.subjectsS1, ...this.subjectsS2];
    const entries: GradeEntry[] = allSubjects
      .filter(subj => this.gradesMap[subj.id!] !== null && this.gradesMap[subj.id!] !== undefined)
      .map(subj => ({ subjectId: subj.id!, grade: this.gradesMap[subj.id!] }));

    if (entries.length === 0) {
      this.errorMessage = 'Saisis au moins une note avant d\'enregistrer.';
      return;
    }

    this.gradeService.saveBatch({
      studentId: this.selectedStudentId,
      sessionType: 'MAIN',
      grades: entries
    }).subscribe({
      next: () => {
        this.successMessage = 'Notes enregistrées avec succès.';
        this.errorMessage = '';
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Erreur lors de l\'enregistrement.';
        this.successMessage = '';
      }
    });
  }
}
