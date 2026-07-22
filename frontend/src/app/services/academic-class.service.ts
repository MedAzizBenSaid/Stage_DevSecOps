import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AcademicClass } from '../models/academic-class.model';

/**
 * Service HTTP pour la gestion des classes académiques.
 * Toute la communication avec le backend Spring Boot passe par ici ;
 * les composants n'appellent jamais HttpClient directement.
 */
@Injectable({ providedIn: 'root' })
export class AcademicClassService {

  private readonly baseUrl = '/api/classes';

  constructor(private http: HttpClient) {}

  getAll(): Observable<AcademicClass[]> {
    return this.http.get<AcademicClass[]>(this.baseUrl);
  }

  getById(id: number): Observable<AcademicClass> {
    return this.http.get<AcademicClass>(`${this.baseUrl}/${id}`);
  }

  create(academicClass: AcademicClass): Observable<AcademicClass> {
    return this.http.post<AcademicClass>(this.baseUrl, academicClass);
  }

  update(id: number, academicClass: AcademicClass): Observable<AcademicClass> {
    return this.http.put<AcademicClass>(`${this.baseUrl}/${id}`, academicClass);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
