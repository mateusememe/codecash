import { Httpx } from 'https://jslib.k6.io/httpx/0.1.0/index.js';

const graphqlHttp = new Httpx({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json",
    },
    timeout: 3000,
});

export async function createUser(name, email, document, password) {
    const query = `
        mutation CreateUser($input: CreateUserInput!) {
            createUser(input: $input) {
                id
                name
                email
                document
                account {
                    id
                    balance
                }
            }
        }
    `;

    const variables = {
        input: {
            name,
            email,
            document,
            password
        }
    };

    const payload = JSON.stringify({ query, variables });
    const response = await graphqlHttp.asyncPost('/graphql', payload);
    
    if (response.status === 200) {
        const body = JSON.parse(response.body);
        if (body.errors) {
            console.error('GraphQL errors:', JSON.stringify(body.errors));
            return null;
        }
        return body.data.createUser;
    }
    
    console.error(`Error creating user (HTTP ${response.status})`);
    return null;
}

export async function addFunds(accountId, amount) {
    const query = `
        mutation AddFunds($input: AddFundsInput!) {
            addFunds(input: $input) {
                id
                balance
            }
        }
    `;

    const variables = {
        input: {
            accountId,
            amount
        }
    };

    const payload = JSON.stringify({ query, variables });
    const response = await graphqlHttp.asyncPost('/graphql', payload);
    
    if (response.status === 200) {
        const body = JSON.parse(response.body);
        if (body.errors) {
            console.error('GraphQL errors:', JSON.stringify(body.errors));
            return null;
        }
        return body.data.addFunds;
    }
    
    console.error(`Error adding funds (HTTP ${response.status})`);
    return null;
}

export async function createTransaction(amount, payerAccountId, payeeAccountId) {
    const query = `
        mutation CreateTransaction($input: CreateTransactionInput!) {
            createTransaction(input: $input) {
                id
                amount
                transactionTime
                payerAccount {
                    id
                    balance
                }
                payeeAccount {
                    id
                    balance
                }
            }
        }
    `;

    const variables = {
        input: {
            amount,
            payerAccountId,
            payeeAccountId
        }
    };

    const payload = JSON.stringify({ query, variables });
    const response = await graphqlHttp.asyncPost('/graphql', payload);
    
    if (response.status === 200) {
        const body = JSON.parse(response.body);
        if (body.errors) {
            return { success: false, errors: body.errors };
        }
        return { success: true, transaction: body.data.createTransaction };
    }
    
    return { success: false, status: response.status };
}

export async function getAllUsers() {
    const query = `
        query {
            allUsers {
                id
                name
                email
                account {
                    id
                    balance
                }
            }
        }
    `;

    const payload = JSON.stringify({ query });
    const response = await graphqlHttp.asyncPost('/graphql', payload);
    
    if (response.status === 200) {
        const body = JSON.parse(response.body);
        if (body.errors) {
            console.error('GraphQL errors:', JSON.stringify(body.errors));
            return [];
        }
        return body.data.allUsers || [];
    }
    
    console.error(`Error fetching users (HTTP ${response.status})`);
    return [];
}

export async function getUserById(userId) {
    const query = `
        query GetUser($id: ID!) {
            userById(id: $id) {
                id
                name
                email
                document
                account {
                    id
                    balance
                }
            }
        }
    `;

    const variables = { id: userId };
    const payload = JSON.stringify({ query, variables });
    const response = await graphqlHttp.asyncPost('/graphql', payload);
    
    if (response.status === 200) {
        const body = JSON.parse(response.body);
        if (body.errors) {
            return null;
        }
        return body.data.userById;
    }
    
    return null;
}
