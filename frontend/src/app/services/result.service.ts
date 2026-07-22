import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StudentResult } from '../models/result.model';
import { Student } from '../models/student.model';

@Injectable({ providedIn: 'root' })
export class ResultService {

  private readonly baseUrl = '/api/results';

  constructor(private http: HttpClient) {}

  getStudentResult(studentId: number): Observable<StudentResult> {
    return this.http.get<StudentResult>(`${this.baseUrl}/${studentId}`);
  }

  getStudentsRequiringControlSession(): Observable<Student[]> {
    return this.http.get<Student[]>(`${this.baseUrl}/control-session-students`);
  }
}
