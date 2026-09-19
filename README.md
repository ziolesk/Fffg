# Bei Poa v2 – Campus Marketplace

Premium campus marketplace for Kenyan universities.

## Stack
- HTML / CSS / JavaScript / Bootstrap 5
- Supabase (Auth, Postgres, Realtime)
- Express (Render Web Service)

## Project structure
```
bei-poa/
  package.json
  server.js
  public/
    index.html
    css/app.css
    js/config.js
  sql/
    upgrade-v2.sql
  README.md
```

## Local run
```bash
npm install
npm start
```
Open http://localhost:3000

## Supabase setup
1. Run existing schema (profiles, items, reports) if not already applied.
2. Run `sql/upgrade-v2.sql` for favorites, chat, notifications, views.
3. Authentication → URL Configuration:
   - Site URL: your Render URL
   - Redirect URLs: `https://YOUR-APP.onrender.com/**`
4. Optional: Custom SMTP (Brevo/Resend) under Authentication → SMTP.

## Deploy on Render (Web Service)
1. Push this repo to GitHub.
2. Render → New → Web Service → connect repo.
3. Settings:
   - **Runtime:** Node
   - **Build Command:** `npm install`
   - **Start Command:** `npm start`
4. Deploy.

Environment variables (optional later):
- None required for client anon key (public by design).
- Do **not** put the service_role/secret key in the frontend.

## Features preserved
- Email + Google auth
- Listings + moderation
- Admin / moderator roles
- Reports & bans
- Profiles
- Realtime listing updates

## v2 additions
- Design system + dark mode
- Mobile bottom nav + horizontal top actions
- How it works section
- Campus-focused categories
- SQL for chat, favorites, notifications
- Express server with compression + helmet
