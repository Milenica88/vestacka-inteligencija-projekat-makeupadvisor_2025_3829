import { useState } from "react";
import "./App.css";
import MakeupForm from "./MakeupForm";
import ResultPanel from "./components/ResultPanel";
import { getRecommendation } from "./api";

function App() {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async (request) => {
    setLoading(true);
    setError(null);
    try {
      const data = await getRecommendation(request);
      setResult(data);
    } catch (e) {
      setError("Could not get a recommendation. Make sure the backend is running on port 3001.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">
      <header className="hero">
        <h1>AI Makeup Advisor</h1>
        <p>
          Personalized makeup recommendations based on your skin type,
          undertone, tone, preferences, and occasion.
        </p>
      </header>

      <main className="layout">
        <div className="panel">
          <MakeupForm onSubmit={handleSubmit} loading={loading} />
        </div>

        <div className="panel panel--result">
          {error && <div className="error">{error}</div>}
          {!result && !error && (
            <div className="empty">
              <p>Fill in your profile on the left and your recommendation will appear here.</p>
            </div>
          )}
          <ResultPanel result={result} />
        </div>
      </main>

      <footer className="footer">
        Project for Tools and Methods of Artificial Intelligence and Software Engineering · FON
      </footer>
    </div>
  );
}

export default App;