import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'pokedexNumber',
})
export class PokedexNumberPipe implements PipeTransform {
  transform(value: number): string {
    return '#' + value.toString().padStart(3, '0');
  }
}
