type FeatureCardProps = {
  title: string
  description: string
}

export function FeatureCard({ title, description }: FeatureCardProps) {
  return (
    <article className="card h-100 border-0 shadow-sm">
      <div className="card-body p-4">
        <h3 className="h5">{title}</h3>
        <p className="text-secondary mb-0">{description}</p>
      </div>
    </article>
  )
}
