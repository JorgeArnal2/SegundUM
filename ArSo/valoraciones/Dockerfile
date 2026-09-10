FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["valoraciones.csproj", "./"]
RUN dotnet restore "./valoraciones.csproj"
COPY . .
RUN dotnet build "valoraciones.csproj" -c Release -o /app/build
RUN dotnet publish "valoraciones.csproj" -c Release -o /app/publish

FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "valoraciones.dll"]
