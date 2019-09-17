const jsonServer = require('json-server');
const server = jsonServer.create();
const fs = require('fs');

server.use(jsonServer.defaults());
server.use(jsonServer.bodyParser);

server.post('/api/repositories/sync', (req: any, res: any, next: any) => {
  // mock server delay
  setTimeout(() => res.send(JSON.parse(fs.readFileSync('./json-server/data/repositories-pages-sync.json'))), 1000);
});

server.get('/api/repositories', (req: any, res: any, next: any) => {
  // mock server delay
  setTimeout(() => res.send(getRepositoriesPage(req.query.page)), 1000);
});

server.post('/api/repositories/analyze', (req: any, res: any, next: any) => {
  // mock server delay
  setTimeout(() => res.status(200).jsonp({}), 1000);
});

server.get('/api/issues/backlog', (req: any, res: any, next: any) => {
  // mock server delay
  setTimeout(() => res.send(getIssuesPage()), 1000);
});

server.post('/api/issues/priority', (req: any, res: any, next: any) => {
  // mock server delay
  setTimeout(() => res.status(200).jsonp({}), 1000);
});

server.get('/api/orders/active', (req: any, res: any, next: any) => {
  setTimeout(() => res.send(getOrder()), 1000);
});

server.post('/api/orders/submit', (req: any, res: any, next: any) => {
  setTimeout(() => res.send(getOrder()), 1000);
});

function getOrder() {
  return JSON.parse(fs.readFileSync('./json-server/data/order.json'));
}

function getIssuesPage() {
  return JSON.parse(fs.readFileSync('./json-server/data/issues-list.json'));
}

function getRepositoriesPage(page: number) {
  return JSON.parse(fs.readFileSync('./json-server/data/repositories-pages.json'))[page - 1];
}

server.listen(3000, () => {
  console.log('JSON Server is running');
});
