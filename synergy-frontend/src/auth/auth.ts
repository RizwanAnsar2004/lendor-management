// Replaced by lib/token.ts — stub kept for import compatibility
export type SignUpIdentifier = { type: 'email' | 'phone'; value: string };
export async function startSignUp(_id: SignUpIdentifier) { return { username: '' }; }
export async function verifySignUp(_username: string, _code: string) { return {}; }
export async function login(_email: string, _pw: string) { return {}; }
export async function logout() {}
export async function getAccessToken(): Promise<string | null> { return null; }
export async function requestSetPassword(_email: string) {}
export async function setPasswordWithCode(_email: string, _code: string, _pw: string) {}
