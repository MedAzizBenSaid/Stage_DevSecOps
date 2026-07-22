import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GradeBatchRequest, StudentGrade } from '../models/grade.model';

@Injectable({ providedIn: 'root' })
export class GradeService {

  private readonly baseUrl = '/api/grades';

  constructor(private http: HttpClient) {}

  getByStudent(studentId: number): Observable<StudentGrade[]> {
    return this.http.get<StudentGrade[]>(`${this.baseUrl}/student/${studentId}`);
  }

  /**
   * Enregistre toutes les notes d'un étudiant (session MAIN ou CONTROL)
   * en une seule requête, comme demandé dans le cahier des charges.
   */
  saveBatch(request: GradeBatchRequest): Observable<StudentGrade[]> {
    return this.http.post<StudentGrade[]>(`${this.baseUrl}/batch`, request);
  }

  update(id: number, grade: StudentGrade): Observable<StudentGrade> {
    return this.http.put<StudentGrade>(`${this.baseUrl}/${id}`, grade);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
