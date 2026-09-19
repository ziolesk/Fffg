/**
 * Bei Poa – Render Web Service
 * Serves static files from ./public OR project root (fallback).
 */
const path = require('path');
const fs = require('fs');
const express = require('express');
const compression = require('compression');
const helmet = require('helmet');

const app = express();
const PORT = process.env.PORT || 3000;

const publicDir = path.join(__dirname, 'public');
const rootIndex = path.join(__dirname, 'index.html');
const publicIndex = path.join(publicDir, 'index.html');

// Prefer public/, else project root (if user uploaded index.html at root)
const STATIC_ROOT = fs.existsSync(publicIndex)
  ? publicDir
  : __dirname;

const INDEX_FILE = fs.existsSync(publicIndex)
  ? publicIndex
  : rootIndex;

if (!fs.existsSync(INDEX_FILE)) {
  console.error('FATAL: index.html not found in public/ or project root');
  console.error('Looked for:', publicIndex, 'and', rootIndex);
  console.error('Files in __dirname:', fs.readdirSync(__dirname));
}

app.use(compression());
app.use(
  helmet({
    contentSecurityPolicy: false,
    crossOriginEmbedderPolicy: false,
  })
);

app.use(
  express.static(STATIC_ROOT, {
    maxAge: '1h',
    setHeaders(res, filePath) {
      if (filePath.endsWith('.html')) {
        res.setHeader('Cache-Control', 'no-cache');
      }
    },
  })
);

app.get('*', (req, res) => {
  if (!fs.existsSync(INDEX_FILE)) {
    return res
      .status(500)
      .send(
        'index.html missing. Upload public/index.html (or index.html at repo root) to GitHub and redeploy.'
      );
  }
  res.sendFile(INDEX_FILE);
});

app.listen(PORT, () => {
  console.log('Bei Poa on port', PORT);
  console.log('Static root:', STATIC_ROOT);
  console.log('Index file:', INDEX_FILE);
});
