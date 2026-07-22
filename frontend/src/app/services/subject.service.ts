import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Subject, Semester } from '../models/subject.model';

@Injectable({ providedIn: 'root' })
export class SubjectService {

  private readonly baseUrl = '/api/subjects';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Subject[]> {
    return this.http.get<Subject[]>(this.baseUrl);
  }

  getByClass(academicClassId: number): Observable<Subject[]> {
    return this.http.get<Subject[]>(`${this.baseUrl}/class/${academicClassId}`);
  }

  getByClassAndSemester(academicClassId: number, semester: Semester): Observable<Subject[]> {
    return this.http.get<Subject[]>(`${this.baseUrl}/class/${academicClassId}/semester/${semester}`);
  }

  create(subject: Subject): Observable<Subject> {
    return this.http.post<Subject>(this.baseUrl, subject);
  }

  update(id: number, subject: Subject): Observable<Subject> {
    return this.http.put<Subject>(`${this.baseUrl}/${id}`, subject);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
