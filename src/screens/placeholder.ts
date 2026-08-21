export function renderPlaceholder(
  title: string,
  copy: string,
): string {
  return `
    <section class="placeholder" aria-labelledby="screen-title">
      <h1 id="screen-title" class="placeholder__title">${title}</h1>
      <p class="placeholder__copy">${copy}</p>
    </section>
  `;
}
