import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '10s', target: 100 }, // Ramp-up to 100 VUs
    { duration: '30s', target: 500 }, // Peak load at 500 VUs (SLA-CFG-01 target)
    { duration: '10s', target: 0 },   // Ramp-down
  ],
  thresholds: {
    http_req_duration: ['p(95)<5'], // SLA-CFG-01: 95% of requests must complete in < 5ms
    http_req_failed: ['rate<0.01'],  // < 1% error rate allowed
  },
};

const BASE_URL = __ENV.TARGET_URL || 'http://localhost:8081';

export default function () {
  // Test 1: High-throughput unauthenticated cached public theme API
  const themeRes = http.get(`${BASE_URL}/api/v1/projects/PRJ-99201/public-theme`, {
    headers: {
      'Accept': 'application/json',
      'X-Correlation-ID': `k6-${__VU}-${__ITER}`,
    },
  });

  check(themeRes, {
    'Public theme status is 200': (r) => r.status === 200,
    'Cache-Control header present': (r) => r.headers['Cache-Control'] && r.headers['Cache-Control'].includes('public'),
    'Response payload valid': (r) => r.json('data.projectId') === 'PRJ-99201',
  });

  // Test 2: Authenticated cached project configuration endpoint
  const projectRes = http.get(`${BASE_URL}/api/v1/projects/PRJ-99201`, {
    headers: {
      'Accept': 'application/json',
      'X-Project-ID': 'PRJ-99201',
      'X-User-ID': 'USR-K6-LOADTEST',
      'X-Correlation-ID': `k6-${__VU}-${__ITER}`,
    },
  });

  check(projectRes, {
    'Project config status is 200': (r) => r.status === 200,
    'Project name matches': (r) => r.json('data.name') !== null,
  });

  sleep(0.1);
}
