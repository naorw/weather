export function rangeBar(
  low: number,
  high: number,
  min: number,
  max: number,
): { startPercent: number; widthPercent: number } {
  const span = max - min;
  if (span === 0) {
    return { startPercent: 0, widthPercent: 100 };
  }
  return {
    startPercent: ((low - min) / span) * 100,
    widthPercent: ((high - low) / span) * 100,
  };
}
