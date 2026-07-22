import { Semester } from './subject.model';

export type ResultStatus = 'ADMITTED' | 'CONTROL_SESSION' | 'FAILED';

export interface SubjectGradeRow {
  subjectId: number;
  subjectName: string;
  coefficient: number;
  mainGrade: number | null;
  controlGrade: number | null;
}

export interface SemesterResult {
  semester: Semester;
  subjectGrades: SubjectGradeRow[];
  semesterAverage: number | null;
  semesterAverageAfterControl: number | null;
}

export interface StudentResult {
  studentId: number;
  registrationNumber: string;
  firstName: string;
  lastName: string;
  academicClassName: string;
  semester1: SemesterResult;
  semester2: SemesterResult;
  annualAverage: number | null;
  mainSessionResult: ResultStatus | null;
  annualAverageAfterControl: number | null;
  finalResult: ResultStatus | null;
}
