package http

import (
	_ "embed"
	"net/http"
)

//go:embed swagger_assets/swagger-ui.html
var swaggerHTML string

//go:embed swagger_assets/openapi.yaml
var openAPIYAML string

func serveSwaggerIndex(w http.ResponseWriter, _ *http.Request) {
	w.Header().Set("Content-Type", "text/html; charset=utf-8")
	_, _ = w.Write([]byte(swaggerHTML))
}

func serveOpenAPI(w http.ResponseWriter, _ *http.Request) {
	w.Header().Set("Content-Type", "application/yaml; charset=utf-8")
	_, _ = w.Write([]byte(openAPIYAML))
}
