# client (client)

A Quasar Project

## Environment

### Client
&#9989; <strong>Quasar CLI</strong> &#8658; `1.4.3`

&#9989; <strong>Node</strong> &#8658; `18.15.0`

### Server
&#9989; <strong>Java</strong> &#8658; `19`

&#9989; <strong>Maven</strong> &#8658; `3.8.6`

&#9989; <strong>PostgreSQL</strong> &#8658; `12.12`

### DDL
```sql
INSERT INTO diotviet.users (email, name, password, role) VALUES ('ahihi@gmail.com', 'ahihi', '$2a$10$VUa2eprhyThcTvCxbe2jV.uC3wPGOMmk0QsZTLUIrYj77lHYt6haO', 'ROLE_STAFF');

INSERT INTO diotviet.categories (id, name, type) VALUES (1, 'Hàng hóa', 0);
INSERT INTO diotviet.categories (id, name, type) VALUES (2, 'Dịch vụ', 0);
INSERT INTO diotviet.categories (id, name, type) VALUES (3, 'Combo - Đóng gói', 0);
INSERT INTO diotviet.categories (id, name, type) VALUES (4, 'Cá nhân', 2);

INSERT INTO diotviet.groups (id, name, type) VALUES (1, 'Giặt + Sấy + Gấp', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (2, 'GIẶT HẤP', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (3, 'GIẶT SẤY', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (4, 'GIẶT ƯỚT', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (5, 'KHUYẾN MÃI', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (6, 'ỦI', 0);
INSERT INTO diotviet.groups (id, name, type) VALUES (7, 'Thân thiết', 2);
```

## Install the dependencies
```bash
yarn
# or
npm install
```

### Start the app in development mode (hot-code reloading, error reporting, etc.)
```bash
quasar dev
```


### Lint the files
```bash
yarn lint
# or
npm run lint
```


### Format the files
```bash
yarn format
# or
npm run format
```



### Build the app for production
```bash
quasar build
```

### Customize the configuration
See [Configuring quasar.config.js](https://v2.quasar.dev/quasar-cli-vite/quasar-config-js).
