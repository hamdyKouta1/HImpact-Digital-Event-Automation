import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { VitePWA } from 'vite-plugin-pwa';
import path from 'path';
// https://vite.dev/config/
// See: project-index/10_Deployment_DevOps.md — GitHub Pages hosting
// See: project-index/04_Non_Functional_Requirements.md — NFR-09 Compatibility (PWA)
export default defineConfig({
    plugins: [
        react(),
        VitePWA({
            registerType: 'autoUpdate',
            includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'masked-icon.svg'],
            manifest: {
                name: 'HImpact — Digital Event Automation',
                short_name: 'HImpact',
                description: 'Automate the Event. Preserve the Memories.',
                theme_color: '#0F172A',
                background_color: '#0F172A',
                display: 'standalone',
                orientation: 'portrait',
                scope: '/',
                start_url: '/',
                icons: [
                    {
                        src: 'pwa-192x192.png',
                        sizes: '192x192',
                        type: 'image/png',
                    },
                    {
                        src: 'pwa-512x512.png',
                        sizes: '512x512',
                        type: 'image/png',
                    },
                ],
            },
            workbox: {
                // Cache strategies for offline support
                runtimeCaching: [
                    {
                        urlPattern: /^https:\/\/api\.himpact\.app\/.*/i,
                        handler: 'NetworkFirst',
                        options: {
                            cacheName: 'api-cache',
                            expiration: {
                                maxEntries: 50,
                                maxAgeSeconds: 60 * 60, // 1 hour
                            },
                        },
                    },
                ],
            },
        }),
    ],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, './src'),
        },
    },
    // GitHub Pages deploys to a sub-path by default.
    // When using a custom domain, base should be '/'.
    // Set VITE_BASE_URL in CI to override.
    base: process.env.VITE_BASE_URL || '/',
    build: {
        outDir: 'dist',
        sourcemap: false,
        rollupOptions: {
            output: {
                // Split large vendor chunks for better caching
                manualChunks: {
                    vendor: ['react', 'react-dom', 'react-router-dom'],
                    query: ['@tanstack/react-query'],
                },
            },
        },
    },
    server: {
        port: 5173,
        proxy: {
            // Proxy API calls to backend during development
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
            },
        },
    },
});
