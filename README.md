# Garage App

Aplicação de gerenciamento de estacionamento, permitindo registrar entradas e saídas de veículos, consultar vagas e faturamento por setor.

---

## Requisitos

- Docker e Docker Compose

---

## Rodando a aplicação com Docker

O projeto inclui um `docker-compose.yml` com três serviços:

1. **PostgreSQL** (`garage-db`)
2. **pgAdmin** (`garage-pgadmin`)
3. **Garage App** (`garage-app`) - aplicação Spring Boot

### Passos

1. Subir todos os serviços:

```bash
docker-compose up -d --build
```


Garage App será acessível em http://localhost:8081/garage

pgAdmin será acessível em http://localhost:8080

Email: admin@garage.com
Senha: garage

Conectar pgAdmin ao PostgreSQL:
Host: postgres (nome do serviço no Docker Compose)
Port: 5432

Database: garage_db
Username: garage
Password: garage

### Endpoints da aplicação 

1. Registrar evento (entrada ou saída)

POST /garage/webhook

Registrar entrada:
```bash
curl -X POST http://localhost:8081/garage/webhook \
-H "Content-Type: application/json" \
-d '{
"vehiclePlate": "ABC-1234",
"eventType": "ENTRY",
"entryTime": "2025-09-29T10:00:00"
}'
```

Registrar saída:

```bash
curl -X POST http://localhost:8081/garage/webhook \
-H "Content-Type: application/json" \
-d '{
"vehiclePlate": "ABC-1234",
"eventType": "EXIT",
"exitTime": "2025-09-29T12:30:00"
}'
```

2. Consultar status de um veículo

GET /garage/plate-status?plate=ABC-1234

```bash
curl http://localhost:8081/garage/plate-status?plate=ABC-1234
Exemplo de resposta quando o veículo está estacionado:

{
"vehiclePlate": "ABC-1234",
"eventType": "PARKED"
}
```

3. Consultar vagas disponíveis/ocupadas
```bash
GET /garage/spot-status (todas as vagas)

curl http://localhost:8081/garage/spot-status
```

GET /garage/spot-status?sector=A (vagas de um setor específico)
```bash
curl http://localhost:8081/garage/spot-status?sector=A
```

Exemplo de resposta:

[
{
"sector": "A",
"occupied": true,
"vehiclePlate": "ABC-1234"
},
{
"sector": "A",
"occupied": false,
"vehiclePlate": null
}
]

4. Consultar faturamento

GET /garage/revenue?sector=A&date=2025-09-29
```bash
curl "http://localhost:8081/garage/revenue?sector=A&date=2025-09-29"
```

sector: opcional; se omitido, retorna faturamento total.

date: obrigatório, formato ISO YYYY-MM-DD.

Exemplo para faturamento total:
```bash
curl "http://localhost:8081/garage/revenue?date=2025-09-29"
```

Exemplo de resposta:

125.0