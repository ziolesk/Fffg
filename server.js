// Bei Poa - Zero-dependency Standalone Node.js HTTP Server for Render
const http = require('http');
const fs = require('fs');
const path = require('path');
const url = require('url');

const PORT = process.env.PORT || 10000;

let items = [
  {
    id: 'bp-1',
    title: 'Engineering Mathematics by K.A. Stroud (7th Edition)',
    price: 1800,
    category: 'Textbooks',
    condition: 'Like New',
    university: 'University of Nairobi',
    contact: '+254712345678',
    seller_name: 'Collins Kiprop',
    status: 'approved',
    sold: false,
    image_url: 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600',
    description: 'Hardcover, minimal pencil highlights. Clean binding.'
  },
  {
    id: 'bp-2',
    title: 'Casio fx-991EX Scientific Calculator',
    price: 2400,
    category: 'Electronics',
    condition: 'Good',
    university: 'Kenyatta University',
    contact: '+254723456789',
    seller_name: 'Faith Mwangi',
    status: 'approved',
    sold: false,
    image_url: 'https://images.unsplash.com/photo-1611117775350-ac3950990985?w=600',
    description: 'Solar powered ClassWiz with slide-on case. KU Main Gate.'
  },
  {
    id: 'bp-3',
    title: 'Hostel Study Desk & Chair',
    price: 4500,
    category: 'Furniture',
    condition: 'Good',
    university: 'JKUAT',
    contact: '+254734567890',
    seller_name: 'Brian Otieno',
    status: 'approved',
    sold: false,
    image_url: 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=600',
    description: 'Solid wooden desk with side bookshelf. Gate B Juja.'
  }
];

const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  // CORS headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PATCH, DELETE, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    return res.end();
  }

  // API Health
  if (pathname === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify({ status: 'ok', app: 'Bei Poa Campus Marketplace', time: new Date().toISOString() }));
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
          status: 'approved',
          sold: false
        };
        items.unshift(newItem);
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
    res.writeHead(200, { 'Content-Type': 'application/json' });
    return res.end(JSON.stringify({ message: 'Deleted', id }));
  }

  // Serve index.html as fallback for any frontend route
  const indexPath = path.join(__dirname, 'index.html');
  fs.readFile(indexPath, (err, data) => {
    if (err) {
      res.writeHead(500, { 'Content-Type': 'text/plain' });
      return res.end('Error loading index.html');
    }
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
    res.end(data);
  });
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`[BeiPoa Server] Serving live at http://0.0.0.0:${PORT}`);
});
