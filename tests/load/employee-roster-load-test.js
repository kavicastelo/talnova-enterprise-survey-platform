import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 20 },
    { duration: '30s', target: 50 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<50'], // 95% of queries must complete in < 50ms (SLA-EMP-01, SLA-EMP-02)
    http_req_failed: ['rate<0.01'],  // Less than 1% failure rate
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8083';
const PROJECT_ID = 'PRJ-99201';

export default function () {
  // Test 1: High-throughput employee search lookup
  const employeeId = `EMP-${10020 + (Math.floor(Math.random() * 1000))}`;
  const res = http.get(`${BASE_URL}/api/v1/employees/${employeeId}?projectId=${PROJECT_ID}`);

  check(res, {
    'status is 200 or 404': (r) => r.status === 200 || r.status === 404,
    'response time < 50ms': (r) => r.timings.duration < 50,
  });

  sleep(0.1);
}
