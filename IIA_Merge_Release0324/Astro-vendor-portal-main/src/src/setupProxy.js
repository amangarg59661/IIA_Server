const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  app.use(
    "/api",
    createProxyMiddleware({
     // target: "http://103.181.158.220:8081",
      target: "http://localhost:8081/astro-service",
      changeOrigin: true,
      pathRewrite: { "^/api": "/astro-service/api" },
    })
  );
};