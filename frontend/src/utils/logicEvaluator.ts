import { LogicRule } from '../types/survey';

export function evaluateLogicRule(
  userAnswer: { numericValue?: number; textValue?: string; selectedOptions?: string[] } | undefined,
  rule: LogicRule
): boolean {
  if (!userAnswer) {
    return false;
  }

  const compVal = rule.comparisonValue;

  switch (rule.operator) {
    case 'EQUALS':
      if (userAnswer.numericValue !== undefined) {
        return userAnswer.numericValue === parseFloat(compVal);
      }
      if (userAnswer.textValue !== undefined) {
        return userAnswer.textValue === compVal;
      }
      if (userAnswer.selectedOptions) {
        return userAnswer.selectedOptions.includes(compVal);
      }
      return false;

    case 'NOT_EQUALS':
      if (userAnswer.numericValue !== undefined) {
        return userAnswer.numericValue !== parseFloat(compVal);
      }
      if (userAnswer.textValue !== undefined) {
        return userAnswer.textValue !== compVal;
      }
      if (userAnswer.selectedOptions) {
        return !userAnswer.selectedOptions.includes(compVal);
      }
      return true;

    case 'LESS_THAN':
      if (userAnswer.numericValue !== undefined) {
        return userAnswer.numericValue < parseFloat(compVal);
      }
      return false;

    case 'GREATER_THAN':
      if (userAnswer.numericValue !== undefined) {
        return userAnswer.numericValue > parseFloat(compVal);
      }
      return false;

    default:
      return false;
  }
}

export function evaluateBranchingTargetPage(
  answers: Record<string, { numericValue?: number; textValue?: string; selectedOptions?: string[] }>,
  logicRules?: LogicRule[]
): string | null {
  if (!logicRules || logicRules.length === 0) {
    return null;
  }

  for (const rule of logicRules) {
    if (evaluateLogicRule(answers[rule.ruleId], rule)) {
      return rule.targetPageId;
    }
  }

  return null;
}
