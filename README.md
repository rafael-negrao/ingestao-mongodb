# Projeto exemplo para fazer leitura/gravação de dados no Mongodb usando Spark 

## Pré-requisitos

- Java 8 ou Java 11
- Maven 3
- Scala 2.12.17
- Spark 2.4.3

## Ambiente de desenvolvimento

### Subir o mongodb
Ir até o diretório `./docker-compose/mongodb` e subir o mongodb
- Executar o comando abaixo:
```shell script
docker-compose up
```
Para parar o mongodb basta parar o processo precionando `Ctrl + C`

### Profiles

Existem dois profiles neste projeto maven
- **dev**: Deixar este profile ativo no IDE. Desta forma é possível rodar o projeto local na máquina se for necessário.
- **prepare-deploy**: Usar este profile para empacotar o jar

## Como compilar e fazer o deploy no ambiente produtivo

### Como compilar/empacotar

- **Passo 1**, executar o comando abaixo:
```shell script
cd /home/seu_usuario/diretorio_de_sua_preferencia
```

- **Passo 2**, executar o comando abaixo:
```shell script
git clone URL ingestao-mongodb
```

- **Passo 3**, executar o comando abaixo:
```shell script
cd ingestao-mongodb
```

- **Passo 4**, fazer a compilação via maven executando o comando:
```shell script
mvn clean install -Pprepare-deploy
```
O jar gerado conterá todas as dependências do projeto, assim ficará mais simples o submit para o Spark. 

### Como fazer o submit para o Spark

```shell script
spark-submit --name app_ingestao-mongodb \
--class br.com.ajuda.IngestaoDadosMongoDB target/ingestao-mongodb-1.0.0.0.jar
```
