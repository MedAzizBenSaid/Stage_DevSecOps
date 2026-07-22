export type SessionType = 'MAIN' | 'CONTROL';

export interface StudentGrade {
  id?: number;
  grade: number;
  studentId: number;
  studentFullName?: string;
  subjectId: number;
  subjectName?: string;
  sessionType: SessionType;
}

export interface GradeEntry {
  subjectId: number;
  grade: number | null;
}

export interface GradeBatchRequest {
  studentId: number;
  sessionType: SessionType;
  grades: GradeEntry[];
}
