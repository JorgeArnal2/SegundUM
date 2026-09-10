CREATE DATABASE IF NOT EXISTS arso_usuarios;
CREATE DATABASE IF NOT EXISTS arso_productos;
GRANT ALL PRIVILEGES ON arso_usuarios.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON arso_productos.* TO 'root'@'%';
FLUSH PRIVILEGES;
