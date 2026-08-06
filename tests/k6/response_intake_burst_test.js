import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  scenarios: {
    burst_load: {
      executor: 'constant-arrival-rate',
      rate: 5000,
      timeUnit: '1s',
      duration: '30s',
      preAllocatedVUs: 200,
      maxVUs: 1000,
    },
  },
  thresholds: {
    http_req_duration: ['p(99.9)<20'], // NFR-INT-001: 99.9th percentile latency < 20ms
    http_req_failed: ['rate<0.0001'],  // NFR-INT-002: Error rate < 0.01%
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8086/api/v1/responses';

export default function () {
  const payload = JSON.stringify({
    projectId: 'PRJ-BURST-99',
    campaignId: 'CMP-BURST-100',
    surveyId: 'SRV-BURST-500',
    surveyVersion: 1,
    respondentType: 'SEMI_ANONYMOUS',
    responseToken: `TKN-BURST-${Math.floor(Math.random() * 1000000)}`,
    nodeId: 'N-COLOMBO-01',
    answers: [
      { questionId: 'Q-101', questionType: 'LIKERT', numericValue: 5 },
      { questionId: 'Q-102', questionType: 'LONG_TEXT', textValue: 'Solid employee engagement experience.' }
    ]
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Project-ID': 'PRJ-BURST-99',
    },
  };

  const res = http.post(BASE_URL, payload, params);

  check(res, {
    'status is 202 or 200': (r) => r.status === 202 || r.status === 200,
  });

  sleep(0.1);
}
