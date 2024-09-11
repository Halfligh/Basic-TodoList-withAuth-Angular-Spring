import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'roleTransform',
  standalone: true, // Déclaration standalone
})
export class RolePipe implements PipeTransform {
  transform(value: string): string {
    if (value === 'ROLE_USER') {
      return 'utilisateur';
    } else if (value === 'ROLE_ADMIN') {
      return 'administrateur';
    }
    return value;
  }
}