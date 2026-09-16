run:
	export $(cat .env | xargs) && mvn spring-boot:run

bd:
	docker compose up -d

logs:
	docker compose logs -f db