package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	httpdelivery "tiktokcoinmanager/backend/internal/delivery/http"
	"tiktokcoinmanager/backend/internal/infrastructure/config"
	"tiktokcoinmanager/backend/internal/infrastructure/database"
	"tiktokcoinmanager/backend/internal/infrastructure/persistence"
	"tiktokcoinmanager/backend/internal/usecase"
)

func main() {
	cfg := config.Load()

	db, err := database.Open(cfg.DatabaseURL)
	if err != nil {
		log.Fatalf("open database: %v", err)
	}
	defer db.Close()

	transactionRepository := persistence.NewPostgresTransactionRepository(db)
	transactionUseCase := usecase.NewTransactionUseCase(transactionRepository)
	router := httpdelivery.NewRouter(transactionUseCase, cfg.EnableSwagger)

	server := &http.Server{
		Addr:         ":" + cfg.HTTPPort,
		Handler:      router,
		ReadTimeout:  10 * time.Second,
		WriteTimeout: 10 * time.Second,
	}

	go func() {
		log.Printf("backend listening on http://localhost:%s", cfg.HTTPPort)
		if cfg.EnableSwagger {
			log.Printf("swagger ui available at http://localhost:%s/swagger/index.html", cfg.HTTPPort)
		}
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("listen: %v", err)
		}
	}()

	stop := make(chan os.Signal, 1)
	signal.Notify(stop, os.Interrupt, syscall.SIGTERM)
	<-stop

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	if err := server.Shutdown(ctx); err != nil {
		log.Printf("shutdown: %v", err)
	}
}
