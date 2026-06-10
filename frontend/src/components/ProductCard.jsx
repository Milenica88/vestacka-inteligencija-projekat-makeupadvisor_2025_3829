export default function ProductCard({ product }) {
  const initial = (product.brand || product.name || "?").charAt(0).toUpperCase();

  return (
    <div className="product">
      <div className="product__thumb">
        {product.image ? (
          <img src={product.image} alt={product.name} loading="lazy" />
        ) : (
          <span className="product__placeholder">{initial}</span>
        )}
      </div>
      <div className="product__body">
        <div className="product__head">
          <span className="product__brand">{product.brand}</span>
          {typeof product.score === "number" && (
            <span className="product__score">★ {product.score}</span>
          )}
        </div>
        <h4 className="product__name">{product.name}</h4>
        <div className="product__meta">
          <span className="product__type">{product.type}</span>
          {product.price != null && (
            <span className="product__price">${product.price}</span>
          )}
          {product.rating != null && (
            <span className="product__rating">rating {product.rating}</span>
          )}
        </div>
        {product.tags?.length > 0 && (
          <div className="product__tags">
            {product.tags.map((t) => (
              <span key={t} className="tag">{t}</span>
            ))}
          </div>
        )}
        {product.reasons?.length > 0 && (
          <ul className="product__reasons">
            {product.reasons.map((r, i) => (
              <li key={i}>{r}</li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}