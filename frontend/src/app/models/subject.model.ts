export type Semester = 'S1' | 'S2';

export interface Subject {
  id?: number;
  name: string;
  coefficient: number;
  semester: Semester;
  academicClassId: number;
  academicClassName?: string;
}
