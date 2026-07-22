import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { AcademicClassService } from '../../services/academic-class.service';
import { Student } from '../../models/student.model';
import { AcademicClass } from '../../models/academic-class.model';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center">
      <h2 class="page-title">Gestion des étudiants</h2>
      <button class="btn btn-primary" (click)="openCreateForm()">+ Nouvel étudiant</button>
    </div>

    <div class="card mb-3">
      <div class="card-body d-flex gap-2">
        <input class="form-control" placeholder="Rechercher par nom ou prénom..."
               [(ngModel)]="searchQuery" [ngModelOptions]="{standalone: true}">
        <button class="btn btn-outline-primary" (click)="search()">Rechercher</button>
        <button class="btn btn-outline-secondary" (click)="resetSearch()">Réinitialiser</button>
      </div>
    </div>

    <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

    <div class="card">
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead>
            <tr>
              <th>Matricule</th>
              <th>Prénom</th>
              <th>Nom</th>
              <th>Email</th>
              <th>Classe</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let s of students">
              <td>{{ s.registrationNumber }}</td>
              <td>{{ s.firstName }}</td>
              <td>{{ s.lastName }}</td>
              <td>{{ s.email }}</td>
              <td>{{ s.academicClassName }}</td>
              <td class="text-end">
                <button class="btn btn-sm btn-outline-secondary me-2" (click)="openEditForm(s)">Modifier</button>
                <button class="btn btn-sm btn-outline-danger" (click)="deleteStudent(s)">Supprimer</button>
              </td>
            </tr>
            <tr *ngIf="students.length === 0">
              <td colspan="6" class="text-center text-muted py-3">Aucun étudiant trouvé.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="card mt-4" *ngIf="showForm">
      <div class="card-body">
        <h5>{{ editingId ? 'Modifier l\\'étudiant' : 'Nouvel étudiant' }}</h5>
        <form [formGroup]="studentForm" (ngSubmit)="submitForm()">
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Matricule</label>
              <input class="form-control" formControlName="registrationNumber">
            </div>
            <div class="col-md-4">
              <label class="form-label">Prénom</label>
              <input class="form-control" formControlName="firstName">
            </div>
            <div class="col-md-4">
              <label class="form-label">Nom</label>
              <input class="form-control" formControlName="lastName">
            </div>
            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input class="form-control" type="email" formControlName="email">
            </div>
            <div class="col-md-6">
              <label class="form-label">Classe</label>
              <select class="form-select" formControlName="academicClassId">
                <option [ngValue]="null" disabled>-- Choisir une classe --</option>
                <option *ngFor="let c of allClasses" [ngValue]="c.id">{{ c.name }}</option>
              </select>
            </div>
          </div>
          <div class="mt-3">
            <button class="btn btn-success me-2" type="submit" [disabled]="studentForm.invalid">Enregistrer</button>
            <button class="btn btn-secondary" type="button" (click)="cancelForm()">Annuler</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class StudentListComponent implements OnInit {

  students: Student[] = [];
  allClasses: AcademicClass[] = [];
  studentForm: FormGroup;
  showForm = false;
  editingId: number | null = null;
  errorMessage = '';
  searchQuery = '';

  constructor(
    private studentService: StudentService,
    private academicClassService: AcademicClassService,
    private fb: FormBuilder
  ) {
    this.studentForm = this.fb.group({
      registrationNumber: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      academicClassId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadStudents();
    this.academicClassService.getAll().subscribe(data => this.allClasses = data);
  }

  loadStudents(): void {
    this.studentService.getAll().subscribe(data => this.students = data);
  }

  search(): void {
    if (!this.searchQuery.trim()) {
      this.loadStudents();
      return;
    }
    this.studentService.searchByName(this.searchQuery).subscribe(data => this.students = data);
  }

  resetSearch(): void {
    this.searchQuery = '';
    this.loadStudents();
  }

  openCreateForm(): void {
    this.editingId = null;
    this.studentForm.reset({ academicClassId: null });
    this.showForm = true;
    this.errorMessage = '';
  }

  openEditForm(s: Student): void {
    this.editingId = s.id ?? null;
    this.studentForm.patchValue(s);
    this.showForm = true;
    this.errorMessage = '';
  }

  cancelForm(): void {
    this.showForm = false;
  }

  submitForm(): void {
    if (this.studentForm.invalid) return;
    const payload: Student = this.studentForm.value;

    const request$ = this.editingId
      ? this.studentService.update(this.editingId, payload)
      : this.studentService.create(payload);

    request$.subscribe({
      next: () => {
        this.showForm = false;
        this.loadStudents();
      },
      error: (err) => this.errorMessage = err?.error?.message ?? 'Une erreur est survenue.'
    });
  }

  deleteStudent(s: Student): void {
    if (!s.id) return;
    if (!confirm(`Supprimer l'étudiant ${s.firstName} ${s.lastName} ?`)) return;

    this.studentService.delete(s.id).subscribe({
      next: () => this.loadStudents(),
      error: (err) => this.errorMessage = err?.error?.message ?? 'Suppression impossible.'
    });
  }
}
