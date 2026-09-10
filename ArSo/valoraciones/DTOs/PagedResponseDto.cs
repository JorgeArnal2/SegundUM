namespace valoraciones.DTOs
{
    public class PagedResponseDto<T>
    {
        public IEnumerable<T> Items { get; set; } = new List<T>();
        public int TotalItems { get; set; }
        public int Page { get; set; }
        public int Size { get; set; }
        public List<LinkDto> Links { get; set; } = new List<LinkDto>();
    }
}
