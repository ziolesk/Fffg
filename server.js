// Bei Poa - Zero-dependency Standalone Node.js HTTP Server for Render
const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = (process.env.PORT && process.env.PORT !== '8080') ? process.env.PORT : 3000;
const DATA_FILE = path.join(__dirname, 'items.json');

// Persistent items storage (starts empty - no fake listings)
let items = [];
try {
  if (fs.existsSync(DATA_FILE)) {
    items = JSON.parse(fs.readFileSync(DATA_FILE, 'utf8'));
  }
} catch (e) {
  items = [];
}

function saveItems() {
  try {
    fs.writeFileSync(DATA_FILE, JSON.stringify(items, null, 2));
  } catch (e) {
    console.error('Failed to save items to disk:', e);
  }
}

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PATCH, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    return res.end();
  }

  // API Health
  if (pathname === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify({ status: 'ok', app: 'Bei Poa Campus Marketplace', port: PORT, time: new Date().toISOString() }));
  }

  // API Items GET
  if (pathname === '/api/items' && req.method === 'GET') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify(items));
  }

  // API Items POST
  if (pathname === '/api/items' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => { body += chunk.toString(); });
    req.on('end', () => {
      try {
        const parsed = JSON.parse(body);
        const newItem = {
          id: 'bp-' + Date.now(),
          title: parsed.title,
          price: Number(parsed.price),
          category: parsed.category,
          condition: parsed.condition || 'Good',
          university: parsed.university || 'University of Nairobi',
          contact: parsed.contact,
          image_url: parsed.image_url || 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600',
          seller_name: parsed.seller_name || 'Campus Student',
          seller_email: parsed.seller_email || '',
          status: 'approved',
          sold: false,
          created_at: new Date().toISOString()
        };
        items.unshift(newItem);
        saveItems();
        res.writeHead(201, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify(newItem));
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Invalid JSON body' }));
      }
    });
    return;
  }

  // API Items DELETE
  if (pathname.startsWith('/api/items/') && req.method === 'DELETE') {
    const id = pathname.replace('/api/items/', '');
    items = items.filter(i => i.id !== id);
    saveItems();
    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify({ message: 'Deleted', id }));
  }

  // Serve static files / index.html
  const filePath = path.join(__dirname, pathname === '/' ? 'index.html' : pathname);
  fs.readFile(filePath, (err, data) => {
    if (err) {
      // Fallback to index.html for single page client side routing
      const indexPath = path.join(__dirname, 'index.html');
      fs.readFile(indexPath, (fallbackErr, indexData) => {
        if (fallbackErr) {
          res.writeHead(500, { 'Content-Type': 'text/plain' });
          return res.end('Error loading index.html');
        }
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
        res.end(indexData);
      });
      return;
    }

    const ext = path.extname(filePath);
    let contentType = 'text/html';
    if (ext === '.js') contentType = 'application/javascript';
    else if (ext === '.css') contentType = 'text/css';
    else if (ext === '.json') contentType = 'application/json';
    else if (ext === '.svg') contentType = 'image/svg+xml';
    else if (ext === '.png') contentType = 'image/png';

    res.writeHead(200, { 'Content-Type': contentType });
    res.end(data);
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[BeiPoa Server] Serving live at http://0.0.0.0:${PORT}`);
});
