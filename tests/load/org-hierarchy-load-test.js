import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '5s', target: 50 },
    { duration: '10s', target: 500 },
    { duration: '5s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<10'], // SLA-ORG-01: sub-tree query p95 latency under 10ms
    http_req_failed: ['rate<0.01'],  // Less than 1% failure rate
  },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8082';
const PROJECT_ID = 'PRJ-99201';

export default function () {
  const headers = {
    'Content-Type': 'application/json',
    'X-Correlation-Id': `LOAD-${__VU}-${__ITER}`,
    'X-Node-Scope': ',N-001,N-101,'
  };

  // Scenario 1: Fast Sub-Tree Path Prefix Regex Query
  const subTreeRes = http.get(`${BASE_URL}/api/v1/nodes/N-101/subtree?projectId=${PROJECT_ID}`, { headers });
  check(subTreeRes, {
    'sub-tree status is 200': (r) => r.status === 200,
    'sub-tree response latency < 10ms': (r) => r.timings.duration < 10,
  });

  // Scenario 2: Ancestor Lineage Path Resolution
  const lineageRes = http.get(`${BASE_URL}/api/v1/nodes/N-301/lineage?projectId=${PROJECT_ID}`, { headers });
  check(lineageRes, {
    'lineage status is 200': (r) => r.status === 200,
    'lineage response latency < 10ms': (r) => r.timings.duration < 10,
  });

  sleep(0.1);
}
