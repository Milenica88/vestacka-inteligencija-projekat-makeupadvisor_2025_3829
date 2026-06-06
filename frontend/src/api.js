const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:3001";

async function handle(res) {
  if (!res.ok) {
    throw new Error(`Server error: ${res.status}`);
  }
  return res.json();
}

export async function getRecommendation(request) {
  const res = await fetch(`${BASE_URL}/recommend`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });
  return handle(res);
}

export async function getHistory() {
  const res = await fetch(`${BASE_URL}/history`);
  return handle(res);
}

export async function getProducts() {
  const res = await fetch(`${BASE_URL}/products`);
  return handle(res);
}