package config

import "os"

type Config struct {
	HTTPPort      string
	DatabaseURL   string
	EnableSwagger bool
}

func Load() Config {
	return Config{
		HTTPPort:      valueOrDefault("HTTP_PORT", "8080"),
		DatabaseURL:   os.Getenv("DATABASE_URL"),
		EnableSwagger: valueOrDefault("ENABLE_SWAGGER", "true") != "false",
	}
}

func valueOrDefault(key string, fallback string) string {
	value := os.Getenv(key)
	if value == "" {
		return fallback
	}
	return value
}
