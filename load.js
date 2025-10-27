import { textSummary } from "https://jslib.k6.io/k6-summary/0.1.0/index.js";
import { randomString } from "https://jslib.k6.io/k6-utils/1.4.0/index.js";
import { sleep } from "k6";
import { Counter, Trend } from "k6/metrics";
import { SharedArray } from "k6/data";
import {
  createUser,
  addFunds,
  createTransaction,
  getAllUsers
} from "./requests.js";

const VUS = __ENV.VUS ?? 50;
const DURATION = __ENV.DURATION ?? "30s";

export const options = {
  summaryTrendStats: [
    "avg",
    "min",
    "med",
    "max",
    "p(90)",
    "p(95)",
    "p(99)",
    "count",
  ],
  thresholds: {
    http_req_duration: ['p(95) < 500'],
    'transaction_errors': ['count < 100'],
  },
  scenarios: {
    setup_users: {
      exec: "setupUsers",
      executor: "per-vu-iterations",
      vus: 10,
      iterations: 1,
      startTime: "0s",
    },
    transactions: {
      exec: "runTransactions",
      executor: "ramping-vus",
      startVUs: 1,
      startTime: "5s",
      gracefulRampDown: "5s",
      stages: [
        { target: VUS, duration: "10s" },
        { target: VUS, duration: DURATION },
        { target: 0, duration: "5s" },
      ],
    },
  },
};

const usersCreatedCounter = new Counter("users_created");
const usersCreationErrorsCounter = new Counter("users_creation_errors");
const fundsAddedCounter = new Counter("funds_added");
const transactionsSuccessCounter = new Counter("transactions_success");
const transactionsFailureCounter = new Counter("transactions_failure");
const transactionErrorsCounter = new Counter("transaction_errors");
const insufficientFundsCounter = new Counter("insufficient_funds_errors");
const totalTransactionsAmountCounter = new Counter("total_transactions_amount");

const transactionDuration = new Trend("transaction_duration");

let userAccounts = new SharedArray("userAccounts", function () {
  return [];
});

export async function setupUsers() {
  const vuId = __VU;
  const iterationId = __ITER;
  const uniqueId = `${vuId}-${iterationId}-${Date.now()}`;
  
  const name = `User ${randomString(8)}`;
  const email = `user-${uniqueId}@test.com`;
  const document = randomString(11, '0123456789');
  const password = 'password123';

  console.log(`Creating user: ${email}`);
  const user = await createUser(name, email, document, password);
  
  if (user && user.account) {
    usersCreatedCounter.add(1);
    console.log(`User created: ${user.id}, Account: ${user.account.id}`);
    
    const initialFunds = Math.floor(Math.random() * 10000) + 1000;
    const fundedAccount = await addFunds(user.account.id, initialFunds);
    
    if (fundedAccount) {
      fundsAddedCounter.add(initialFunds);
      console.log(`Added ${initialFunds} to account ${fundedAccount.id}, new balance: ${fundedAccount.balance}`);
    }
  } else {
    usersCreationErrorsCounter.add(1);
    console.error(`Failed to create user: ${email}`);
  }
  
  sleep(0.5);
}

export async function runTransactions() {
  const users = await getAllUsers();
  
  if (!users || users.length < 2) {
    console.error('Not enough users to perform transactions');
    sleep(1);
    return;
  }

  const payerIndex = Math.floor(Math.random() * users.length);
  let payeeIndex = Math.floor(Math.random() * users.length);
  
  while (payeeIndex === payerIndex) {
    payeeIndex = Math.floor(Math.random() * users.length);
  }
  
  const payer = users[payerIndex];
  const payee = users[payeeIndex];
  
  if (!payer.account || !payee.account) {
    console.error('Users without accounts found');
    sleep(1);
    return;
  }
  
  const amount = Math.floor(Math.random() * 100) + 10;
  
  const startTime = Date.now();
  const result = await createTransaction(
    amount,
    payer.account.id,
    payee.account.id
  );
  const duration = Date.now() - startTime;
  
  transactionDuration.add(duration);
  
  if (result.success) {
    transactionsSuccessCounter.add(1);
    totalTransactionsAmountCounter.add(amount);
  } else {
    transactionsFailureCounter.add(1);
    
    if (result.errors) {
      const errorMessage = JSON.stringify(result.errors);
      if (errorMessage.includes('Insufficient funds') || errorMessage.includes('Saldo insuficiente')) {
        insufficientFundsCounter.add(1);
      } else {
        transactionErrorsCounter.add(1);
        console.error(`Transaction error: ${errorMessage}`);
      }
    } else {
      transactionErrorsCounter.add(1);
    }
  }
  
  sleep(Math.random() * 2);
}

export function handleSummary(data) {
  const usersCreated = data.metrics.users_created?.values?.count || 0;
  const usersCreationErrors = data.metrics.users_creation_errors?.values?.count || 0;
  const totalFundsAdded = data.metrics.funds_added?.values?.count || 0;
  const transactionsSuccess = data.metrics.transactions_success?.values?.count || 0;
  const transactionsFailure = data.metrics.transactions_failure?.values?.count || 0;
  const transactionErrors = data.metrics.transaction_errors?.values?.count || 0;
  const insufficientFundsErrors = data.metrics.insufficient_funds_errors?.values?.count || 0;
  const totalTransactionsAmount = data.metrics.total_transactions_amount?.values?.count || 0;

  const httpReqDuration = data.metrics.http_req_duration?.values;
  const p95 = httpReqDuration?.['p(95)'] || 0;
  const p99 = httpReqDuration?.['p(99)'] || 0;
  
  const successRate = transactionsSuccess / (transactionsSuccess + transactionsFailure) * 100 || 0;

  const custom_data = {
    test_info: {
      test_name: "CodeCash GraphQL Load Test",
      vus: VUS,
      duration: DURATION,
      timestamp: new Date().toISOString(),
    },
    users: {
      created: usersCreated,
      creation_errors: usersCreationErrors,
      total_funds_added: totalFundsAdded,
    },
    transactions: {
      successful: transactionsSuccess,
      failed: transactionsFailure,
      insufficient_funds_errors: insufficientFundsErrors,
      other_errors: transactionErrors,
      total_amount_transacted: totalTransactionsAmount,
      success_rate: `${successRate.toFixed(2)}%`,
    },
    performance: {
      p95_ms: p95?.toFixed(2),
      p99_ms: p99?.toFixed(2),
      avg_ms: httpReqDuration?.avg?.toFixed(2),
    },
    summary: {
      status: p95 < 500 && transactionErrors < 100 ? "PASSED ✓" : "FAILED ✗",
      description: "GraphQL API performance test for CodeCash transaction system"
    }
  };

  const result = {
    stdout: textSummary(data),
  };

  result['./graphql-test-results.json'] = JSON.stringify(custom_data, null, 2);

  return result;
}
