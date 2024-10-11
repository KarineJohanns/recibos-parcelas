# Usar uma imagem oficial do JDK como base
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho no contêiner
WORKDIR /app

# Copiar o arquivo JAR para o contêiner (ajuste o nome do JAR se necessário)
COPY target/recibos-0.0.1-SNAPSHOT.jar app.jar

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
