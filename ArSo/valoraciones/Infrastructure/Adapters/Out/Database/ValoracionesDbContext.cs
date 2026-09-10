using Microsoft.EntityFrameworkCore;
using valoraciones.Core.Domain;

namespace valoraciones.Infrastructure.Adapters.Out.Database
{
    public class ValoracionesDbContext : DbContext
    {
        public ValoracionesDbContext(DbContextOptions<ValoracionesDbContext> options)
            : base(options)
        {
        }

        public DbSet<Valoracion> Valoraciones { get; set; } = null!;

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            
            modelBuilder.Entity<Valoracion>(entity =>
            {
                entity.ToTable("Valoraciones");
                entity.HasKey(e => e.Id);
                entity.Property(e => e.Id).ValueGeneratedOnAdd();
                entity.Property(e => e.IdCompraventa).IsRequired().HasMaxLength(50);
                entity.Property(e => e.IdValorado).IsRequired().HasMaxLength(50);
                entity.Property(e => e.IdValorador).IsRequired().HasMaxLength(50);
                entity.Property(e => e.RolValorado).IsRequired().HasMaxLength(20);
                entity.Property(e => e.Puntuacion).IsRequired();
                entity.Property(e => e.Comentario).IsRequired(false).HasMaxLength(500);
                entity.Property(e => e.FechaCreacion).IsRequired();
            });
        }
    }
}
