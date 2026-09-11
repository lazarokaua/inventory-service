run:
	export $(cat .env | xargs) && mvn spring-boot:run
