import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ClassListComponent } from './components/classes/class-list.component';
import { StudentListComponent } from './components/students/student-list.component';
import { SubjectListComponent } from './components/subjects/subject-list.component';
import { MainSessionGradesComponent } from './components/grades/main-session-grades.component';
import { ControlSessionGradesComponent } from './components/grades/control-session-grades.component';
import { StudentResultComponent } from './components/results/student-result.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'classes', component: ClassListComponent },
  { path: 'students', component: StudentListComponent },
  { path: 'subjects', component: SubjectListComponent },
  { path: 'grades/main-session', component: MainSessionGradesComponent },
  { path: 'grades/control-session', component: ControlSessionGradesComponent },
  { path: 'results', component: StudentResultComponent },
  { path: '**', redirectTo: 'dashboard' }
];
