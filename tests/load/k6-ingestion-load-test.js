import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 500 },  // Ramp up to 500 VUs
    { duration: '1m', target: 5000 },  // Burst load up to 5,000 req/sec SLA
    { duration: '30s', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<200'],  // 95% of requests must complete below 200ms
    http_req_failed: ['rate<0.01'],    // Error rate < 1%
  },
};

export default function () {
  const url = 'http://localhost:8080/api/v1/responses';
  const payload = JSON.stringify({
    surveyId: 'SRV-001',
    token: 'TKN-SINGLE-USE-VAL-123',
    answers: [
      { questionId: 'Q-01', score: 5 },
      { questionId: 'Q-02', text: 'Great team collaboration and support.' }
    ]
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Project-ID': 'PRJ-DEFAULT-001',
    },
  };

  const res = http.post(url, payload, params);
  check(res, {
    'is status 202 or 200': (r) => r.status === 202 || r.status === 200,
  });

  sleep(0.1);
}
