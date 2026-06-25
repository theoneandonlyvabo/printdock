.PHONY: run stop db backend frontend logs clean

# Start everything: DB + backend + JavaFX client
run: db backend frontend

db:
	docker compose up -d postgres
	@echo "Waiting for PostgreSQL to be ready..."
	@until docker compose exec postgres pg_isready -U postgres > /dev/null 2>&1; do sleep 1; done
	@echo "PostgreSQL ready."

backend:
	DB_HOST=localhost DB_PORT=5433 DB_NAME=printdock_db DB_USER=postgres DB_PASSWORD=postgres \
	./gradlew bootRun &

frontend:
	cd javafx-client && ./gradlew run

stop:
	docker compose down
	@pkill -f "printdock" || true

logs:
	docker compose logs -f

clean:
	docker compose down -v
	./gradlew clean
	cd javafx-client && ./gradlew clean
