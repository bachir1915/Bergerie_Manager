import { Component, input } from '@angular/core';

@Component({
  selector: 'app-placeholder',
  template: `
    <div class="coming-soon">
      <i class="bi bi-cone-striped"></i>
      <h2>{{ title() }}</h2>
      <p>Cette page arrive dans un prochain incrément.</p>
    </div>
  `
})
export class Placeholder {
  readonly title = input('Bientôt disponible');
}
