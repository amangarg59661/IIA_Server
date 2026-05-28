const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  app.use(
    "/api",
    createProxyMiddleware({
      target: "http://localhost:8081",
      changeOrigin: true,
      pathRewrite: { "^/api": "/astro-service/api" },
    })
  );
  app.use(
    "/file",
    createProxyMiddleware({
      target: "http://localhost:8081",
      changeOrigin: true,
      pathRewrite: { "^/file": "/astro-service/file" },
    })
  );
};