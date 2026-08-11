/**
 * Real-Time PII Text Scrubber Utility (FEAT-006 Section 20)
 * Scrubs emails and phone numbers from open-ended survey text responses
 * prior to event streaming and downstream sentiment processing.
 */
export function scrubPiiFromText(text: string | undefined): string | undefined {
  if (!text || typeof text !== 'string') return text;

  // Regex patterns for emails and phone numbers
  const emailPattern = /[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}/g;
  const phonePattern = /(\+\d{1,3}[-.\s]?)?\(?\d{3}\)?[-.\s]?\d{3}[-.\s]?\d{4}/g;

  return text
    .replace(emailPattern, '[REDACTED EMAIL]')
    .replace(phonePattern, '[REDACTED PHONE]');
}
