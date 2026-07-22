import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AcademicClassService } from '../../services/academic-class.service';
import { AcademicClass } from '../../models/academic-class.model';

/**
 * Page de gestion des classes académiques : liste + formulaire
 * d'ajout/modification (Reactive Forms), et suppression.
 */
@Component({
  selector: 'app-class-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="d-flex justify-content-between align-items-center">
      <h2 class="page-title">Gestion des classes</h2>
      <button class="btn btn-primary" (click)="openCreateForm()">+ Nouvelle classe</button>
    </div>

    <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>

    <div class="card">
      <div class="card-body p-0">
        <table class="table table-hover mb-0">
          <thead>
            <tr>
              <th>Nom</th>
              <th>Niveau</th>
              <th>Étudiants</th>
              <th>Matières</th>
              <th class="text-end">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let c of classes">
              <td>{{ c.name }}</td>
              <td>{{ c.level }}</td>
              <td>{{ c.studentCount }}</td>
              <td>{{ c.subjectCount }}</td>
              <td class="text-end">
                <button class="btn btn-sm btn-outline-secondary me-2" (click)="openEditForm(c)">Modifier</button>
                <button class="btn btn-sm btn-outline-danger" (click)="deleteClass(c)">Supprimer</button>
              </td>
            </tr>
            <tr *ngIf="classes.length === 0">
              <td colspan="5" class="text-center text-muted py-3">Aucune classe pour le moment.</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Formulaire (affiché conditionnellement, sans modal Bootstrap JS pour rester simple) -->
    <div class="card mt-4" *ngIf="showForm">
      <div class="card-body">
        <h5>{{ editingId ? 'Modifier la classe' : 'Nouvelle classe' }}</h5>
        <form [formGroup]="classForm" (ngSubmit)="submitForm()">
          <div class="row g-3">
            <div class="col-md-6">
              <label class="form-label">Nom</label>
              <input class="form-control" formControlName="name" placeholder="Ex: GL2">
              <div class="text-danger small" *ngIf="classForm.get('name')?.touched && classForm.get('name')?.invalid">
                Le nom est obligatoire.
              </div>
            </div>
            <div class="col-md-6">
              <label class="form-label">Niveau</label>
              <input class="form-control" formControlName="level" placeholder="Ex: 2ème année">
              <div class="text-danger small" *ngIf="classForm.get('level')?.touched && classForm.get('level')?.invalid">
                Le niveau est obligatoire.
              </div>
            </div>
          </div>
          <div class="mt-3">
            <button class="btn btn-success me-2" type="submit" [disabled]="classForm.invalid">Enregistrer</button>
            <button class="btn btn-secondary" type="button" (click)="cancelForm()">Annuler</button>
          </div>
        </form>
      </div>
    </div>
  `
})
export class ClassListComponent implements OnInit {

  classes: AcademicClass[] = [];
  classForm: FormGroup;
  showForm = false;
  editingId: number | null = null;
  errorMessage = '';

  constructor(private academicClassService: AcademicClassService, private fb: FormBuilder) {
    this.classForm = this.fb.group({
      name: ['', Validators.required],
      level: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadClasses();
  }

  loadClasses(): void {
    this.academicClassService.getAll().subscribe(data => this.classes = data);
  }

  openCreateForm(): void {
    this.editingId = null;
    this.classForm.reset();
    this.showForm = true;
    this.errorMessage = '';
  }

  openEditForm(c: AcademicClass): void {
    this.editingId = c.id ?? null;
    this.classForm.patchValue({ name: c.name, level: c.level });
    this.showForm = true;
    this.errorMessage = '';
  }

  cancelForm(): void {
    this.showForm = false;
  }

  submitForm(): void {
    if (this.classForm.invalid) return;

    const payload: AcademicClass = this.classForm.value;

    const request$ = this.editingId
      ? this.academicClassService.update(this.editingId, payload)
      : this.academicClassService.create(payload);

    request$.subscribe({
      next: () => {
        this.showForm = false;
        this.loadClasses();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message ?? 'Une erreur est survenue.';
      }
    });
  }

  deleteClass(c: AcademicClass): void {
    if (!c.id) return;
    if (!confirm(`Supprimer la classe "${c.name}" ?`)) return;

    this.academicClassService.delete(c.id).subscribe({
      next: () => this.loadClasses(),
      error: (err) => this.errorMessage = err?.error?.message ?? 'Suppression impossible.'
    });
  }
}
