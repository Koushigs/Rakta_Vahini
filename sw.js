const CACHE_NAME = 'raktavahini-demo-v1';
const PRECACHE_URLS = [
  '/',
  '/Rakta_Vahini/',
  '/Rakta_Vahini/index.html',
  '/Rakta_Vahini/manifest.webmanifest',
  '/Rakta_Vahini/icon.svg'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => cache.addAll(PRECACHE_URLS))
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => Promise.all(
      keys.map((key) => {
        if (key !== CACHE_NAME) return caches.delete(key);
      })
    ))
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  event.respondWith(
    caches.match(event.request).then((resp) => resp || fetch(event.request))
  );
});
