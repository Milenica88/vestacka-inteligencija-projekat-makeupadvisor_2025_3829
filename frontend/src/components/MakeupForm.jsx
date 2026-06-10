import { useState } from "react";

const SKIN_TYPES = [
  { value: "dry", label: "Dry" },
  { value: "oily", label: "Oily" },
  { value: "combination", label: "Combination" },
  { value: "sensitive", label: "Sensitive" },
  { value: "normal", label: "Normal" },
];

const UNDERTONES = [
  { value: "warm", label: "Warm" },
  { value: "neutral", label: "Neutral" },
  { value: "cool", label: "Cool" },
];

const SKIN_TONES = [
  { value: "fair", label: "Fair" },
  { value: "medium", label: "Medium" },
  { value: "deep", label: "Deep" },
];

const OCCASIONS = [
  { value: "everyday", label: "Everyday" },
  { value: "work", label: "Work" },
  { value: "evening", label: "Evening" },
  { value: "party", label: "Party" },
  { value: "wedding", label: "Wedding" },
];

const PREFERENCES = [
  { value: "vegan", label: "Vegan" },
  { value: "cruelty free", label: "Cruelty-free" },
  { value: "matte", label: "Matte finish" },
  { value: "dewy", label: "Dewy finish" },
  { value: "budget", label: "Budget-friendly" },
  { value: "longwear", label: "Long-wear" },
];

export default function MakeupForm({ onSubmit, loading }) {
  const [skinType, setSkinType] = useState("oily");
  const [undertone, setUndertone] = useState("warm");
  const [skinTone, setSkinTone] = useState("fair");
  const [occasion, setOccasion] = useState("evening");
  const [preferences, setPreferences] = useState(["vegan"]);

  const togglePreference = (value) => {
    setPreferences((prev) =>
      prev.includes(value) ? prev.filter((p) => p !== value) : [...prev, value]
    );
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({
      "skin-type": skinType,
      undertone: undertone,
      "skin-tone": skinTone,
      occasion: occasion,
      preferences: preferences,
    });
  };

  return (
    <form className="form" onSubmit={handleSubmit}>
      <div className="field">
        <label>Skin type</label>
        <select value={skinType} onChange={(e) => setSkinType(e.target.value)}>
          {SKIN_TYPES.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </div>

      <div className="field">
        <label>Undertone</label>
        <select value={undertone} onChange={(e) => setUndertone(e.target.value)}>
          {UNDERTONES.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </div>

      <div className="field">
        <label>Skin tone / depth</label>
        <select value={skinTone} onChange={(e) => setSkinTone(e.target.value)}>
          {SKIN_TONES.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </div>

      <div className="field">
        <label>Occasion</label>
        <select value={occasion} onChange={(e) => setOccasion(e.target.value)}>
          {OCCASIONS.map((o) => (
            <option key={o.value} value={o.value}>{o.label}</option>
          ))}
        </select>
      </div>

      <div className="field">
        <label>Preferences</label>
        <div className="chips">
          {PREFERENCES.map((o) => (
            <button
              type="button"
              key={o.value}
              className={`chip ${preferences.includes(o.value) ? "chip--on" : ""}`}
              onClick={() => togglePreference(o.value)}
            >
              {o.label}
            </button>
          ))}
        </div>
      </div>

      <button className="submit" type="submit" disabled={loading}>
        {loading ? "Finding your match..." : "Recommend my makeup"}
      </button>
    </form>
  );
}