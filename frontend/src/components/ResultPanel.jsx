import ProductCard from "./ProductCard";

export default function ResultPanel({ result }) {
  if (!result) return null;
  const { style, products, profile } = result;

  return (
    <div className="result">
      <section className="advice">
        <h2>Your recommendation</h2>
        {profile && (
          <p className="advice__profile">
            {profile["skin-type"]} skin · {profile.undertone} undertone · {profile.tone} tone · {profile.occasion}
          </p>
        )}
        <div className="advice__grid">
          <div className="advice__item">
            <span className="advice__label">Foundation</span>
            <strong>{style.foundation.name}</strong>
            <small>{style.foundation.description}</small>
          </div>
          <div className="advice__item">
            <span className="advice__label">Lipstick</span>
            <strong>{style.lipstick}</strong>
          </div>
          <div className="advice__item">
            <span className="advice__label">Blush</span>
            <strong>{style.blush}</strong>
          </div>
          <div className="advice__item">
            <span className="advice__label">Eyes</span>
            <strong>{style.eyes}</strong>
          </div>
        </div>
        {style.tip && <p className="advice__tip">💡 {style.tip}</p>}
      </section>

      <section className="products">
        <h3>Recommended products</h3>
        {products?.length > 0 ? (
          <div className="products__grid">
            {products.map((p, i) => (
              <ProductCard key={p.id ?? i} product={p} />
            ))}
          </div>
        ) : (
          <p className="muted">No products in the catalog.</p>
        )}
      </section>
    </div>
  );
}