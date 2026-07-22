import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { SubjectService } from '../../services/subject.service';
import { AcademicClassService } from '../../services/academic-class.service';
import { Subject } from '../../models/subject.model';
import { AcademicClass } from '../../models/academic-class.model';

@Component({
  selector: 'app-subject-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center">
      <h2 class="page-title">Gestion des matières</h2>
      <button class="btn btn-primary" (click)="openCreateForm()">+ Nouvelle matière</button>
    </div>

    <div class="card mb-3">
      <div class="card-body">
        <label class="form-label">Filtrer par classe</label>
        <select class="form-select w-auto d-inline-block" (change)="onFilterClassChange($event)">
          <option value="">Toutes les classes</option>
          <option *ngFor="let c of allClasses" [value]="c.id">{{ c.name }}</option>
        </select>
      </div>
    </div>

    <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

    <div class="card">
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead>
            <tr>
              <th>Nom</th>
              <th>Coefficient</th>
              <th>Semestre</th>
              <th>Classe</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let s of subjects">
              <td>{{ s.name }}</td>
              <td>{{ s.coefficient }}</td>
              <td>{{ s.semester }}</td>
              <td>{{ s.academicClassName }}</td>
              <td class="text-end">
                <button class="btn btn-sm btn-outline-secondary me-2" (click)="openEditForm(s)">Modifier</button>
                <button class="btn btn-sm btn-outline-danger" (click)="deleteSubject(s)">Supprimer</button>
              </td>
            </tr>
            <tr *ngIf="subjects.length === 0">
              <td colspan="5" class="text-center text-muted py-3">Aucune matière trouvée.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="card mt-4" *ngIf="showForm">
      <div class="card-body">
        <h5>{{ editingId ? 'Modifier la matière' : 'Nouvelle matière' }}</h5>
        <form [formGroup]="subjectForm" (ngSubmit)="submitForm()">
          <div class="row g-3">
            <div class="col-md-4">
              <label class="form-label">Nom</label>
              <input class="form-control" formControlName="name" placeholder="Ex: Java">
            </div>
            <div class="col-md-2">
              <label class="form-label">Coefficient</label>
              <input class="form-control" type="number" step="0.5" min="0.01" formControlName="coefficient">
            </div>
            <div class="col-md-3">
              <label class="form-label">Semestre</label>
              <select class="form-select" formControlName="semester">
                <option value="S1">Semestre 1</option>
                <option value="S2">Semestre 2</option>
              </select>
            </div>
            <div class="col-md-3">
              <label class="form-label">Classe</label>
              <select class="form-select" formControlName="academicClassId">
                <option [ngValue]="null" disabled>-- Choisir --</option>
                <option *ngFor="let c of allClasses" [ngValue]="c.id">{{ c.name }}</option>
              </select>
            </div>
          </div>
          <div class="mt-3">
            <button class="btn btn-success me-2" type="submit" [disabled]="subjectForm.invalid">Enregistrer</button>
            <button class="btn btn-secondary" type="button" (click)="cancelForm()">Annuler</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class SubjectListComponent implements OnInit {

  subjects: Subject[] = [];
  allClasses: AcademicClass[] = [];
  subjectForm: FormGroup;
  showForm = false;
  editingId: number | null = null;
  errorMessage = '';
  currentClassFilter: number | null = null;

  constructor(
    private subjectService: SubjectService,
    private academicClassService: AcademicClassService,
    private fb: FormBuilder
  ) {
    this.subjectForm = this.fb.group({
      name: ['', Validators.required],
      coefficient: [1, [Validators.required, Validators.min(0.01)]],
      semester: ['S1', Validators.required],
      academicClassId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadSubjects();
    this.academicClassService.getAll().subscribe(data => this.allClasses = data);
  }

  loadSubjects(): void {
    if (this.currentClassFilter) {
      this.subjectService.getByClass(this.currentClassFilter).subscribe(data => this.subjects = data);
    } else {
      this.subjectService.getAll().subscribe(data => this.subjects = data);
    }
  }

  onFilterClassChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    this.currentClassFilter = value ? Number(value) : null;
    this.loadSubjects();
  }

  openCreateForm(): void {
    this.editingId = null;
    this.subjectForm.reset({ semester: 'S1', coefficient: 1, academicClassId: null });
    this.showForm = true;
    this.errorMessage = '';
  }

  openEditForm(s: Subject): void {
    this.editingId = s.id ?? null;
    this.subjectForm.patchValue(s);
    this.showForm = true;
    this.errorMessage = '';
  }

  cancelForm(): void {
    this.showForm = false;
  }

  submitForm(): void {
    if (this.subjectForm.invalid) return;
    const payload: Subject = this.subjectForm.value;

    const request$ = this.editingId
      ? this.subjectService.update(this.editingId, payload)
      : this.subjectService.create(payload);

    request$.subscribe({
      next: () => {
        this.showForm = false;
        this.loadSubjects();
      },
      error: (err) => this.errorMessage = err?.error?.message ?? 'Une erreur est survenue.'
    });
  }

  deleteSubject(s: Subject): void {
    if (!s.id) return;
    if (!confirm(`Supprimer la matière "${s.name}" ?`)) return;

    this.subjectService.delete(s.id).subscribe({
      next: () => this.loadSubjects(),
      error: (err) => this.errorMessage = err?.error?.message ?? 'Suppression impossible.'
    });
  }
}
