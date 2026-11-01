import { Directive, ElementRef, Input, OnDestroy, OnInit, inject } from '@angular/core';

/**
 * Apparition progressive d'un bloc quand il entre dans l'écran (équivalent de « fadeInUp / fadeInDown /
 * fadeInLeft / fadeInRight » déclenchés au défilement). Usage : appReveal="up|down|left|right|zoom".
 * Les styles sont dans styles.scss (classes .reveal et .reveal-in). Respecte « réduire les animations ».
 */
@Directive({ selector: '[appReveal]', standalone: true })
export class RevealDirective implements OnInit, OnDestroy {
  @Input('appReveal') effet = 'up';
  @Input() revealDelay?: number;

  private readonly el = inject<ElementRef<HTMLElement>>(ElementRef);
  private observer?: IntersectionObserver;

  ngOnInit(): void {
    const e = this.el.nativeElement;
    e.classList.add('reveal');
    e.setAttribute('data-reveal', this.effet || 'up');
    if (this.revealDelay != null) {
      e.style.animationDelay = `${this.revealDelay}ms`;
    }
    const reduit = typeof matchMedia === 'function' && matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (typeof IntersectionObserver === 'undefined' || reduit) {
      e.classList.add('reveal-in');
      return;
    }
    this.observer = new IntersectionObserver(
      (entries) => {
        if (entries.some((en) => en.isIntersecting)) {
          e.classList.add('reveal-in');
          this.observer?.disconnect();
        }
      },
      { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
    );
    this.observer.observe(e);
  }

  ngOnDestroy(): void {
    this.observer?.disconnect();
  }
}
